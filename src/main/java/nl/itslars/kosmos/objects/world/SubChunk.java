package nl.itslars.kosmos.objects.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.kosmos.enums.BlockType;
import nl.itslars.mcpenbt.tags.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for representing a SubChunk (16x16x16 area) of a {@link Chunk}.
 * This contains all chunk blocks.
 */
@RequiredArgsConstructor
@Getter
public class SubChunk {

    // A one-time-created palette for waterlogged blocks
    private static final List<CompoundTag> WATER_LOGGED_PALETTE = Arrays.asList(
            new Block(BlockType.AIR, 0, 0, 0).serialize(),
            new Block(BlockType.WATER, 0, 0, 0).serialize()
    );

    // The parent Chunk that this SubChunk belongs to
    private final Chunk parentChunk;
    // The chunk Y of this SubChunk
    private final short subChunkHeight;
    // The 16x16x16 3D-array containing all blocks in this chunk
    private final Block[][][] blocks;

    /**
     * Converts an instance of this object into a {@link SerializedSubChunk}, that is then saved by the chunk saver.
     * @return A new {@link SerializedSubChunk} object.
     */
    public SerializedSubChunk serialize() {
        // Create a new array of palette indices
        short[][][] paletteIndices = new short[16][16][16];
        // Create a new list that wil represent the palette of this SubChunk
        List<CompoundTag> palette = new ArrayList<>();

        // After looping through all blocks, this boolean will tell whether the chunk contains any waterlogged blocks.
        boolean hasWaterLoggedBlocks = false;
        // The 3d array of all waterlogged indices. A point is this array is '1' if the block is waterlogged.
        short[][][] waterLoggedIndices = new short[16][16][16];

        // Loop through all blocks
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = blocks[x][y][z];
                    // Check if the block was waterlogged
                    if (block.isWaterLogged()) {
                        hasWaterLoggedBlocks = true;
                        waterLoggedIndices[x][y][z] = 1;
                    }
                    // Serialize the block into a CompoundTag, to add to the palette.
                    CompoundTag tag = block.serialize();
                    // Check if this block was already in the palette.
                    int index = palette.indexOf(tag);

                    // If the block was already in the palette, we point the index of this block to that palette location
                    // Otherwise, we add the new palette item to the palette, and point the block index to that location
                    if (index >= 0) {
                        paletteIndices[x][y][z] = (short) index;
                    } else {
                        palette.add(tag);
                        paletteIndices[x][y][z] = (short) (palette.size() - 1);
                    }
                }
            }
        }

        // Create the new SerializedSubChunk
        SerializedSubChunk result = new SerializedSubChunk(parentChunk, subChunkHeight, paletteIndices, palette);
        // If the chunk had waterlogged blocks, add them to the SerializedSubChunk
        if (hasWaterLoggedBlocks) {
            result.setWaterLoggedIndices(waterLoggedIndices);
            result.setWaterLoggedPalette(WATER_LOGGED_PALETTE);
        }
        return result;
    }

    /**
     * Converts a {@link SerializedSubChunk} to an actual SubChunk.
     * @param serializedSubChunk The {@link SerializedSubChunk} that was loaded by the chunk loader
     * @return The newly created SubChunk
     */
    public static SubChunk deserialize(SerializedSubChunk serializedSubChunk) {
        // Create a new SubChunk instance
        SubChunk result = new SubChunk(serializedSubChunk.getParentChunk(),
                serializedSubChunk.getSubChunkHeight(),
                new Block[16][16][16]);

        // Loop through all blocks in the SubChunk
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    // Check the palette index that the block corresponded to.
                    short paletteIndex = serializedSubChunk.getPaletteIndices()[x][y][z];
                    CompoundTag tag = serializedSubChunk.getPalette().get(paletteIndex);
                    // Deserialize the palette CompoundTag into a Block, and set it in the SubChunk
                    result.getBlocks()[x][y][z] = Block.deserialize(tag,
                            x + 16 * serializedSubChunk.getParentChunk().getChunkX(),
                            y + 16 * serializedSubChunk.getSubChunkHeight(),
                            z + 16 * serializedSubChunk.getParentChunk().getChunkZ());
                }
            }
        }

        return result;
    }
}
