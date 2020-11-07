package nl.itslars.kosmos.objects.world;

import lombok.Getter;
import lombok.Setter;
import nl.itslars.kosmos.enums.BlockType;
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

    public Block(CompoundTag states, String name, int version) {
        this.states = states;
        this.name = name;
        this.version = version;
    }

    public Block(String name) {
        this(new CompoundTag("states", new ArrayList<>()), name, DEFAULT_BLOCK_VERSION);
    }

    public Block(BlockType blockType) {
        this(blockType.getNameSpacedId());
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
     * @return The resulting Block object.
     */
    public static Block deserialize(CompoundTag compoundTag) {
        Optional<Tag> statesTag = compoundTag.getByName("states");
        Optional<Tag> nameTag = compoundTag.getByName("name");
        Optional<Tag> versionTag = compoundTag.getByName("version");
        if (!statesTag.isPresent() || !nameTag.isPresent() || !versionTag.isPresent()) {
            throw new IllegalStateException("Failed to deserialize the block, a parsing error occured.");
        }
        CompoundTag states = statesTag.get().getAsCompound();
        String name = nameTag.get().getAsString().getValue();
        int version = versionTag.get().getAsInt().getValue();
        return new Block(states, name, version);
    }
}
