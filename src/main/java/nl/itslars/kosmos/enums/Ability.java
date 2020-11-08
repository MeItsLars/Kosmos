package nl.itslars.kosmos.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.mcpenbt.enums.TagType;

/**
 * Enum for representing all player abilities. Each gamerule has its associated NBT key name and type.
 */
@RequiredArgsConstructor
@Getter
public enum Ability {

    ATTACK_MOBS("attackmobs", TagType.TAG_BYTE),
    ATTACK_PLAYERS("attackplayers", TagType.TAG_BYTE),
    BUILD("build", TagType.TAG_BYTE),
    DOOR_SAND_SWITCHES("doorsandswitches", TagType.TAG_BYTE),
    FLYING("flying", TagType.TAG_BYTE),
    FLY_SPEED("flySpeed", TagType.TAG_FLOAT),
    INSTA_BUILD("instabuild", TagType.TAG_BYTE),
    INVULNERABLE("invulnerable", TagType.TAG_BYTE),
    LIGHTNING("lightning", TagType.TAG_BYTE),
    MAY_FLY("mayfly", TagType.TAG_BYTE),
    MINE("mine", TagType.TAG_BYTE),
    OP("op", TagType.TAG_BYTE),
    OPEN_CONTAINERS("opencontainers", TagType.TAG_BYTE),
    PERMISSIONS_LEVEL("permissionsLevel", TagType.TAG_INT),
    PLAYER_PERMISSIONS_LEVEL("playerPermissionsLevel", TagType.TAG_INT),
    TELEPORT("teleport", TagType.TAG_BYTE),
    WALK_SPEED("walkSpeed", TagType.TAG_FLOAT)
    ;

    // The NBT key name
    private final String name;
    // The NBT tag type
    private final TagType tagType;
}
