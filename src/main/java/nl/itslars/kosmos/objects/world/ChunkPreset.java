package nl.itslars.kosmos.objects.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.kosmos.enums.Dimension;

/**
 * Class representing a Chunk at a location.
 * All ChunkPresets of a Minecraft world are loaded when the world is opened.
 * This way, you can easily check if a certain chunk is already generated.
 */
@RequiredArgsConstructor
@Getter
public class ChunkPreset {

    // The corresponding parent WorldData object
    private final WorldData world;
    // The chunk X
    private final int x;
    // The chunk Z
    private final int z;
    // The Dimension of the chunk
    private final Dimension dimension;

}
