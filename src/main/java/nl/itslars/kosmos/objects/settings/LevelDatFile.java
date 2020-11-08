package nl.itslars.kosmos.objects.settings;

import lombok.Getter;
import nl.itslars.kosmos.enums.GameRule;
import nl.itslars.mcpenbt.enums.TagType;
import nl.itslars.mcpenbt.tags.ByteTag;
import nl.itslars.mcpenbt.tags.CompoundTag;
import nl.itslars.mcpenbt.tags.IntTag;
import nl.itslars.mcpenbt.tags.Tag;

import java.io.File;
import java.util.Optional;

/**
 * Class for representing the Minecraft level.dat file, stored in the {@link nl.itslars.kosmos.objects.world.WorldData}
 */
@Getter
public class LevelDatFile {

    // The associated file location
    private final File file;
    // The NBT compound tag that all information is stored in
    private final CompoundTag parentCompoundTag;
    // The set of abilities in the level.dat file, that players will copy once joined
    private final Abilities abilities;

    public LevelDatFile(File file, CompoundTag parentCompoundTag) {
        this.file = file;
        this.parentCompoundTag = parentCompoundTag;
        Optional<Tag> optionalTag = parentCompoundTag.getByName("abilities");
        if (!optionalTag.isPresent()) throw new IllegalStateException("Could not find an 'abilities' compound tag in the level.dat NBT!");
        abilities = new Abilities(optionalTag.get().getAsCompound());
    }

    /**
     * Sets a gamerule to a certain boolean value. If the provided gamerule is not a byte gamerule, an exception is thrown.
     * @param gameRule The gamerule
     * @param value The (boolean) value
     */
    public void setGameRule(GameRule gameRule, boolean value) {
        // Check correctness
        if (gameRule.getTagType() != TagType.TAG_BYTE) {
            throw new IllegalArgumentException("The " + gameRule.name() + " game rule does not accept BOOLEAN values");
        }
        parentCompoundTag.change(gameRule.getLevelDatName(), new ByteTag(gameRule.getLevelDatName(), (byte) (value ? 1 : 0)));
    }

    /**
     * Sets a gamerule to a certain int value. If the provided gamerule is not an int gamerule, an exception is thrown.
     * @param gameRule The gamerule
     * @param value The (int) value
     */
    public void setGameRule(GameRule gameRule, int value) {
        // Check correctness
        if (gameRule.getTagType() != TagType.TAG_INT) {
            throw new IllegalArgumentException("The " + gameRule.name() + " game rule does not accept INT values");
        }
        parentCompoundTag.change(gameRule.getLevelDatName(), new IntTag(gameRule.getLevelDatName(), value));
    }

    /**
     * Attempts to get the value of the boolean gamerule provided. Throws an exception if the gamerule was not a byte gamerule.
     * @param gameRule The gamerule
     * @return The boolean value of that gamerule
     */
    public boolean getBooleanGameRule(GameRule gameRule) {
        // Check correctness
        if (gameRule.getTagType() != TagType.TAG_BYTE) {
            throw new IllegalArgumentException("The " + gameRule.name() + " game rule does not have a BOOLEAN value");
        }
        Optional<Tag> tagOptional = parentCompoundTag.getByName(gameRule.getLevelDatName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given GameRule is not in the level.dat");

        return tagOptional.get().getAsByte().getValue() == (byte) 1;
    }

    /**
     * Attempts to get the value of the int gamerule provided. Throws an exception if the gamerule was not an int gamerule.
     * @param gameRule The gamerule
     * @return The int value of that gamerule
     */
    public int getIntGameRule(GameRule gameRule) {
        // Check correctness
        if (gameRule.getTagType() != TagType.TAG_INT) {
            throw new IllegalArgumentException("The " + gameRule.name() + " game rule does not have an INT value");
        }
        Optional<Tag> tagOptional = parentCompoundTag.getByName(gameRule.getLevelDatName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given GameRule is not in the level.dat");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Sets the game's cheat state to the given boolean value
     * @param value The cheat state (true iff enabled, false iff disabled)
     */
    public void setCheatsEnabled(boolean value) {
        parentCompoundTag.change("commandsEnabled", new ByteTag("commandsEnabled", (byte) (value ? 1 : 0)));
    }

    /**
     * Returns whether cheats are enabled
     * @return The cheat state (true iff enabled, false iff disabled)
     */
    public boolean isCheatsEnabled() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("commandsEnabled");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("Cheat settings were not found in level.dat");

        return tagOptional.get().getAsByte().getValue() == (byte) 1;
    }
}
