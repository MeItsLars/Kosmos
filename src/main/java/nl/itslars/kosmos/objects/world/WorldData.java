package nl.itslars.kosmos.objects.world;

import lombok.Getter;
import lombok.SneakyThrows;
import nl.itslars.kosmos.World;
import nl.itslars.kosmos.enums.BlockType;
import nl.itslars.kosmos.enums.Dimension;
import nl.itslars.kosmos.objects.entity.Player;
import nl.itslars.kosmos.objects.settings.LevelDatFile;
import nl.itslars.kosmos.util.Chunks;
import nl.itslars.mcpenbt.NBTUtil;
import nl.itslars.mcpenbt.enums.HeaderType;
import nl.itslars.mcpenbt.tags.CompoundTag;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Class for representing the following data of a Minecraft world:
 * - Players
 * - Player Pointers (weird form of player data of which I don't know what it's used for
 * - A map, containing all chunks that Minecraft has generated
 * - A map, containing all cached chunks that have been loaded at one point in the past
 * - The world's level.dat file data
 */
public class WorldData implements Closeable {

    // Maps Players to their corresponding player_server_X id, or ~localhost if localhost player
    private final Map<Player, byte[]> players = new HashMap<>();
    // Maps player_x pointers to their corresponding player_server_X id
    private final Map<byte[], byte[]> playerPointers = new HashMap<>();
    // List of keys that are scheduled to be removed with the next world save
    private final Set<byte[]> deletionKeys = new HashSet<>();
    // Map containing all chunks that Minecraft has generated. FORMAT: <Dimension, <X, <Z, CHUNK>>>
    @Getter
    private final Map<Dimension, Map<Integer, Map<Integer, ChunkPreset>>> chunkPresets = new EnumMap<>(Dimension.class);
    // Map containing all cached chunks that have been loaded. FORMAT: <Dimension, <X, <Z, CHUNK>>>
    @Getter
    private final Map<Dimension, Map<Integer, Map<Integer, Chunk>>> cachedChunks = new EnumMap<>(Dimension.class);
    // The parent World file, that contains all interaction with the LevelDB storage
    @Getter
    private final World world;
    // The world's level.dat file data
    @Getter
    private final LevelDatFile levelDatFile;

    public WorldData(World world, File levelDat) {
        this.world = world;
        this.levelDatFile = new LevelDatFile(levelDat, (CompoundTag) NBTUtil.read(true, levelDat.toPath()));
        // Initialize dimensions in the chunk maps
        Stream.of(Dimension.values()).forEach(dim -> {
            chunkPresets.put(dim, new HashMap<>());
            cachedChunks.put(dim, new ConcurrentHashMap<>());
        });
    }

    /**
     * Saves the world data to the LevelDB storage.
     */
    @SneakyThrows
    public void save() {
        // Save all chunks
        saveChunks();
        // Saving all player data:
        players.forEach((key, value) -> {
            byte[] playerData = NBTUtil.write(key.getParentCompoundTag());
            world.getDb().put(value, playerData);
        });
        // Deleting all scheduled deletion keys:
        for (byte[] key : deletionKeys) {
            world.getDb().delete(key);
        }
        deletionKeys.clear();
        // Delete the level.dat file, then save it
        Files.delete(levelDatFile.getFile().toPath());
        // Serialize and save the level.dat file
        Files.write(levelDatFile.getFile().toPath(), NBTUtil.write(levelDatFile.getParentCompoundTag(), HeaderType.LEVEL_DAT));
    }

    /**
     * Save all cached chunks in {@link #cachedChunks} to the LevelDB storage
     */
    public void saveChunks() {
        // Saving all chunks:
        cachedChunks.forEach((d, xzc) -> xzc.forEach((x, zc) -> zc.forEach((z, chunk) -> Chunks.saveChunk(world.getDb(), chunk))));
    }

    /**
     * Closes the world's LevelDB opening state. This method should ALWAYS be called after a world has been opened!
     * A try-with-resources is recommend to open the world.
     *
     * @throws IOException Thrown when the closing has failed (due to e.g. deleted files)
     */
    @Override
    public void close() throws IOException {
        unloadChunks();
        world.close();
    }

    // ==============================================================
    //                 WORLD MODIFICATION METHODS
    // ==============================================================

    /**
     * Gets the OVERWORLD chunk at the given chunk X and Z
     *
     * @param chunkX The chunk X
     * @param chunkZ The chunk Z
     * @return An optional containing the chunk if it was generated, otherwise empty
     */
    public Optional<Chunk> getChunk(int chunkX, int chunkZ) {
        return getChunk(Dimension.OVERWORLD, chunkX, chunkZ);
    }


    /**
     * Gets the chunk at the given dimension and chunk X and Z
     *
     * @param dimension The chunk dimension
     * @param chunkX    The chunk X
     * @param chunkZ    The chunk Z
     * @return An optional containing the chunk if it was generated, otherwise empty
     */
    public Optional<Chunk> getChunk(Dimension dimension, int chunkX, int chunkZ) {
        // Check if the chunk was cached (caching chunks saves a LOT of time on big world operations)
        // If it was cached, return the cached chunk
        Map<Integer, Chunk> zChunks = cachedChunks.get(dimension).get(chunkX);
        if (zChunks != null) {
            Chunk chunk = zChunks.get(chunkZ);
            if (chunk != null) {
                return Optional.of(chunk);
            }
        }

        // If the chunk was not cached, we check if Minecraft has actually already generated this chunk.
        // If the requested chunk was NOT generated by Minecraft, an empty optional is returned
        Map<Integer, ChunkPreset> zChunkPresets = chunkPresets.get(dimension).get(chunkX);
        if (zChunkPresets == null) return Optional.empty();
        ChunkPreset chunkPreset = zChunkPresets.get(chunkZ);
        if (chunkPreset == null) return Optional.empty();

        // If the chunk was generated by Minecraft, load it from the LevelDB database, cache it, and then return it.
        Chunk chunk = Chunks.loadChunk(world.getDb(), chunkPreset);
        cachedChunks.get(dimension).computeIfAbsent(chunkX, x -> new ConcurrentHashMap<>())
                .put(chunkZ, chunk);
        return Optional.of(chunk);
    }

    /**
     * Loops through all chunk presets, loads them, applies the predicate,
     * then unloads them and, if necessary, saves them
     *
     * @param predicate The predicate. Returns whether the chunk should be saved or not
     */
    public void forEachChunk(Predicate<Chunk> predicate) {
        for (Dimension dimension : Dimension.values()) {
            forEachChunk(dimension, predicate);
        }
    }

    /**
     * Loops through all chunk presets in the given dimension, loads them, applies the predicate,
     * then unloads them and, if necessary, saves them
     *
     * @param dimension The dimension
     * @param predicate The predicate. Returns whether the chunk should be saved or not
     */
    public void forEachChunk(Dimension dimension, Predicate<Chunk> predicate) {
        getChunkPresets().get(dimension).entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .map(Map.Entry::getValue).forEach(preset -> {
            getChunk(preset.getDimension(), preset.getX(), preset.getZ()).ifPresent(chunk -> {
                boolean shouldSave = predicate.test(chunk);
                chunk.unload(shouldSave);
            });
        });
    }

    /**
     * Unloads all cached chunks.
     * When performing big world operations, this might be required (occasionally) to prevent Java heap out of memory errors
     */
    public void unloadChunks() {
        cachedChunks.get(Dimension.OVERWORLD).clear();
        cachedChunks.get(Dimension.NETHER).clear();
        cachedChunks.get(Dimension.END).clear();
    }

    /**
     * Retrieves the block in the OVERWORLD at the given coordinates
     *
     * @param x The block X
     * @param y the block Y
     * @param z the block Z
     * @return An optional containing the block if present, empty otherwise
     */
    public Optional<Block> getBlock(int x, int y, int z) {
        return getBlock(Dimension.OVERWORLD, x, y, z);
    }

    /**
     * Retrieves the block at the given coordinates, in the given dimension
     *
     * @param dimension The block dimension
     * @param x         The block X
     * @param y         the block Y
     * @param z         the block Z
     * @return An optional containing the block if present, empty otherwise
     */
    public Optional<Block> getBlock(Dimension dimension, int x, int y, int z) {
        // Retrieve the block's chunk X and Z
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        // Attempt to load the chunk. If it did not exist, return an empty optional.
        // Otherwise, return the given block in the chunk
        Optional<Chunk> chunkOptional = getChunk(dimension, chunkX, chunkZ);
        if (!chunkOptional.isPresent()) return Optional.empty();
        return chunkOptional.get().getBlock(x - (16 * chunkX), y, z - (16 * chunkZ));
    }

    /**
     * Sets a block in the OVERWORLD at the given location
     *
     * @param x         The block X
     * @param y         The block Y
     * @param z         The block Z
     * @param blockType The {@link BlockType}
     * @return An optional containing the block if it was successfully set, empty otherwise
     */
    public Optional<Block> setBlock(int x, int y, int z, BlockType blockType) {
        return setBlock(Dimension.OVERWORLD, x, y, z, blockType);
    }

    /**
     * Sets a block in the OVERWORLD at the given location
     *
     * @param x    The block X
     * @param y    The block Y
     * @param z    The block Z
     * @param name The block name
     * @return An optional containing the block if it was successfully set, empty otherwise
     */
    public Optional<Block> setBlock(int x, int y, int z, String name) {
        return setBlock(Dimension.OVERWORLD, x, y, z, name);
    }

    /**
     * Sets a block in given location and dimension
     *
     * @param dimension The block dimension
     * @param x         The block X
     * @param y         The block Y
     * @param z         The block Z
     * @param blockType The {@link BlockType}
     * @return An optional containing the block if it was successfully set, empty otherwise
     */
    public Optional<Block> setBlock(Dimension dimension, int x, int y, int z, BlockType blockType) {
        return setBlock(dimension, x, y, z, blockType.getNameSpacedId());
    }

    /**
     * Sets a block in given location and dimension
     *
     * @param dimension The block dimension
     * @param x         The block X
     * @param y         The block Y
     * @param z         The block Z
     * @param name      The block name
     * @return An optional containing the block if it was successfully set, empty otherwise
     */
    public Optional<Block> setBlock(Dimension dimension, int x, int y, int z, String name) {
        // Retrieve the block's chunk X and Z
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        // Attempt to load the chunk. If it did not exist, return an empty optional.
        // Otherwise, set the block at the location, and return it
        Optional<Chunk> chunkOptional = getChunk(dimension, chunkX, chunkZ);
        if (!chunkOptional.isPresent()) return Optional.empty();
        return chunkOptional.get().setBlock(x - (16 * chunkX), y, z - (16 * chunkZ), name);
    }

    /**
     * Fills an entire area of blocks in the OVERWORLD, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param x         The starting block X
     * @param y         The starting block Y
     * @param z         The starting block Z
     * @param x2        The ending block X
     * @param y2        The ending block Y
     * @param z2        The ending block Z
     * @param blockType The Block Type to fill
     */
    public void fill(int x, int y, int z, int x2, int y2, int z2, BlockType blockType) {
        fill(Dimension.OVERWORLD, x, y, z, x2, y2, z2, blockType.getNameSpacedId());
    }

    /**
     * Fills an entire area of blocks in the OVERWORLD, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param x    The starting block X
     * @param y    The starting block Y
     * @param z    The starting block Z
     * @param x2   The ending block X
     * @param y2   The ending block Y
     * @param z2   The ending block Z
     * @param name The name of the block to fill
     */
    public void fill(int x, int y, int z, int x2, int y2, int z2, String name) {
        fill(Dimension.OVERWORLD, x, y, z, x2, y2, z2, name);
    }

    /**
     * Fills an entire area of blocks in the given dimension, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param dimension The fill dimension
     * @param x         The starting block X
     * @param y         The starting block Y
     * @param z         The starting block Z
     * @param x2        The ending block X
     * @param y2        The ending block Y
     * @param z2        The ending block Z
     * @param blockType The Block Type to fill
     */
    public void fill(Dimension dimension, int x, int y, int z, int x2, int y2, int z2, BlockType blockType) {
        fill(dimension, x, y, z, x2, y2, z2, blockType.getNameSpacedId());
    }

    /**
     * Fills an entire area of blocks in the given dimension, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param dimension The fill dimension
     * @param x         The starting block X
     * @param y         The starting block Y
     * @param z         The starting block Z
     * @param x2        The ending block X
     * @param y2        The ending block Y
     * @param z2        The ending block Z
     * @param name      The name of the block to fill
     */
    public void fill(Dimension dimension, int x, int y, int z, int x2, int y2, int z2, String name) {
        // Convert the coordinates to a min/max list
        int xMin = Math.min(x, x2);
        int xMax = Math.max(x, x2);
        int yMin = Math.min(y, y2);
        int yMax = Math.max(y, y2);
        int zMin = Math.min(z, z2);
        int zMax = Math.max(z, z2);

        // Loop through all blocks, and set the block at that position
        int minChunkX = xMin >> 4;
        int maxChunkX = xMax >> 4;
        int minChunkZ = zMin >> 4;
        int maxChunkZ = zMax >> 4;
        for (int currentChunkX = minChunkX; currentChunkX <= maxChunkX; currentChunkX++) {
            for (int currentChunkZ = minChunkZ; currentChunkZ <= maxChunkZ; currentChunkZ++) {
                getChunk(dimension, currentChunkX, currentChunkZ).ifPresent(chunk -> {
                    chunk.ensureChunkSpace(yMax >> 4);
                    chunk.forEachBlock(block -> {
                        if (block.getX() >= xMin && block.getX() <= xMax
                                && block.getY() >= yMin && block.getY() <= yMax
                                && block.getZ() >= zMin && block.getZ() <= zMax) {
                            block = new Block(name, block.getX(), block.getY(), block.getZ());
                        }
                        return block;
                    });
                    chunk.unload(true);
                });
            }
        }
    }

    /**
     * Replaces an entire area of blocks in the OVERWORLD, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param x      The starting block X
     * @param y      The starting block Y
     * @param z      The starting block Z
     * @param x2     The ending block X
     * @param y2     The ending block Y
     * @param z2     The ending block Z
     * @param source The Block Type that is to be replaced
     * @param target The Block Type that will be placed
     */
    public void replace(int x, int y, int z, int x2, int y2, int z2, BlockType source, BlockType target) {
        replace(Dimension.OVERWORLD, x, y, z, x2, y2, z2, source.getNameSpacedId(), target.getNameSpacedId());
    }

    /**
     * Replaces an entire area of blocks in the OVERWORLD, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param x      The starting block X
     * @param y      The starting block Y
     * @param z      The starting block Z
     * @param x2     The ending block X
     * @param y2     The ending block Y
     * @param z2     The ending block Z
     * @param source The name of the block that is to be replaced
     * @param target The name of the block that will be placed
     */
    public void replace(int x, int y, int z, int x2, int y2, int z2, String source, String target) {
        replace(Dimension.OVERWORLD, x, y, z, x2, y2, z2, source, target);
    }

    /**
     * Replaces an entire area of blocks in the given dimension, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param dimension The replacement dimension
     * @param x         The starting block X
     * @param y         The starting block Y
     * @param z         The starting block Z
     * @param x2        The ending block X
     * @param y2        The ending block Y
     * @param z2        The ending block Z
     * @param source    The Block Type that is to be replaced
     * @param target    The Block Type that will be placed
     */
    public void replace(Dimension dimension, int x, int y, int z, int x2, int y2, int z2, BlockType source, BlockType target) {
        replace(dimension, x, y, z, x2, y2, z2, source.getNameSpacedId(), target.getNameSpacedId());
    }

    /**
     * Replaces an entire area of blocks in the given dimension, between the given coordinates.
     * Note that this method is relatively slow for larger areas. It may also cause Java heap out of memory errors.
     * A faster (possibly multi-threaded) solution may be created in the future. If you create one yourself, please
     * create a PR on GitHub so people can use it!
     *
     * @param dimension The replacement dimension
     * @param x         The starting block X
     * @param y         The starting block Y
     * @param z         The starting block Z
     * @param x2        The ending block X
     * @param y2        The ending block Y
     * @param z2        The ending block Z
     * @param source    The name of the block that is to be replaced
     * @param target    The name of the block that will be placed
     */
    public void replace(Dimension dimension, int x, int y, int z, int x2, int y2, int z2, String source, String target) {
        // Convert the coordinates to a min/max list
        int xMin = Math.min(x, x2);
        int xMax = Math.max(x, x2);
        int yMin = Math.min(y, y2);
        int yMax = Math.max(y, y2);
        int zMin = Math.min(z, z2);
        int zMax = Math.max(z, z2);

        // Loop through all blocks, and set the block at that position
        int minChunkX = xMin >> 4;
        int maxChunkX = xMax >> 4;
        int minChunkZ = zMin >> 4;
        int maxChunkZ = zMax >> 4;
        for (int currentChunkX = minChunkX; currentChunkX <= maxChunkX; currentChunkX++) {
            for (int currentChunkZ = minChunkZ; currentChunkZ <= maxChunkZ; currentChunkZ++) {
                getChunk(dimension, currentChunkX, currentChunkZ).ifPresent(chunk -> {
                    chunk.ensureChunkSpace(yMax >> 4);
                    chunk.forEachBlock(block -> {
                        if (block.getX() >= xMin && block.getX() <= xMax
                                && block.getY() >= yMin && block.getY() <= yMax
                                && block.getZ() >= zMin && block.getZ() <= zMax
                                && block.getName().equals(source)) {
                            block = new Block(target, block.getX(), block.getY(), block.getZ());
                        }
                        return block;
                    });
                    chunk.unload(true);
                });
            }
        }
    }

    // ==============================================================
    //               PLAYER DATA MODIFICATION METHODS
    // ==============================================================

    /**
     * Retrieves the set of all loaded players
     *
     * @return The set containing all loaded players
     */
    public Set<Player> getPlayers() {
        return players.keySet();
    }

    /**
     * Adds a player with the given player key (~local_player or player_server_?)
     *
     * @param player The player entity object
     * @param key    The player LevelDB key
     */
    public void addPlayer(Player player, byte[] key) {
        players.put(player, key);
    }

    /**
     * Adds a player pointer to the player pointer list. I have no clue what these are used for, but they are
     * player data, so I'm just storing them.
     *
     * @param pointerKey    The pointer key (player_x)
     * @param pointerTarget The target player (player_server_x)
     */
    public void addPlayerPointer(byte[] pointerKey, byte[] pointerTarget) {
        playerPointers.put(pointerKey, pointerTarget);
    }

    /**
     * Deletes a player and the associated pointer(s) from the player data, and adds their keys to the
     * deletionKeys array, that is used when the world is saved.
     *
     * @param player The Player object
     */
    public void deletePlayer(Player player) {
        // Retrieve the key
        byte[] key = players.get(player);
        // Add all associated pointers to the scheduled deletion list
        playerPointers.forEach((key1, value) -> {
            if (Arrays.equals(value, key)) {
                deletionKeys.add(key1);
            }
        });
        // Delete all associated pointers from the pointer map
        playerPointers.entrySet().removeIf(entry -> Arrays.equals(entry.getValue(), key));
        // Remove the player from the player map
        players.remove(player);
        // Add the key to the ascheduled deletion list
        deletionKeys.add(key);
    }

    /**
     * Deletes the player data and pointers from all currently loaded players.
     */
    public void deleteAllPlayers() {
        // Adds all player keys and pointer keys to the scheduled deletion list
        deletionKeys.addAll(players.values());
        deletionKeys.addAll(playerPointers.keySet());
        // Clear data
        players.clear();
        playerPointers.clear();
    }
}
