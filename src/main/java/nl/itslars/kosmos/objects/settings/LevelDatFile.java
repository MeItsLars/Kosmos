package nl.itslars.kosmos.objects.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import nl.itslars.kosmos.enums.GameMode;
import nl.itslars.kosmos.enums.GameRule;
import nl.itslars.kosmos.enums.Generator;
import nl.itslars.mcpenbt.enums.TagType;
import nl.itslars.mcpenbt.tags.*;

import java.io.File;
import java.util.Optional;

/**
 * Class for representing the Minecraft level.dat file, stored in the {@link nl.itslars.kosmos.objects.world.WorldData}
 */
@Getter
public class LevelDatFile {

    // The Gson instance
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

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
     * @param value The cheat state (true if enabled, false if disabled)
     */
    public void setCheatsEnabled(boolean value) {
        parentCompoundTag.change("commandsEnabled", new ByteTag("commandsEnabled", (byte) (value ? 1 : 0)));
    }

    /**
     * Returns whether cheats are enabled
     * @return The cheat state (true if enabled, false if disabled)
     */
    public boolean isCheatsEnabled() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("commandsEnabled");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("Cheat settings were not found in level.dat");

        return tagOptional.get().getAsByte().getValue() == (byte) 1;
    }

    /**
     * Sets the game's world generator to the given value
     * @param value The generator
     */
    public void setGenerator(Generator value) {
        parentCompoundTag.change("Generator", new IntTag("Generator", value.getId()));
    }

    /**
     * Attempts to get the generator of the world. Throws an exception if the generator setting was not found.
     * @return The generator
     */
    public Generator getGenerator() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("Generator");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("Generator setting was not found in level.dat");

        return Generator.fromId(tagOptional.get().getAsInt().getValue());
    }

    /**
     * Sets the world's game mode to the given value
     * @param value The game mode
     */
    public void setGameMode(GameMode value) {
        parentCompoundTag.change("GameType", new IntTag("GameType", value.getId()));
    }

    /**
     * Attempts to get the game mode of the world. Throws an exception if the game mode setting was not found.
     * @return The game mode
     */
    public GameMode getGameMode() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("GameType");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("GameType setting was not found in level.dat");

        return GameMode.fromId(tagOptional.get().getAsInt().getValue());
    }

    /**
     * Sets the world's spawn x coordinate to the given value
     * @param value The spawn x coordinate
     */
    public void setSpawnX(int value) {
        parentCompoundTag.change("SpawnX", new IntTag("SpawnX", value));
    }

    /**
     * Attempts to get the spawn x coordinate of the world. Throws an exception if the game mode setting was not found.
     * @return The spawn x coordinate
     */
    public int getSpawnX() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("SpawnX");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("SpawnX setting was not found in level.dat");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Sets the world's spawn y coordinate to the given value
     * @param value The spawn y coordinate
     */
    public void setSpawnY(int value) {
        parentCompoundTag.change("SpawnY", new IntTag("SpawnY", value));
    }

    /**
     * Attempts to get the spawn y coordinate of the world. Throws an exception if the game mode setting was not found.
     * @return The spawn y coordinate
     */
    public int getSpawnY() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("SpawnY");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("SpawnY setting was not found in level.dat");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Sets the world's spawn z coordinate to the given value
     * @param value The spawn z coordinate
     */
    public void setSpawnZ(int value) {
        parentCompoundTag.change("SpawnZ", new IntTag("SpawnZ", value));
    }

    /**
     * Attempts to get the spawn z coordinate of the world. Throws an exception if the game mode setting was not found.
     * @return The spawn z coordinate
     */
    public int getSpawnZ() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("SpawnZ");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("SpawnZ setting was not found in level.dat");

        return tagOptional.get().getAsInt().getValue();
    }

    /**
     * Sets the flat world layers configuration to the given value
     * @param value The flat world layers configuration
     */
    public void setFlatWorldLayers(FlatWorldLayers value) {
        parentCompoundTag.change("FlatWorldLayers", new StringTag("FlatWorldLayers", GSON.toJson(value)));
    }

    /**
     * Attempts to get the flat world layers configuration of the world. Throws an exception if the game mode setting was not found.
     * @return The flat world layers configuration
     */
    public FlatWorldLayers getFlatWorldLayers() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("FlatWorldLayers");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("FlatWorldLayers setting was not found in level.dat");

        return GSON.fromJson(tagOptional.get().getAsString().getValue(), FlatWorldLayers.class);
    }

}
