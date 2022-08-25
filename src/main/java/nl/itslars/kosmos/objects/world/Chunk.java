package nl.itslars.kosmos.objects.world;

import lombok.RequiredArgsConstructor;
import nl.itslars.kosmos.leveldb.LevelDB;
import nl.itslars.kosmos.enums.BlockType;
import nl.itslars.kosmos.enums.Dimension;
import nl.itslars.kosmos.objects.entity.Entity;
import nl.itslars.kosmos.objects.entity.TileEntity;
import nl.itslars.kosmos.util.Chunks;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * Class for representing a Chunk. Note the difference between this class and a {@link SubChunk}.
 * This class represents a 16x16x256 area in a certain dimension. This area is divided into {@link SubChunk}
 * objects, that are stored inside of this class. The class contains chunk information like elevation, biomes
 * entities, tile entities, and of course all blocks.
 */
@RequiredArgsConstructor
public class Chunk {

    // The parent WorldData class that this Chunk is stored in.
    private final WorldData world;
    // The X coordinate of the chunk
    private final int chunkX;
    // The Z coordinate of the chunk
    private final int chunkZ;
    // The chunk dimension
    private final Dimension dimension;
    // Terrain loader
    private final BiConsumer<LevelDB, Chunk> terrainLoader;
    // Entity loader
    private final BiConsumer<LevelDB, Chunk> entitiesLoader;
    // Data 2D loader
    private final BiConsumer<LevelDB, Chunk> data2DLoader;

    private boolean terrainLoaded = false;
    private boolean entitiesLoaded = false;
    private boolean data2DLoaded = false;

    // The 2d elevation map, that contains the maximum height of each x/z combination, plus 1.
    // TODO: When a chunk has been changed, the elevation should be updated. This is not currently done yet.
    private final short[][] elevation = new short[16][16];
    // The 2d biome map, that contains the biome for each x/z combination, represented as a number.
    private final byte[][] biomes = new byte[16][16];

    // The set of entities inside this chunk
    private final Set<Entity> entities = new HashSet<>();
    // The set of tile entities inside this chunk
    private final Set<TileEntity> tileEntities = new HashSet<>();

    // The SubChunk map. Each chunk height is mapped to the corresponding SubChunk.
    private final Map<Short, SubChunk> subChunks = new HashMap<>();

    /**
     * Make sure the terrain is loaded before accessing it.
     */
    private void ensureTerrainLoaded() {
        if (terrainLoaded) {
            return;
        }
        // Mark as loaded before executing the loader to avoid stack overflow error.
        terrainLoaded = true;
        // Load the terrain
        terrainLoader.accept(world.getWorld().getDb(), this);
    }

    /**
     * Make sure the entities are loaded before accessing it.
     */
    private void ensureEntitiesLoaded() {
        if (entitiesLoaded) {
            return;
        }
        // Mark as loaded before executing the loader to avoid stack overflow error.
        entitiesLoaded = true;
        // Load the entities
        entitiesLoader.accept(world.getWorld().getDb(), this);
    }

    /**
     * Make sure the 2D data is loaded before accessing it.
     */
    private void ensureData2DLoaded() {
        if (data2DLoaded) {
            return;
        }
        // Mark as loaded before executing the loader to avoid stack overflow error.
        data2DLoaded = true;
        // Load the 2D data
        data2DLoader.accept(world.getWorld().getDb(), this);
    }

    /**
     * Retrieves the block that is at the given in-chunk coordinates. If the block didn't exist (because there was
     * no {@link SubChunk} there), an empty {@link Optional} is returned.
     *
     * @param translatedX The translated X coordinate (the 'local' x coordinate, ranging from 0-15) of the block
     * @param y           The y coordinate of the block
     * @param translatedZ The translated Z coordinate (the 'local' z coordinate, ranging from 0-15) of the block
     * @return An {@link Optional} containing the block if present, empty otherwise.
     */
    public Optional<Block> getBlock(int translatedX, int y, int translatedZ) {
        ensureTerrainLoaded();
        // Get the SubChunk Y
        short chunkY = (short) (y >> 4);
        // If the SubChunk is not present, return an empty optional
        if (!subChunks.containsKey(chunkY)) {
            return Optional.empty();
        }
        // Return the block that is at the given coordinates
        return Optional.ofNullable(subChunks.get(chunkY).getBlocks()[translatedX][y - (16 * chunkY)][translatedZ]);
    }

    /**
     * Sets a block at the given in-chunk coordinates. If the block was successfully created, the block is returned.
     * If the block could not be created, an empty {@link Optional} is returned.
     *
     * @param translatedX The translated X coordinate (the 'local' x coordinate, ranging from 0-15) of the block
     * @param y           The y coordinate of the block
     * @param translatedZ The translated Z coordinate (the 'local' z coordinate, ranging from 0-15) of the block
     * @param blockType   The block type to place
     * @return An {@link Optional} containing the resulting block if present, empty otherwise.
     */
    public Optional<Block> setBlock(int translatedX, int y, int translatedZ, BlockType blockType) {
        return setBlock(translatedX, y, translatedZ, blockType.name());
    }

    /**
     * Sets a block at the given in-chunk coordinates. If the block was successfully created, the block is returned.
     * If the block could not be created, an empty {@link Optional} is returned.
     *
     * @param translatedX The translated X coordinate (the 'local' x coordinate, ranging from 0-15) of the block
     * @param y           The y coordinate of the block
     * @param translatedZ The translated Z coordinate (the 'local' z coordinate, ranging from 0-15) of the block
     * @param name        The name of the block type to place
     * @return An {@link Optional} containing the resulting block if present, empty otherwise.
     */
    public Optional<Block> setBlock(int translatedX, int y, int translatedZ, String name) {
        ensureTerrainLoaded();
        // Get the SubChunk Y
        short chunkY = (short) (y >> 4);
        // Make sure all chunks up to and including chunkY are created
        ensureChunkSpace(chunkY);
        // Retrieve the SubChunk
        SubChunk subChunk = subChunks.get(chunkY);
        // If the SubChunk was not null, create, set, and return the block
        if (subChunk != null) {
            Block result = new Block(name, translatedX + 16 * chunkX, y, translatedZ + 16 * chunkZ);
            subChunk.getBlocks()[translatedX][y - (16 * chunkY)][translatedZ] = result;
            return Optional.of(result);
        }
        return Optional.empty();
    }

    /**
     * Make sure all chunks up to and including the desired chunk height are created.
     * The desired chunk height may be at most 15, because the world height limit is at 256 blocks.
     *
     * @param desiredChunkHeight The desired chunk height
     */
    public void ensureChunkSpace(int desiredChunkHeight) {
        ensureTerrainLoaded();
        // Loop through all chunk heights. If the SubChunk did not yet exist, create a new SubChunk
        for (short currentY = 0; currentY <= Math.min(desiredChunkHeight, 15); currentY++) {
            if (!subChunks.containsKey(currentY)) {
                subChunks.put(currentY, createNewSubChunk(currentY));
            }
        }
    }

    /**
     * Creates and initializes a new {@link SubChunk} for the current chunk
     *
     * @param chunkY The height of the {@link SubChunk}
     * @return The newly create {@link SubChunk}
     */
    private SubChunk createNewSubChunk(short chunkY) {
        // Create and fill a new blocks array with air
        Block[][][] blocks = new Block[16][16][16];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    blocks[x][y][z] = new Block(BlockType.AIR, x + 16 * chunkX, y + 16 * chunkY, z + 16 * chunkZ);
                }
            }
        }
        return new SubChunk(this, chunkY, blocks);
    }

    /**
     * Loops through all blocks in the chunk, and applies the function to those blocks.
     * The block resets to the given function result.
     *
     * @param function The function, returns the new block position
     */
    public void forEachBlock(UnaryOperator<Block> function) {
        ensureTerrainLoaded();
        for (SubChunk subChunk : subChunks.values()) {
            Block[][][] blocks = subChunk.getBlocks();
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        blocks[x][y][z] = function.apply(blocks[x][y][z]);
                    }
                }
            }
        }
    }

    /**
     * Retrieves the X coordinate of this chunk
     *
     * @return The X coordinate
     * @deprecated Use {@link #getChunkX()} instead
     */
    @Deprecated
    public int getX() {
        return chunkX;
    }


    /**
     * Retrieves the Z coordinate of this chunk
     *
     * @return The Z coordinate
     * @deprecated Use {@link #getChunkZ()} instead
     */
    @Deprecated
    public int getZ() {
        return chunkZ;
    }

    /**
     * Saves the chunk to the Minecraft Bedrock LevelDB storage
     */
    public void save() {
        Chunks.saveChunk(world.getWorld().getDb(), this, terrainLoaded, entitiesLoaded, data2DLoaded);
    }

    /**
     * Unloads the current chunk from the cached chunk map in the {@link WorldData} chunk storage
     */
    public void unload() {
        unload(false);
    }

    /**
     * Unloads the current chunk from the cached chunk map in the {@link WorldData} chunk storage
     *
     * @param save Whether the chunk should be saved before it is unloaded
     */
    public void unload(boolean save) {
        if (save) {
            save();
        }

        // Retrieve the (sub)map containing this chunk, and remove this chunk from that map
        Map<Integer, Chunk> zs = world.getCachedChunks().get(dimension).get(chunkX);
        // If chunk is deleted, zs might be null
        if (zs != null) {
            zs.remove(chunkZ);
            // If the new map is empty, remove it entirely from the chunk map
            if (zs.size() == 0) {
                world.getCachedChunks().get(dimension).remove(chunkX);
            }
        }
    }

    /**
     * Deletes the current chunk from the world
     */
    public void delete() {
        world.deleteChunk(getDimension(), getChunkX(), getChunkZ());
    }

    public WorldData getWorld() {
        return this.world;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public short[][] getElevation() {
        ensureData2DLoaded();
        return this.elevation;
    }

    public byte[][] getBiomes() {
        ensureData2DLoaded();
        return this.biomes;
    }

    public Set<Entity> getEntities() {
        ensureEntitiesLoaded();
        return this.entities;
    }

    public Set<TileEntity> getTileEntities() {
        ensureTerrainLoaded();
        return this.tileEntities;
    }

    public Map<Short, SubChunk> getSubChunks() {
        ensureTerrainLoaded();
        return this.subChunks;
    }
}
