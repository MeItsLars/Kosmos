package nl.itslars.kosmos.objects.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.mcpenbt.tags.CompoundTag;
import nl.itslars.mcpenbt.tags.IntTag;
import nl.itslars.mcpenbt.tags.StringTag;
import nl.itslars.mcpenbt.tags.Tag;

import java.util.Optional;

/**
 * Class used to represent all tile entities. This class should become abstract in the future.
 * Every in-game tile entity that is stored in a chunk, is converted to an object that extends this class.
 * Right now, there are no directly implemented subclasses.
 * If you want to add a tile entity, feel free to do a PR on Git!
 */
@RequiredArgsConstructor
@Getter
public class TileEntity {

    private final CompoundTag parent;

    /**
     * Sets the tile entity's x coordinate to the given value
     * @param value The tile entity's x coordinate
     */
    public void setX(int value) {
        parent.change("x", new IntTag("x", value));
    }

    /**
     * Attempts to get the tile entity's x coordinate. Throws an exception if the x setting was not found.
     * @return The tile entity's x coordinate
     */
    public int getX() {
        Optional<Tag> tagOptional = parent.getByName("x");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("x setting was not found in tile entity");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Sets the tile entity's y coordinate to the given value
     * @param value The tile entity's y coordinate
     */
    public void setY(int value) {
        parent.change("y", new IntTag("y", value));
    }

    /**
     * Attempts to get the tile entity's y coordinate. Throws an exception if the y setting was not found.
     * @return The tile entity's y coordinate
     */
    public int getY() {
        Optional<Tag> tagOptional = parent.getByName("y");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("y setting was not found in tile entity");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Sets the tile entity's z coordinate to the given value
     * @param value The tile entity's z coordinate
     */
    public void setZ(int value) {
        parent.change("z", new IntTag("z", value));
    }

    /**
     * Attempts to get the tile entity's z coordinate. Throws an exception if the z setting was not found.
     * @return The tile entity's z coordinate
     */
    public int getZ() {
        Optional<Tag> tagOptional = parent.getByName("z");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("z setting was not found in tile entity");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Attempts to get the tile entity's id. Throws an exception if the id setting was not found.
     * @return The tile entity's id
     */
    public String getId() {
        Optional<Tag> tagOptional = parent.getByName("id");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("id setting was not found in tile entity");

        return tagOptional.get().getAsString().getValue();
    }

    /**
     * Sets the tile entity's id to the given value
     * @param value The tile entity's id
     */
    public void setId(String value) {
        parent.change("id", new StringTag("id", value));
    }
}
