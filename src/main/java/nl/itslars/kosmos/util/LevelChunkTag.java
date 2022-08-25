package nl.itslars.kosmos.util;

// From https://docs.microsoft.com/en-us/minecraft/creator/documents/actorstorage
// Comments were copied from the above link.
public enum LevelChunkTag {
    Data3D(43),
    // This was moved to the front as needed for the extended heights feature. Old chunks will not have this data.
    Version(44),
    Data2D(45),
    Data2DLegacy(46),
    SubChunkPrefix(47),
    LegacyTerrain(48),
    BlockEntity(49),
    Entity(50),
    PendingTicks(51),
    LegacyBlockExtraData(52),
    BiomeState(53),
    FinalizedState(54),
    // data that the converter provides, that are used at runtime for things like blending
    ConversionData(55),
    BorderBlocks(56),
    HardcodedSpawners(57),
    RandomTicks(58),
    CheckSums(59),
    GenerationSeed(60),
    // not used, DON'T REMOVE
    GeneratedPreCavesAndCliffsBlending(61),
    // not used, DON'T REMOVE
    BlendingBiomeHeight(62),
    MetaDataHash(63),
    BlendingData(64),
    ActorDigestVersion(65),
    LegacyVersion(118);

    private final byte id;

    LevelChunkTag(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
