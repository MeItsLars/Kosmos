package nl.itslars.kosmos.objects.world;

import lombok.Getter;
import lombok.Setter;
import nl.itslars.kosmos.enums.BlockType;
import nl.itslars.kosmos.objects.entity.TileEntity;
import nl.itslars.mcpenbt.tags.CompoundTag;
import nl.itslars.mcpenbt.tags.IntTag;
import nl.itslars.mcpenbt.tags.StringTag;
import nl.itslars.mcpenbt.tags.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Class used for representing any block in the world.
 * Every block in the world has a unique Block object, and no two coordinates should have the same Block object.
 */
@Getter
public class Block {

    // The default block version. I think this changes occasionally, but I honestly have no clue what it does.
    public static final int DEFAULT_BLOCK_VERSION = 17825808;

    // The 'states' compound tag, that represents a lot of information for some blocks, like direction.
    private final CompoundTag states;
    // The namespaced ID of this block. For default blocks, this is 'minecraft:*', for custom blocks this may change.
    private final String name;
    // The version of this block. By default set to DEFAULT_BLOCK_VERSION
    private final int version;
    // Whether the block was water logged or not. Be aware; not every block may be waterlogged.
    @Setter
    private boolean waterLogged = false;
    // Block X, Y and Z
    private final int x;
    private final int y;
    private final int z;
    // Tile entity associated with this block
    @Setter
    private TileEntity tileEntity;

    public Block(CompoundTag states, String name, int version, int x, int y, int z) {
        this.states = states;
        this.name = name;
        this.version = version;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Block(String name, int x, int y, int z) {
        this(new CompoundTag("states", new ArrayList<>()), name, DEFAULT_BLOCK_VERSION, x, y, z);
    }

    public Block(BlockType blockType, int x, int y, int z) {
        this(blockType.getNameSpacedId(), x, y, z);
    }

    /**
     * Serializes this block into a {@link CompoundTag}. Used for creating palettes in the world data.
     * @return The serialized block, represented by a {@link CompoundTag}.
     */
    public CompoundTag serialize() {
        List<Tag> tags = Arrays.asList(states, new StringTag("name", name), new IntTag("version", version));
        return new CompoundTag("", tags);
    }

    /**
     * Deserializes a {@link CompoundTag} from a palette into a Block.
     * @param compoundTag The {@link CompoundTag} that represents the palette.
     * @param x The X coordinate of the block
     * @param y The Y coordinate of the block
     * @param z The Z coordinate of the block
     * @return The resulting Block object.
     */
    public static Block deserialize(CompoundTag compoundTag, int x, int y, int z) {
        Optional<Tag> statesTag = compoundTag.getByName("states");
        Optional<Tag> nameTag = compoundTag.getByName("name");
        Optional<Tag> versionTag = compoundTag.getByName("version");
        if (statesTag.isPresent() & nameTag.isPresent() && versionTag.isPresent()) {
            CompoundTag states = statesTag.get().getAsCompound();
            String name = nameTag.get().getAsString().getValue();
            int version = versionTag.get().getAsInt().getValue();
            return new Block(states, name, version, x, y, z);
        }
        // Older worlds and worlds converted from Java might not have those tags present
        else if (nameTag.isPresent()) {
            String name = nameTag.get().getAsString().getValue();
            return new Block(name, x, y, z);
        } else {
            throw new IllegalStateException("Failed to deserialize the block, a parsing error occured.");
        }
    }
}
