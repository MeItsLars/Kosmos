package nl.itslars.kosmos.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
/**
 * Enum for representing all Minecraft gamerules. Each gamerule has its associated NBT key name.
 */
public enum GameRule {

    COMMAND_BLOCKS_ENABLED("commandblocksenabled"),
    COMMAND_BLOCK_OUTPUT("commandblockoutput"),
    DO_DAYLIGHT_CYCLE("dodaylightcycle"),
    DO_ENTITY_DROPS("doentitydrops"),
    DO_FIRE_TICK("dofiretick"),
    DO_INSOMNIA("doinsomnia"),
    DO_IMMEDIATE_RESPAWN("doimmediaterespawn"),
    DO_MOB_LOOT("domobploot"),
    DO_MOB_SPAWNING("domobspawning"),
    DO_TILE_DROPS("dotiledrops"),
    DO_WEATHER_CYCLE("doweathercycle"),
    DROWNING_DAMAGE("drowningdamage"),
    FALL_DAMAGE("falldamage"),
    FIRE_DAMAGE("firedamage"),
    KEEP_INVENTORY("keepinventory"),
    MAX_COMMAND_CHAIN_LENGTH("maxcommandchainlength"),
    MOB_GRIEFING("mobgriefing"),
    NATURAL_REGENERATION("naturalregeneration"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomtickspeed"),
    SEND_COMMAND_FEEDBACK("sendcommandfeedback"),
    SHOW_COORDINATES("showcoordinates"),
    SHOW_DEATH_MESSAGES("showdeathmessages"),
    SPAWN_RADIUS("spawnradius"),
    TNT_EXPLODES("tntexplodes"),
    SHOW_TAGS("showtags"),
    ;

    // The NBT key name in the level.dat file
    private final String levelDatName;

}
