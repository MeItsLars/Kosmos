package nl.itslars.kosmos.objects.entity;

import nl.itslars.kosmos.exception.NotYetImplementedException;
import nl.itslars.mcpenbt.tags.CompoundTag;

import java.util.List;

import static nl.itslars.kosmos.util.EntityNBTConstants.*;

/**
 * Abstract class used to represent all living entities, extends the {@link Entity}.
 * Every living in-game entity that is stored in a chunk, is converted to an object that extends this class.
 * Right now, the only finished subclass is the {@link Player} class, and all other entities are represented as
 * an {@link UnfinishedEntity}. If you want to add an entity, feel free to do a PR on Git!
 *
 * No methods in this class have JavaDocs. I decided this, because it would take me a huge amount
 * of time to document all methods, and it doesn't add that much value. If you want to do this, feel free to do so! :D
 */
public abstract class LivingEntity extends Entity {

    public LivingEntity(CompoundTag parentCompoundTag) {
        super(parentCompoundTag);
    }

    // List of ItemStack???
    public <T> List<T> getArmor() {
        throw new NotYetImplementedException();
    }

    // List of ItemStack???
    public <T> void setArmor(List<T> armor) {
        throw new NotYetImplementedException();
    }

    public <T> List<T> getAttributes() {
        throw new NotYetImplementedException();
    }

    public <T> void setAttributes(List<T> attributes) {
        throw new NotYetImplementedException();
    }

    // Is this even a thing in the latest MC version?
    public <T> List<T> getMainHand() {
        throw new UnsupportedOperationException();
    }

    // Is this even a thing in the latest MC version?
    public <T> void setMainHand(List<T> mainHand) {
        throw new UnsupportedOperationException();
    }

    // Is this even a thing in the latest MC version?
    public <T> List<T> getOffHand() {
        throw new UnsupportedOperationException();
    }

    // Is this even a thing in the latest MC version?
    public <T> void setOffHand(List<T> mainHand) {
        throw new UnsupportedOperationException();
    }

    public short getAir() {
        return getShortTag(LIVING_ENTITY_NBT_AIR);
    }

    public void setAir(short air) {
        setShortTag(LIVING_ENTITY_NBT_AIR, air);
    }

    public short getAttackTime() {
        return getShortTag(LIVING_ENTITY_NBT_ATTACK_TIME);
    }

    public void setAttackTime(short attackTime) {
        setShortTag(LIVING_ENTITY_NBT_ATTACK_TIME, attackTime);
    }

    public int getBoundX() {
        return getIntTag(LIVING_ENTITY_NBT_BOUND_X);
    }

    public void setBoundX(int boundX) {
        setIntTag(LIVING_ENTITY_NBT_BOUND_X, boundX);
    }

    public int getBoundY() {
        return getIntTag(LIVING_ENTITY_NBT_BOUND_Y);
    }

    public void setBoundY(int boundY) {
        setIntTag(LIVING_ENTITY_NBT_BOUND_Y, boundY);
    }

    public int getBoundZ() {
        return getIntTag(LIVING_ENTITY_NBT_BOUND_Z);
    }

    public void setBoundZ(int boundZ) {
        setIntTag(LIVING_ENTITY_NBT_BOUND_Z, boundZ);
    }

    public byte canPickupItems() {
        return getByteTag(LIVING_ENTITY_NBT_CAN_PICKUP_ITEMS);
    }

    public void setCanPickupItems(byte canPickupItems) {
        setByteTag(LIVING_ENTITY_NBT_CAN_PICKUP_ITEMS, canPickupItems);
    }

    public byte isDead() {
        return getByteTag(LIVING_ENTITY_NBT_DEAD);
    }

    public void setDead(byte dead) {
        setByteTag(LIVING_ENTITY_NBT_DEAD, dead);
    }

    public short getDeathTime() {
        return getShortTag(LIVING_ENTITY_NBT_DEATH_TIME);
    }

    public void setDeathTime(short deathTime) {
        setShortTag(LIVING_ENTITY_NBT_DEATH_TIME, deathTime);
    }

    public byte hasBoundOrigin() {
        return getByteTag(LIVING_ENTITY_NBT_HAS_BOUND_ORIGIN);
    }

    public void setHasByteOrigin(byte hasByteOrigin) {
        setByteTag(LIVING_ENTITY_NBT_HAS_BOUND_ORIGIN, hasByteOrigin);
    }

    public byte hasSetCanPickupItems() {
        return getByteTag(LIVING_ENTITY_NBT_HAS_SET_CAN_PICKUP_ITEMS);
    }

    public void setHasSetCanPickupItems(byte hasSetCanPickupItems) {
        setByteTag(LIVING_ENTITY_NBT_HAS_SET_CAN_PICKUP_ITEMS, hasSetCanPickupItems);
    }

    public short getHurtTime() {
        return getShortTag(LIVING_ENTITY_NBT_HURT_TIME);
    }

    public void setHurtTime(short hurtTime) {
        setShortTag(LIVING_ENTITY_NBT_HURT_TIME, hurtTime);
    }

    public byte isPregnant() {
        return getByteTag(LIVING_ENTITY_NBT_IS_PREGNANT);
    }

    public void setPregnant(byte pregnant) {
        setByteTag(LIVING_ENTITY_NBT_IS_PREGNANT, pregnant);
    }

    public long getLeasherID() {
        return getLongTag(LIVING_ENTITY_NBT_LEASHER_ID);
    }

    public void setLeasherID(long leasherID) {
        setLongTag(LIVING_ENTITY_NBT_LEASHER_ID, leasherID);
    }

    public int getLimitedLife() {
        return getIntTag(LIVING_ENTITY_NBT_LIMITED_LIFE);
    }

    public void setLimitedLife(int limitedLife) {
        setIntTag(LIVING_ENTITY_NBT_LIMITED_LIFE, limitedLife);
    }

    public byte getNaturalSpawn() {
        return getByteTag(LIVING_ENTITY_NBT_NATURAL_SPAWN);
    }

    public void setNaturalSpawn(byte naturalSpawn) {
        setByteTag(LIVING_ENTITY_NBT_NATURAL_SPAWN, naturalSpawn);
    }

    public byte getSurface() {
        return getByteTag(LIVING_ENTITY_NBT_SURFACE);
    }

    public void setSurface(byte surface) {
        setByteTag(LIVING_ENTITY_NBT_SURFACE, surface);
    }

    public long getTargetID() {
        return getLongTag(LIVING_ENTITY_NBT_TARGET_ID);
    }

    public void setTargetID(long targetID) {
        setLongTag(LIVING_ENTITY_NBT_TARGET_ID, targetID);
    }

    public int getTradeExperience() {
        return getIntTag(LIVING_ENTITY_NBT_TRADE_EXPERIENCE);
    }

    public void setTradeExperience(int tradeExperience) {
        setIntTag(LIVING_ENTITY_NBT_TRADE_EXPERIENCE, tradeExperience);
    }

    public int getTradeTier() {
        return getIntTag(LIVING_ENTITY_NBT_TRADE_TIER);
    }

    public void setTradeTier(int tradeTier) {
        setIntTag(LIVING_ENTITY_NBT_TRADE_TIER, tradeTier);
    }
}
