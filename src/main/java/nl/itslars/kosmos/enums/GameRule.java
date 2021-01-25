package nl.itslars.kosmos.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.mcpenbt.enums.TagType;

/**
 * Enum for representing all Minecraft gamerules. Each gamerule has its associated NBT key name and type.
 */
@RequiredArgsConstructor
@Getter
public enum GameRule {

    COMMAND_BLOCK_OUTPUT("commandblockoutput", TagType.TAG_BYTE),
    COMMAND_BLOCKS_ENABLED("commandblocksenabled", TagType.TAG_BYTE),
    DO_DAYLIGHT_CYCLE("dodaylightcycle", TagType.TAG_BYTE),
    DO_ENTITY_DROPS("doentitydrops", TagType.TAG_BYTE),
    DO_FIRE_TICK("dofiretick", TagType.TAG_BYTE),
    DO_IMMEDIATE_RESPAWN("doimmediaterespawn", TagType.TAG_BYTE),
    DO_INSOMNIA("doinsomnia", TagType.TAG_BYTE),
    DO_MOB_LOOT("domobploot", TagType.TAG_BYTE),
    DO_MOB_SPAWNING("domobspawning", TagType.TAG_BYTE),
    DO_TILE_DROPS("dotiledrops", TagType.TAG_BYTE),
    DO_WEATHER_CYCLE("doweathercycle", TagType.TAG_BYTE),
    DROWNING_DAMAGE("drowningdamage", TagType.TAG_BYTE),
    FALL_DAMAGE("falldamage", TagType.TAG_BYTE),
    FIRE_DAMAGE("firedamage", TagType.TAG_BYTE),
    FUNCTION_COMMAND_LIMIT("functioncommandlimit", TagType.TAG_INT),
    IMMUTABLE_WORLD("immutableWorld", TagType.TAG_BYTE),
    KEEP_INVENTORY("keepinventory", TagType.TAG_BYTE),
    MAX_COMMAND_CHAIN_LENGTH("maxcommandchainlength", TagType.TAG_INT),
    MOB_GRIEFING("mobgriefing", TagType.TAG_BYTE),
    NATURAL_REGENERATION("naturalregeneration", TagType.TAG_BYTE),
    PVP("pvp", TagType.TAG_BYTE),
    RANDOM_TICK_SPEED("randomtickspeed", TagType.TAG_INT),
    SEND_COMMAND_FEEDBACK("sendcommandfeedback", TagType.TAG_BYTE),
    SHOW_COORDINATES("showcoordinates", TagType.TAG_BYTE),
    SHOW_DEATH_MESSAGES("showdeathmessages", TagType.TAG_BYTE),
    SHOW_TAGS("showtags", TagType.TAG_BYTE),
    SPAWN_MOBS("spawnMobs", TagType.TAG_BYTE),
    SPAWN_RADIUS("spawnradius", TagType.TAG_INT),
    TNT_EXPLODES("tntexplodes", TagType.TAG_BYTE),
    ;

    // The NBT key name in the level.dat file
    private final String levelDatName;
    // The NBT tag type
    private final TagType tagType;
}
