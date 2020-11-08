package nl.itslars.kosmos.objects.settings;

import lombok.RequiredArgsConstructor;
import nl.itslars.kosmos.enums.Ability;
import nl.itslars.mcpenbt.enums.TagType;
import nl.itslars.mcpenbt.tags.*;

import java.util.Optional;

/**
 * This class represents the 'abilities' compound tag that is stored in the level.dat file and the player entity
 */
@RequiredArgsConstructor
public class Abilities {

    // The compound tag associated with the abilities
    private final CompoundTag parentCompoundTag;

    /**
     * Sets a boolean ability in the abilities. Throws an exception if the ability is not a byte ability
     * @param ability The ability
     * @param value The (boolean) value
     */
    public void setAbility(Ability ability, boolean value) {
        // Check correctness
        if (ability.getTagType() != TagType.TAG_BYTE) {
            throw new IllegalArgumentException("The " + ability.name() + " ability does not accept BOOLEAN values");
        }
        parentCompoundTag.change(ability.getName(), new ByteTag(ability.getName(), (byte) (value ? 1 : 0)));
    }

    /**
     * Sets an int ability in the abilities. Throws an exception if the ability is not an int ability
     * @param ability The ability
     * @param value The (int) value
     */
    public void setAbility(Ability ability, int value) {
        // Check correctness
        if (ability.getTagType() != TagType.TAG_INT) {
            throw new IllegalArgumentException("The " + ability.name() + " ability does not accept INT values");
        }
        parentCompoundTag.change(ability.getName(), new IntTag(ability.getName(), value));
    }

    /**
     * Sets a float ability in the abilities. Throws an exception if the ability is not a float ability
     * @param ability The ability
     * @param value The (float) value
     */
    public void setAbility(Ability ability, float value) {
        // Check correctness
        if (ability.getTagType() != TagType.TAG_FLOAT) {
            throw new IllegalArgumentException("The " + ability.name() + " ability does not accept FLOAT values");
        }
        parentCompoundTag.change(ability.getName(), new FloatTag(ability.getName(), value));
    }

    /**
     * Retrieves the given ability as a boolean. Throws an exception if the ability is not a byte ability
     * @param ability The ability
     * @return The ability value as a boolean
     */
    public boolean getBooleanAbility(Ability ability) {
        if (ability.getTagType() != TagType.TAG_BYTE) {
            throw new IllegalArgumentException("The " + ability.name() + " ability does not have BOOLEAN values");
        }
        Optional<Tag> tagOptional = parentCompoundTag.getByName(ability.getName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given ability is not a present boolean ability");
        return tagOptional.get().getAsByte().getValue() == (byte) 1;
    }

    /**
     * Retrieves the given ability as an int. Throws an exception if the ability is not a byte ability
     * @param ability The ability
     * @return The ability value as an int
     */
    public int getIntAbility(Ability ability) {
        if (ability.getTagType() != TagType.TAG_INT) {
            throw new IllegalArgumentException("The " + ability.name() + " ability does not have INT values");
        }
        Optional<Tag> tagOptional = parentCompoundTag.getByName(ability.getName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given ability is not a present int ability");
        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Retrieves the given ability as a float. Throws an exception if the ability is not a float ability
     * @param ability The ability
     * @return The ability value as a float
     */
    public float getFloatAbility(Ability ability) {
        if (ability.getTagType() != TagType.TAG_FLOAT) {
            throw new IllegalArgumentException("The " + ability.name() + " ability does not have FLOAT values");
        }
        Optional<Tag> tagOptional = parentCompoundTag.getByName(ability.getName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given ability is not a present float ability");
        return tagOptional.get().getAsFloat().getValue();
    }
}
