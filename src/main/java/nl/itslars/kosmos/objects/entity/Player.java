package nl.itslars.kosmos.objects.entity;

import lombok.Getter;
import nl.itslars.kosmos.exception.NotYetImplementedException;
import nl.itslars.mcpenbt.enums.TagType;
import nl.itslars.mcpenbt.tags.CompoundTag;

import java.util.Arrays;
import java.util.List;

import static nl.itslars.kosmos.util.EntityNBTConstants.*;

/**
 * Class used to represent all loaded players, extends the {@link LivingEntity}.
 * Every player that is saved in the world, is converted to an object that extends this class.
 *
 * No methods in this class have JavaDocs. I decided this, because it would take me a huge amount
 * of time to document all methods, and it doesn't add that much value. If you want to do this, feel free to do so! :D
 */
public class Player extends LivingEntity {

    // The NBT key that this player has in the world files
    @Getter
    private final byte[] nbtKey;

    public Player(CompoundTag parentCompoundTag, byte[] nbtKey) {
        super(parentCompoundTag);
        this.nbtKey = nbtKey;
    }

    public CompoundTag getAbilities() {
        return (CompoundTag) getNbtTag(PLAYER_NBT_ABILITIES);
    }

    public void setAbilities(CompoundTag abilities) {
        getParentCompoundTag().change(PLAYER_NBT_ABILITIES, abilities);
    }

    // List of ItemStack???
    public <T> List<T> getEnderChestInventory() {
        throw new NotYetImplementedException();
    }

    // List of ItemStack???
    public <T> void setEnderChestInventory(List<T> enderChestInventory) {
        throw new NotYetImplementedException();
    }

    // List of ItemStack???
    public <T> List<T> getInventory() {
        throw new NotYetImplementedException();
    }

    // List of ItemStack???
    public <T> void setInventory(List<T> inventory) {
        throw new NotYetImplementedException();
    }

    public List<Float> getMotion() {
        return getList(PLAYER_NBT_MOTION, TagType.TAG_FLOAT);
    }

    public void setMotion(float x, float y, float z) {
        setMotion(Arrays.asList(x, y, z));
    }

    public void setMotion(List<Float> motion) {
        if (motion.size() != 3) {
            throw new IllegalArgumentException("The motion list must have three elements: X, Y and Z");
        }
        setList(PLAYER_NBT_MOTION, motion, TagType.TAG_FLOAT);
    }

    // What even is this?
    public <T> List<T> getPlayerUIItems() {
        throw new UnsupportedOperationException();
    }

    // What even is this?
    public <T> void setPlayerUIItems(List<T> playerUIItems) {
        throw new UnsupportedOperationException();
    }

    public int getDimensionId() {
        return getIntTag(PLAYER_NBT_DIMENSION_ID);
    }

    public void setDimensionId(int dimensionId) {
        setIntTag(PLAYER_NBT_DIMENSION_ID, dimensionId);
    }

    public int getEnchantmentSeed() {
        return getIntTag(PLAYER_NBT_ENCHANTMENT_SEED);
    }

    public void setEnchantmentSeed(int enchantmentSeed) {
        setIntTag(PLAYER_NBT_ENCHANTMENT_SEED, enchantmentSeed);
    }

    public String getFormatVersion() {
        return getStringTag(PLAYER_NBT_FORMAT_VERSION);
    }

    public void setFormatVersion(String formatVersion) {
        setStringTag(PLAYER_NBT_FORMAT_VERSION, formatVersion);
    }

    public byte hasSeenCredits() {
        return getByteTag(PLAYER_NBT_HAS_SEEN_CREDITS);
    }

    public void setHasSeenCredits(byte hasSeenCredits) {
        setByteTag(PLAYER_NBT_HAS_SEEN_CREDITS, hasSeenCredits);
    }

    public int getMapIndex() {
        return getIntTag(PLAYER_NBT_MAP_INDEX);
    }

    public void setMapIndex(int mapIndex) {
        setIntTag(PLAYER_NBT_MAP_INDEX, mapIndex);
    }

    public int getPlayerGameMode() {
        return getIntTag(PLAYER_NBT_PLAYER_GAME_MODE);
    }

    public void setPlayerGameMode(int playerGameMode) {
        setIntTag(PLAYER_NBT_PLAYER_GAME_MODE, playerGameMode);
    }

    public int getPlayerLevel() {
        return getIntTag(PLAYER_NBT_PLAYER_LEVEL);
    }

    public void setPlayerLevel(int playerLevel) {
        setIntTag(PLAYER_NBT_PLAYER_LEVEL, playerLevel);
    }

    public float getPlayerLevelProgress() {
        return getFloatTag(PLAYER_NBT_PLAYER_LEVEL_PROGRESS);
    }

    public void setPlayerLevelProgress(float playerLevelProgress) {
        setFloatTag(PLAYER_NBT_PLAYER_LEVEL_PROGRESS, playerLevelProgress);
    }

    public byte getR5DataRecoverComplete() {
        return getByteTag(PLAYER_NBT_R5_DATA_RECOVER_COMPLETE);
    }

    public void setR5DataRecoverComplete(byte r5DataRecoverComplete) {
        setByteTag(PLAYER_NBT_R5_DATA_RECOVER_COMPLETE, r5DataRecoverComplete);
    }

    public int getSelectedContainerId() {
        return getIntTag(PLAYER_NBT_SELECTED_CONTAINER_ID);
    }

    public void setSelectedContainerId(int selectedContainerId) {
        setIntTag(PLAYER_NBT_SELECTED_CONTAINER_ID, selectedContainerId);
    }

    public int getSelectedInventorySlot() {
        return getIntTag(PLAYER_NBT_SELECTED_INVENTORY_SLOT);
    }

    public void setSelectedInventorySlot(int selectedInventorySlot) {
        setIntTag(PLAYER_NBT_SELECTED_INVENTORY_SLOT, selectedInventorySlot);
    }

    public byte isSleeping() {
        return getByteTag(PLAYER_NBT_SLEEPING);
    }

    public void setSleeping(byte sleeping) {
        setByteTag(PLAYER_NBT_SLEEPING, sleeping);
    }

    public byte getSleepTimer() {
        return getByteTag(PLAYER_NBT_SLEEP_TIMER);
    }

    public void setSleepTimer(byte sleepTimer) {
        setByteTag(PLAYER_NBT_SLEEP_TIMER, sleepTimer);
    }

    public byte isSneaking() {
        return getByteTag(PLAYER_NBT_SNEAKING);
    }

    public void setSneaking(byte sneaking) {
        setByteTag(PLAYER_NBT_SNEAKING, sneaking);
    }

    public int getSpawnBlockPositionX() {
        return getIntTag(PLAYER_NBT_SPAWN_BLOCK_POSITION_X);
    }

    public void setSpawnBlockPositionX(int spawnBlockPositionX) {
        setIntTag(PLAYER_NBT_SPAWN_BLOCK_POSITION_X, spawnBlockPositionX);
    }

    public int getSpawnBlockPositionY() {
        return getIntTag(PLAYER_NBT_SPAWN_BLOCK_POSITION_Y);
    }

    public void setSpawnBlockPositionY(int spawnBlockPositionY) {
        setIntTag(PLAYER_NBT_SPAWN_BLOCK_POSITION_Y, spawnBlockPositionY);
    }

    public int getSpawnBlockPositionZ() {
        return getIntTag(PLAYER_NBT_SPAWN_BLOCK_POSITION_Z);
    }

    public void setSpawnBlockPositionZ(int spawnBlockPositionZ) {
        setIntTag(PLAYER_NBT_SPAWN_BLOCK_POSITION_Z, spawnBlockPositionZ);
    }

    public int getSpawnDimension() {
        return getIntTag(PLAYER_NBT_SPAWN_DIMENSION);
    }

    public void setSpawnDimension(int spawnDimension) {
        setIntTag(PLAYER_NBT_SPAWN_DIMENSION, spawnDimension);
    }

    public int getSpawnX() {
        return getIntTag(PLAYER_NBT_SPAWN_X);
    }

    public void setSpawnX(int spawnX) {
        setIntTag(PLAYER_NBT_SPAWN_X, spawnX);
    }

    public int getSpawnY() {
        return getIntTag(PLAYER_NBT_SPAWN_Y);
    }

    public void setSpawnY(int spawnY) {
        setIntTag(PLAYER_NBT_SPAWN_Y, spawnY);
    }

    public int getSpawnZ() {
        return getIntTag(PLAYER_NBT_SPAWN_Z);
    }

    public void setSpawnZ(int spawnZ) {
        setIntTag(PLAYER_NBT_SPAWN_Z, spawnZ);
    }

    public int getTimeSinceRest() {
        return getIntTag(PLAYER_NBT_TIME_SINCE_REST);
    }

    public void setTimeSinceRest(int timeSinceRest) {
        setIntTag(PLAYER_NBT_TIME_SINCE_REST, timeSinceRest);
    }
}
