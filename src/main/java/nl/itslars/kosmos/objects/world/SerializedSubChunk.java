package nl.itslars.kosmos.objects.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.itslars.mcpenbt.tags.CompoundTag;

import java.util.List;

/**
 * Class representing a serialized {@link SubChunk}. When a {@link SubChunk} is loaded, it is loaded from a
 * SerializedSubChunk object that is created by the chunk loader. When a {@link SubChunk} is saved, it is serialized
 * into a SerializedSubChunk, which is then saved by the chunk saver.
 */
@RequiredArgsConstructor
@Getter
public class SerializedSubChunk {

    // The parent Chunk object
    private final Chunk parentChunk;
    // The chunk Y level
    private final short subChunkHeight;
    // The block palette indices
    private final short[][][] paletteIndices;
    // The palette; the list of blocks
    private final List<CompoundTag> palette;

    // The water logged palette indices
    @Setter
    private short[][][] waterLoggedIndices;
    // The palette; the list of blocks. For water logging, this should have only 2 blocks: water and air.
    @Setter
    private List<CompoundTag> waterLoggedPalette;

}
