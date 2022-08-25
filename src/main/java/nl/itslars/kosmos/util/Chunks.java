package nl.itslars.kosmos.util;

import lombok.SneakyThrows;
import nl.itslars.kosmos.leveldb.LevelDB;
import nl.itslars.kosmos.enums.Dimension;
import nl.itslars.kosmos.objects.entity.Entity;
import nl.itslars.kosmos.objects.entity.TileEntity;
import nl.itslars.kosmos.objects.world.Chunk;
import nl.itslars.kosmos.objects.world.ChunkPreset;
import nl.itslars.kosmos.objects.world.SerializedSubChunk;
import nl.itslars.kosmos.objects.world.SubChunk;
import nl.itslars.mcpenbt.NBTUtil;
import nl.itslars.mcpenbt.tags.CompoundTag;
import nl.itslars.mcpenbt.tags.Tag;
import org.apache.commons.compress.utils.BitInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for saving and loading chunks to and from the LevelDB storage
 */
public class Chunks {

    private Chunks() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a list of keys to delete from LevelDB to delete a chunk
     * @param preset The chunk preset to delete
     * @return list of keys to delete
     */
    public static List<byte[]> getDeletionKeys(ChunkPreset preset) {
        ArrayList<byte[]> result = new ArrayList<>();
        // Remove ALL keys related to the chunk (along with legacy ones)
        for (int i = 44; i <= 59; i++) {
            if (i != 47) {
                result.add(generateLevelDBKey(preset.getX(), preset.getZ(), preset.getDimension(), (byte) i, (byte) 0));
            } else {
                // SubChunks
                for (byte subChunkHeight = 0; subChunkHeight < 16; subChunkHeight++) {
                    result.add(generateLevelDBKey(preset.getX(), preset.getZ(), preset.getDimension(), (byte) 47, subChunkHeight));
                }
            }
        }
        result.add(generateLevelDBKey(preset.getX(), preset.getZ(), preset.getDimension(), (byte) 118, (byte) 0));
        return result;
    }

    /**
     * Loads a chunk from the given preset from the LevelDB storage
     * @param preset The chunk preset
     * @return The newly loaded chunks
     */
    public static Chunk loadChunk(ChunkPreset preset) {
        // Create a new chunk instance
        return new Chunk(preset.getWorld(), preset.getX(), preset.getZ(), preset.getDimension(),
                (db, chunk) -> {
            loadChunkTileEntities(db, chunk);
            loadChunkSubChunks(db, chunk);
            linkTileEntities(chunk);
        }, Chunks::loadChunkEntities, Chunks::loadChunkData2D);
    }

    /**
     * Load the 2D elevation and biome data into the chunk
     * @param db The LevelDB storage
     * @param preset The chunk object
     */
    private static void loadChunkData2D(LevelDB db, Chunk preset) {
        // Generate the level DB key
        byte[] levelDBKey = generateLevelDBKey(preset.getChunkX(), preset.getChunkZ(), preset.getDimension(), (byte) 45, (byte) 0);
        boolean oldFormat = db.has(levelDBKey);
        byte[] value = null;
        // Check for new format
        if (oldFormat) {
            value = db.get(levelDBKey);
        }
        else {
            levelDBKey = generateLevelDBKey(preset.getChunkX(), preset.getChunkZ(), preset.getDimension(), (byte) 43, (byte) 0);
            if (db.has(levelDBKey)) {
                value = db.get(levelDBKey);
            }
        }
        // When both are absent, skip the loading
        if (value == null) {
            return;
        }

        // Loop through the 2d chunk, and set the chunk's elevation and biomes accordingly
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int elevationIndex = 2 * (x + z * 16);
                int biomeIndex = 512 + x + z * 16;
                preset.getElevation()[x][z] = (short) (value[elevationIndex + 1] << 8 | value[elevationIndex] & 0xFF);
                // In new format, the elevation is stored as absolute value, so we need to offset it to get an actual Y value
                // TODO: Implement loading biomes in new format
                if (!oldFormat) {
                    preset.getElevation()[x][z] -= 64;
                } else {
                    preset.getBiomes()[x][z] = value[biomeIndex];
                }
            }
        }
    }

    /**
     * Load all tile entities that are stored for this chunk, into the chunk
     * @param db The LevelDB storage
     * @param preset The chunk object
     */
    @SneakyThrows
    private static void loadChunkTileEntities(LevelDB db, Chunk preset) {
        // Generate the level DB key
        byte[] levelDBKey = generateLevelDBKey(preset.getChunkX(), preset.getChunkZ(), preset.getDimension(), (byte) 49, (byte) 0);
        // Return if no tile entities exist for this chunk
        if (!db.has(levelDBKey)) {
            return;
        }
        byte[] value = db.get(levelDBKey);

        // Loop through and parse all tile entities that the value array contains.
        InputStream stream = new ByteArrayInputStream(value);
        while (stream.available() != 0) {
            CompoundTag entity = (CompoundTag) NBTUtil.read(false, stream);
            preset.getTileEntities().add(Entities.createTileEntity(entity));
        }
    }

    /**
     * Load all entities that are stored for this chunk, into the chunk
     * @param db The LevelDB storage
     * @param preset The chunk object
     */
    @SneakyThrows
    private static void loadChunkEntities(LevelDB db, Chunk preset) {
        // Generate the level DB key
        byte[] levelDBKey = generateLevelDBKey(preset.getChunkX(), preset.getChunkZ(), preset.getDimension(), (byte) 50, (byte) 0);
        // Return if no entities exist for this chunk
        if (!db.has(levelDBKey)) {
            return;
        }
        byte[] value = db.get(levelDBKey);

        // Loop through and parse all entities that the value array contains.
        InputStream stream = new ByteArrayInputStream(value);
        while (stream.available() != 0) {
            CompoundTag entity = (CompoundTag) NBTUtil.read(false, stream);
            preset.getEntities().add(Entities.createEntity(entity));
        }
    }

    /**
     * Most difficult part of chunk storage, the SubChunks. Relevant storage information is at:
     * {@see <a href="https://minecraft.gamepedia.com/Bedrock_Edition_level_format">Bedrock Level Format</a>}
     * Loads all SubChunks that are stored for this chunk, into the chunk
     * @param db The LevelDB storage
     * @param preset The chunk object
     */
    private static void loadChunkSubChunks(LevelDB db, Chunk preset) {
        // Loop through all possible subchunks
        for (byte subChunkHeight = -4; subChunkHeight < 16; subChunkHeight++) {
            // Generate the level DB key
            byte[] levelDBKey = generateLevelDBKey(preset.getChunkX(), preset.getChunkZ(), preset.getDimension(), (byte) 47, subChunkHeight);

            // If the value didn't exist, the top subchunk is reached, and we can stop.
            if (!db.has(levelDBKey)) {
                continue;
            }
            byte[] value = db.get(levelDBKey);

            // Create a new InputStream, containing the value data
            try (InputStream inputStream = new ByteArrayInputStream(value)) {
                // Read the subchunk version
                int version = inputStream.read();
                // Read the amount of storage sections in this subchunk (1 default, 2 for water logging)
                int storageCount = 1;
                if (version >= 8) {
                    storageCount = inputStream.read();
                }
                if (version >= 9) {
                    inputStream.read();
                }
                SubChunk resultingSubChunk = null;
                // Loop through all storage sections
                for (int blockStorage = 0; blockStorage < storageCount; blockStorage++) {
                    // Read the storage version
                    byte storageVersion = (byte) inputStream.read();
                    // Parse the bits per block
                    int bitsPerBlock = storageVersion >> 1;
                    // Parse the blocks per word
                    int blocksPerWord = (int) Math.floor(32.0 / bitsPerBlock);
                    // Find the amount of words
                    int wordCount = (int) Math.ceil(4096.0 / blocksPerWord);
                    // Whether padding was added at the end of each word
                    boolean hasPadding = 32 % blocksPerWord != 0;

                    // Integer containing the current position
                    int position = 0;
                    // BitInputStream containing all little endian integers from all words in this storage section
                    BitInputStream bitInputStream = new BitInputStream(inputStream, ByteOrder.LITTLE_ENDIAN);

                    // initialize a new blocks array
                    short[][][] blocks = new short[16][16][16];

                    // Loop through all words
                    for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
                        // Loop through all blocks in the current word
                        for (int blockIndex = 0; blockIndex < blocksPerWord; blockIndex++) {
                            long result = bitInputStream.readBits(bitsPerBlock);
                            // This is not an implementation mistake. Because of words with padding,
                            // it can happen that the position becomes too large.
                            if (position >= 4096) {
                                continue;
                            }

                            // Retrieve x, y and z coordinate
                            int x = position / 256;
                            int y = position % 16;
                            int z = (position % 256) / 16;

                            // Set the result for the block location
                            blocks[x][y][z] = (short) result;
                            position++;
                        }

                        // If padding was added at the end of this word (always 2 bits), read it
                        if (hasPadding) {
                            bitInputStream.readBits(2);
                        }
                    }

                    // Read the amount of palettes in this storage section
                    int paletteSize = ByteBuffer
                            .wrap(new byte[]{(byte) inputStream.read(), (byte) inputStream.read(), (byte) inputStream.read(), (byte) inputStream.read()})
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .getInt();

                    // Load and parse all palettes in this storage section
                    List<CompoundTag> palette = new ArrayList<>();
                    for (int i = 0; i < paletteSize; i++) {
                        palette.add((CompoundTag) NBTUtil.read(false, inputStream));
                    }

                    // Check if we are in the first block storage (0 = world, 1 = water logging)
                    if (blockStorage == 0) {
                        // If we are in the first block storage, create and initialize a new SubChunk
                        SerializedSubChunk subChunk = new SerializedSubChunk(preset, subChunkHeight, blocks, palette);
                        resultingSubChunk = SubChunk.deserialize(subChunk);
                        preset.getSubChunks().put((short) subChunkHeight, resultingSubChunk);
                    } else if (resultingSubChunk != null && !palette.isEmpty()) {
                        // If we are in the second block storage, for waterlogged blocks, parse all waterlogged blocks

                        // Check whether the 0-index in the palette is air or water
                        Optional<Tag> paletteOptional = palette.get(0).getByName("name");
                        if (!paletteOptional.isPresent()) {
                            throw new IllegalStateException("An error occurred while attempting to parse the waterlogged block palette");
                        }
                        boolean zeroIsAir = paletteOptional.get().getAsString().getValue().equals("minecraft:air");
                        // For all blocks, parse their waterlogged state.
                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                                for (int z = 0; z < 16; z++) {
                                    short block = blocks[x][y][z];
                                    resultingSubChunk.getBlocks()[x][y][z].setWaterLogged((zeroIsAir && block == 1) || (!zeroIsAir && block == 0));
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Links TileEntities to Blocks
     * @param chunk The chunk object
     */
    private static void linkTileEntities(Chunk chunk) {
        for (TileEntity tileEntity : chunk.getTileEntities()) {
            int localX = tileEntity.getX() % 16;
            int localZ = tileEntity.getZ() % 16;
            if (localX < 0) localX += 16;
            if (localZ < 0) localZ += 16;
            chunk.getBlock(localX, tileEntity.getY(), localZ).ifPresent(block -> {
                block.setTileEntity(tileEntity);
            });
        }
    }

    /**
     * Saves the given chunk to the LevelDB storage
     * @param db The LevelDB storage
     * @param chunk The chunk object
     * @param terrainLoaded whether the terrain was loaded and should be saved
     * @param entitiesLoaded whether the entities were loaded and should be saved
     * @param data2DLoaded whether the data2D was loaded and should be saved
     */
    public static void saveChunk(LevelDB db, Chunk chunk, boolean terrainLoaded, boolean entitiesLoaded, boolean data2DLoaded) {
        if (data2DLoaded) {
            saveChunkData2D(db, chunk);
        }
        if (terrainLoaded) {
            saveChunkTileEntities(db, chunk);
            saveChunkSubChunks(db, chunk);
        }
        if (entitiesLoaded) {
            saveChunkEntities(db, chunk);
        }
    }

    /**
     * Saves the 2d chunk data (elevation, biomes) from the given chunk to the LevelDB storage
     * @param db The LevelDB storage
     * @param chunk The chunk object
     */
    private static void saveChunkData2D(LevelDB db, Chunk chunk) {
        // Generate the level DB key
        byte[] levelDBKey = generateLevelDBKey(chunk.getChunkX(), chunk.getChunkZ(), chunk.getDimension(), (byte) 45, (byte) 0);
        byte[] value = new byte[768];

        // For all blocks, set the values elevation and biome
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int elevationIndex = 2 * (x + z * 16);
                int biomeIndex = 512 + x + z * 16;

                value[elevationIndex] = (byte) (chunk.getElevation()[x][z] & 0xff);
                value[elevationIndex + 1] = (byte) ((chunk.getElevation()[x][z] >> 8) & 0xff);
                value[biomeIndex] = chunk.getBiomes()[x][z];
            }
        }

        // Save to DB
        db.put(levelDBKey, value);
    }

    /**
     * Saves the tile entity data from the given chunk to the LevelDB storage
     * @param db The LevelDB storage
     * @param chunk The chunk object
     */
    @SneakyThrows
    private static void saveChunkTileEntities(LevelDB db, Chunk chunk) {
        // Generate the level DB key
        byte[] levelDBKey = generateLevelDBKey(chunk.getChunkX(), chunk.getChunkZ(), chunk.getDimension(), (byte) 49, (byte) 0);

        // Serialize and write all tile entities to an output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (TileEntity tileEntity : chunk.getTileEntities()) {
            byte[] bytes = NBTUtil.write(tileEntity.getParent());
            outputStream.write(bytes);
        }

        // Save to DB
        db.put(levelDBKey, outputStream.toByteArray());
    }

    /**
     * Saves the entity data from the given chunk to the LevelDB storage
     * @param db The LevelDB storage
     * @param chunk The chunk object
     */
    @SneakyThrows
    private static void saveChunkEntities(LevelDB db, Chunk chunk) {
        // Generate the level DB key
        byte[] levelDBKey = generateLevelDBKey(chunk.getChunkX(), chunk.getChunkZ(), chunk.getDimension(), (byte) 50, (byte) 0);

        // Serialize and write all entities to an output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Entity entity : chunk.getEntities()) {
            byte[] bytes = NBTUtil.write(entity.getParentCompoundTag());
            outputStream.write(bytes);
        }

        // Save to DB
        db.put(levelDBKey, outputStream.toByteArray());
    }

    /**
     * Most difficult part of chunk storage, the SubChunks. Relevant storage information is at:
     * {@see <a href="https://minecraft.gamepedia.com/Bedrock_Edition_level_format">Bedrock Level Format</a>}
     * Saves all SubChunks that are stored in this chunk to the LevelDB storage
     * @param db The LevelDB storage
     * @param chunk The chunk object
     */
    @SneakyThrows
    private static void saveChunkSubChunks(LevelDB db, Chunk chunk) {
        // Loop through all stored subchunks
        chunk.getSubChunks().forEach((subChunkHeight, deserializedSubChunk) -> {
            SerializedSubChunk subChunk = deserializedSubChunk.serialize();
            // Generate the level DB key
            byte[] levelDBKey = generateLevelDBKey(chunk.getChunkX(), chunk.getChunkZ(), chunk.getDimension(), (byte) 47, (byte) ((short) subChunkHeight));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // Write version (8)
            outputStream.write(8);
            // Write the amount of storages (1 by default, 2 if water logging data is present)
            int storageCount = subChunk.getWaterLoggedPalette() == null ? 1 : 2;
            outputStream.write(storageCount);

            // Add the default block storage section to the output stream
            addBlockStorageToOutputStream(outputStream, subChunk.getPalette(), subChunk.getPaletteIndices());
            if (storageCount == 2) {
                // Add the water logging block storage section to the output stream
                addBlockStorageToOutputStream(outputStream, subChunk.getWaterLoggedPalette(), subChunk.getWaterLoggedIndices());
            }

            // Save to DB
            byte[] value = outputStream.toByteArray();
            db.put(levelDBKey, value);
        });
    }

    /**
     * Writes an entire block storage section for the given palette and palette indices to the given output stream.
     * @param outputStream The output stream to write to
     * @param palette The palette
     * @param paletteIndices The palette indices
     */
    @SneakyThrows
    private static void addBlockStorageToOutputStream(OutputStream outputStream, List<CompoundTag> palette, short[][][] paletteIndices) {
        int paletteCount = palette.size();
        // Parse the amount of bits that are required to store the current set of blocks
        int bitsPerBlock = (int) Math.max(Math.ceil(Math.log(paletteCount) / Math.log(2)), 1);
        for (int i : new byte[]{1, 2, 3, 4, 5, 6, 8, 16}) {
            if (i >= bitsPerBlock) {
                bitsPerBlock = i;
                break;
            }
        }

        // Calculate the storage version
        byte storageVersion = (byte) (bitsPerBlock << 1);
        outputStream.write(storageVersion);
        // Calculate the amount of blocks per word and the word count
        int blocksPerWord = (int) Math.floor(32.0 / bitsPerBlock);
        int wordCount = (int) Math.ceil(4096.0 / blocksPerWord);

        // Loop through all words
        int position = 0;
        for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
            int word = 0;
            int index = 0;

            // Difficult piece of logic. Loop backwards through the word size
            for (int blockIndex = blocksPerWord - 1; blockIndex >= 0; blockIndex--) {
                // Check if our position is still correct. If not, it is safe to ignore the current loop.
                if (wordIndex * blocksPerWord + blockIndex < 4096) {
                    // Calculate the X, Y and Z location and the result
                    int x = position / 256;
                    int y = position % 16;
                    int z = (position % 256) / 16;
                    int result = paletteIndices[x][y][z];

                    // Bitwise append the result to the LEFT of the current word.
                    word |= (result << index);

                    // Increment the position and index
                    position++;
                    index += bitsPerBlock;
                }
            }

            // Write the four word bytes (little endian)
            outputStream.write((byte) word);
            outputStream.write((byte) (word >>> 8));
            outputStream.write((byte) (word >>> 16));
            outputStream.write((byte) (word >>> 24));
        }

        // Write the palette to the output stream
        outputStream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(palette.size()).array());
        for (CompoundTag block : palette) {
            outputStream.write(NBTUtil.write(block));
        }
    }

    /**
     * Generates a LevelDB key for the given parameters
     * @param chunkX The chunk X
     * @param chunkZ The chunk Z
     * @param dimension The chunk dimension
     * @param recordType The type of record for the LevelDB accessor
     * @param subChunkIndex The optional SubChunk height index. Set to '0' if the 'recordType' is not 47
     * @return The LevelDB key (byte[])
     */
    public static byte[] generateLevelDBKey(int chunkX, int chunkZ, Dimension dimension, byte recordType, byte subChunkIndex) {
        // Create a new ByteBuffer with the required size
        ByteBuffer buffer = ByteBuffer.allocate(8 + (dimension == Dimension.OVERWORLD ? 0 : 4) + 1 + (recordType != 47 ? 0 : 1)).order(ByteOrder.LITTLE_ENDIAN);
        // Add the chunk X and Z
        buffer.putInt(chunkX).putInt(chunkZ);
        // If the dimension is not the overworld, add it to the key
        if (dimension != Dimension.OVERWORLD) {
            buffer.putInt(dimension.getId());
        }
        // Add the record type
        buffer.put(recordType);
        // If the record is a SubChunk, also add the SubChunk index
        if (recordType == 47) {
            buffer.put(subChunkIndex);
        }
        return buffer.array();
    }
}
