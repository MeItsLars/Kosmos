package nl.itslars.kosmos.util;

/**
 * Constants class containing all NBT key names for all entities
 */
public class EntityNBTConstants {

    private EntityNBTConstants() {
        throw new IllegalStateException("Utility class");
    }

    // NBT TAG CONSTANTS FOR ALL ENTITIES
    public static final String ENTITY_NBT_DEFINITIONS = "definitions";
    public static final String ENTITY_NBT_POS = "Pos";
    public static final String ENTITY_NBT_ROTATION = "Rotation";
    public static final String ENTITY_NBT_TAGS = "Tags";
    public static final String ENTITY_NBT_CHESTED = "Chested";
    public static final String ENTITY_NBT_COLOR = "Color";
    public static final String ENTITY_NBT_COLOR2 = "Color2";
    public static final String ENTITY_NBT_CUSTOM_NAME = "CustomName";
    public static final String ENTITY_NBT_CUSTOM_NAME_VISIBLE = "CustomNameVisible";
    public static final String ENTITY_NBT_FALL_DISTANCE = "FallDistance";
    public static final String ENTITY_NBT_FIRE = "Fire";
    public static final String ENTITY_NBT_IDENTIFIER = "identifier";
    public static final String ENTITY_NBT_INVULNERABLE = "Invulnerable";
    public static final String ENTITY_NBT_IS_ANGRY = "IsAngry";
    public static final String ENTITY_NBT_IS_AUTONOMOUS = "IsAutonomous";
    public static final String ENTITY_NBT_IS_BABY = "IsBaby";
    public static final String ENTITY_NBT_IS_EATING = "IsEating";
    public static final String ENTITY_NBT_IS_GLIDING = "IsGliding";
    public static final String ENTITY_NBT_IS_GLOBAL = "IsGlobal";
    public static final String ENTITY_NBT_IS_ILLAGER_CAPTAIN = "IsIllagerCaptain";
    public static final String ENTITY_NBT_IS_ORPHANED = "IsOrphaned";
    public static final String ENTITY_NBT_IS_ROARING = "IsRoaring";
    public static final String ENTITY_NBT_IS_SCARED = "IsScared";
    public static final String ENTITY_NBT_IS_STUNNED = "IsStunned";
    public static final String ENTITY_NBT_IS_SWIMMING = "IsSwimming";
    public static final String ENTITY_NBT_IS_TAMED = "IsTamed";
    public static final String ENTITY_NBT_IS_TRUSTING = "IsTrusting";
    public static final String ENTITY_NBT_LOOT_DROPPED = "LootDropped";
    public static final String ENTITY_NBT_MARK_VARIANT = "MarkVariant";
    public static final String ENTITY_NBT_ON_GROUND = "OnGround";
    public static final String ENTITY_NBT_OWNER_NEW = "OwnerNew";
    public static final String ENTITY_NBT_PORTAL_COOLDOWN = "PortalCooldown";
    public static final String ENTITY_NBT_SADDLED = "Saddled";
    public static final String ENTITY_NBT_SHEARED = "Sheared";
    public static final String ENTITY_NBT_SHOW_BOTTOM = "ShowBottom";
    public static final String ENTITY_NBT_SITTING = "Sitting";
    public static final String ENTITY_NBT_SKIN_ID = "SkinID";
    public static final String ENTITY_NBT_STRENGTH = "Strength";
    public static final String ENTITY_NBT_STRENGTH_MAX = "StrengthMax";
    public static final String ENTITY_NBT_UNIQUE_ID = "UniqueID";
    public static final String ENTITY_NBT_VARIANT = "Variant";

    // NBT TAG CONSTANTS FOR LIVING ENTITIES
    public static final String LIVING_ENTITY_NBT_ARMOR = "Armor";
    public static final String LIVING_ENTITY_NBT_ATTRIBUTES = "Attributes";
    public static final String LIVING_ENTITY_NBT_MAINHAND = "Mainhand";
    public static final String LIVING_ENTITY_NBT_OFFHAND = "Offhand";
    public static final String LIVING_ENTITY_NBT_AIR = "Air";
    public static final String LIVING_ENTITY_NBT_ATTACK_TIME = "AttackTime";
    public static final String LIVING_ENTITY_NBT_BOUND_X = "boundX";
    public static final String LIVING_ENTITY_NBT_BOUND_Y = "boundY";
    public static final String LIVING_ENTITY_NBT_BOUND_Z = "boundZ";
    public static final String LIVING_ENTITY_NBT_CAN_PICKUP_ITEMS = "canPickupItems";
    public static final String LIVING_ENTITY_NBT_DEAD = "Dead";
    public static final String LIVING_ENTITY_NBT_DEATH_TIME = "DeathTime";
    public static final String LIVING_ENTITY_NBT_HAS_BOUND_ORIGIN = "hasBoundOrigin";
    public static final String LIVING_ENTITY_NBT_HAS_SET_CAN_PICKUP_ITEMS = "hasSetCanPickupItems";
    public static final String LIVING_ENTITY_NBT_HURT_TIME = "HurtTime";
    public static final String LIVING_ENTITY_NBT_IS_PREGNANT = "IsPregnant";
    public static final String LIVING_ENTITY_NBT_LEASHER_ID = "LeasherID";
    public static final String LIVING_ENTITY_NBT_LIMITED_LIFE = "limitedLife";
    public static final String LIVING_ENTITY_NBT_NATURAL_SPAWN = "NaturalSpawn";
    public static final String LIVING_ENTITY_NBT_SURFACE = "Surface";
    public static final String LIVING_ENTITY_NBT_TARGET_ID = "TargetID";
    public static final String LIVING_ENTITY_NBT_TRADE_EXPERIENCE = "TradeExperience";
    public static final String LIVING_ENTITY_NBT_TRADE_TIER = "TradeTier";

    // NBT TAG CONSTANTS FOR ENTITY 'PLAYER'
    public static final String PLAYER_NBT_ABILITIES = "abilities";
    public static final String PLAYER_NBT_ENDER_CHEST_INVENTORY = "EnderChestInventory";
    public static final String PLAYER_NBT_INVENTORY = "Inventory";
    public static final String PLAYER_NBT_MOTION = "Motion";
    public static final String PLAYER_NBT_PLAYER_UI_ITEMS = "PlayerUIItems";
    public static final String PLAYER_NBT_DIMENSION_ID = "DimensionId";
    public static final String PLAYER_NBT_ENCHANTMENT_SEED = "EnchantmentSeed";
    public static final String PLAYER_NBT_FORMAT_VERSION = "format_version";
    public static final String PLAYER_NBT_HAS_SEEN_CREDITS = "HasSeenCredits";
    public static final String PLAYER_NBT_MAP_INDEX = "MapIndex";
    public static final String PLAYER_NBT_PLAYER_GAME_MODE = "PlayerGameMode";
    public static final String PLAYER_NBT_PLAYER_LEVEL = "PlayerLevel";
    public static final String PLAYER_NBT_PLAYER_LEVEL_PROGRESS = "PlayerLevelProgress";
    public static final String PLAYER_NBT_R5_DATA_RECOVER_COMPLETE = "R5DataRecoverComplete";
    public static final String PLAYER_NBT_SELECTED_CONTAINER_ID = "SelectedContainerId";
    public static final String PLAYER_NBT_SELECTED_INVENTORY_SLOT = "SelectedInventorySlot";
    public static final String PLAYER_NBT_SLEEPING = "Sleeping";
    public static final String PLAYER_NBT_SLEEP_TIMER = "SleepTimer";
    public static final String PLAYER_NBT_SNEAKING = "Sneaking";
    public static final String PLAYER_NBT_SPAWN_BLOCK_POSITION_X = "SpawnBlockPositionX";
    public static final String PLAYER_NBT_SPAWN_BLOCK_POSITION_Y = "SpawnBlockPositionY";
    public static final String PLAYER_NBT_SPAWN_BLOCK_POSITION_Z = "SpawnBlockPositionZ";
    public static final String PLAYER_NBT_SPAWN_DIMENSION = "SpawnDimension";
    public static final String PLAYER_NBT_SPAWN_X = "SpawnX";
    public static final String PLAYER_NBT_SPAWN_Y = "SpawnY";
    public static final String PLAYER_NBT_SPAWN_Z = "SpawnZ";
    public static final String PLAYER_NBT_TIME_SINCE_REST = "TimeSinceRest";
}
