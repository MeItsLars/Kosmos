package nl.itslars.kosmos.util;

import nl.itslars.kosmos.objects.entity.Entity;
import nl.itslars.kosmos.objects.entity.TileEntity;
import nl.itslars.kosmos.objects.entity.UnfinishedEntity;
import nl.itslars.mcpenbt.tags.CompoundTag;

import java.util.Random;

/**
 * Utility class for initializing and creating new entities.
 * As of right now, this class is pretty empty, but it can be filled later when spawning entities via code is added.
 */
public class Entities {

    public static final Random RANDOM = new Random();

    private Entities() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates a new {@link Entity} from a given {@link CompoundTag}
     * @param parent The parent {@link CompoundTag}
     * @return The resulting {@link Entity}
     */
    public static Entity createEntity(CompoundTag parent) {
        return new UnfinishedEntity(RANDOM.nextLong(), parent);
    }

    /**
     * Creates a new {@link TileEntity} from a given {@link CompoundTag}
     * @param parent The parent {@link CompoundTag}
     * @return The resulting {@link TileEntity}
     */
    public static TileEntity createTileEntity(CompoundTag parent) {
        return new TileEntity(parent);
    }
}
