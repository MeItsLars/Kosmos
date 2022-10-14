package nl.itslars.kosmos.objects.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.itslars.kosmos.exception.InvalidTagTypeException;
import nl.itslars.kosmos.exception.NoSuchTagException;
import nl.itslars.mcpenbt.enums.TagType;
import nl.itslars.mcpenbt.tags.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nl.itslars.kosmos.util.EntityNBTConstants.*;

/**
 * Abstract class used to represent all entities.
 * Every in-game entity that is stored in a chunk, is converted to an object that extends this class.
 * Right now, the only finished subclass is the {@link Player} class, and all other entities are represented as
 * an {@link UnfinishedEntity}. If you want to add an entity, feel free to do a PR on Git!
 *
 * Nearly every method in this class does not have JavaDocs. I decided this, because it would take me a huge amount
 * of time to document all methods, and it doesn't add that much value. If you want to do this, feel free to do so! :D
 */
@AllArgsConstructor
@Getter
public abstract class Entity {

    // The ID of the entity from the levelDB key
    private final long worldId;
    // The Compound Tag that represents this entire entity. All methods (!) directly change or retrieve information
    // from this object.
    private CompoundTag parentCompoundTag;

    public List<String> getDefinitions() {
        return getList(ENTITY_NBT_DEFINITIONS, TagType.TAG_STRING);
    }

    public void setDefinitions(List<String> definitions) {
        setList(ENTITY_NBT_DEFINITIONS, definitions, TagType.TAG_STRING);
    }

    public List<Float> getPosition() {
        return getList(ENTITY_NBT_POS, TagType.TAG_FLOAT);
    }

    public void setPosition(float x, float y, float z) {
        setPosition(Arrays.asList(x, y, z));
    }

    public void setPosition(List<Float> position) {
        if (position.size() != 3) {
            throw new IllegalArgumentException("The position list must have three elements: X, Y and Z.");
        }
        setList(ENTITY_NBT_POS, position, TagType.TAG_FLOAT);
    }

    public void setRotation(float yaw, float pitch) {
        setRotation(Arrays.asList(yaw, pitch));
    }

    public void setRotation(List<Float> rotation) {
        if (rotation.size() != 2) {
            throw new IllegalArgumentException("The rotation list must have two elements: Yaw and Pitch");
        }
        setList(ENTITY_NBT_ROTATION, rotation, TagType.TAG_FLOAT);
    }

    public List<String> getTags() {
        return getList(ENTITY_NBT_TAGS, TagType.TAG_STRING);
    }

    public void addTag(String tag) {
        List<String> tags = getTags();
        if (!tags.contains(tag)) tags.add(tag);
        setTags(tags);
    }

    public boolean removeTag(String tag) {
        List<String> tags = getTags();
        boolean hadTag = tags.remove(tag);
        setTags(tags);
        return hadTag;
    }

    public boolean hasTag(String tag) {
        return getTags().contains(tag);
    }

    public void setTags(List<String> tags) {
        setList(ENTITY_NBT_TAGS, tags, TagType.TAG_STRING);
    }

    public byte isChested() {
        return getByteTag(ENTITY_NBT_CHESTED);
    }

    public void setChested(byte chested) {
        setByteTag(ENTITY_NBT_CHESTED, chested);
    }

    public byte getColor() {
        return getByteTag(ENTITY_NBT_COLOR);
    }

    public void setColor(byte color) {
        setByteTag(ENTITY_NBT_COLOR, color);
    }

    public byte getColor2() {
        return getByteTag(ENTITY_NBT_COLOR2);
    }

    public void setColor2(byte color2) {
        setByteTag(ENTITY_NBT_COLOR, color2);
    }

    public Optional<String> getCustomName() {
        try {
            return Optional.of(getStringTag(ENTITY_NBT_CUSTOM_NAME));
        } catch (NoSuchTagException e) {
            return Optional.empty();
        }
    }

    public void setCustomName(String customName) {
        setStringTag(ENTITY_NBT_CUSTOM_NAME, customName);
    }

    public Optional<Byte> isCustomNameVisible() {
        try {
            return Optional.of(getByteTag(ENTITY_NBT_CUSTOM_NAME_VISIBLE));
        } catch (NoSuchTagException e) {
            return Optional.empty();
        }
    }

    public void setCustomNameVisible(byte customNameVisible) {
        setByteTag(ENTITY_NBT_CUSTOM_NAME_VISIBLE, customNameVisible);
    }

    public float getFallDistance() {
        return getFloatTag(ENTITY_NBT_FALL_DISTANCE);
    }

    public void setFallDistance(float fallDistance) {
        setFloatTag(ENTITY_NBT_FALL_DISTANCE, fallDistance);
    }

    public short getFire() {
        return getShortTag(ENTITY_NBT_FIRE);
    }

    public void setFire(short fire) {
        setShortTag(ENTITY_NBT_FIRE, fire);
    }

    public String getIdentifier() {
        return getStringTag(ENTITY_NBT_IDENTIFIER);
    }

    public byte isInvulnerable() {
        return getByteTag(ENTITY_NBT_INVULNERABLE);
    }

    public void setInvulnerable(byte invulnerable) {
        setByteTag(ENTITY_NBT_INVULNERABLE, invulnerable);
    }

    public byte isAngry() {
        return getByteTag(ENTITY_NBT_IS_ANGRY);
    }

    public void setAngry(byte angry) {
        setByteTag(ENTITY_NBT_IS_ANGRY, angry);
    }

    public byte isAutonomous() {
        return getByteTag(ENTITY_NBT_IS_AUTONOMOUS);
    }

    public void setAutonomous(byte autonomous) {
        setByteTag(ENTITY_NBT_IS_AUTONOMOUS, autonomous);
    }

    public byte isBaby() {
        return getByteTag(ENTITY_NBT_IS_BABY);
    }

    public void setBaby(byte baby) {
        setByteTag(ENTITY_NBT_IS_BABY, baby);
    }

    public byte isEating() {
        return getByteTag(ENTITY_NBT_IS_EATING);
    }

    public void setEating(byte eating) {
        setByteTag(ENTITY_NBT_IS_EATING, eating);
    }

    public byte isGliding() {
        return getByteTag(ENTITY_NBT_IS_GLIDING);
    }

    public void setGliding(byte gliding) {
        setByteTag(ENTITY_NBT_IS_GLIDING, gliding);
    }

    public byte isGlobal() {
        return getByteTag(ENTITY_NBT_IS_GLOBAL);
    }

    public void setGlobal(byte global) {
        setByteTag(ENTITY_NBT_IS_GLOBAL, global);
    }

    public byte isIllagerCaptain() {
        return getByteTag(ENTITY_NBT_IS_ILLAGER_CAPTAIN);
    }

    public void setIllagerCaptain(byte illagerCaptain) {
        setByteTag(ENTITY_NBT_IS_ILLAGER_CAPTAIN, illagerCaptain);
    }

    public byte isOrphaned() {
        return getByteTag(ENTITY_NBT_IS_ORPHANED);
    }

    public void setOrphaned(byte orphaned) {
        setByteTag(ENTITY_NBT_IS_ORPHANED, orphaned);
    }

    public byte isRoaring() {
        return getByteTag(ENTITY_NBT_IS_ROARING);
    }

    public void setRoaring(byte roaring) {
        setByteTag(ENTITY_NBT_IS_ROARING, roaring);
    }

    public byte isScared() {
        return getByteTag(ENTITY_NBT_IS_SCARED);
    }

    public void setScared(byte scared) {
        setByteTag(ENTITY_NBT_IS_SCARED, scared);
    }

    public byte isStunned() {
        return getByteTag(ENTITY_NBT_IS_STUNNED);
    }

    public void setStunned(byte stunned) {
        setByteTag(ENTITY_NBT_IS_STUNNED, stunned);
    }

    public byte isSwimming() {
        return getByteTag(ENTITY_NBT_IS_SWIMMING);
    }

    public void setSwimming(byte swimming) {
        setByteTag(ENTITY_NBT_IS_SWIMMING, swimming);
    }

    public byte isTamed() {
        return getByteTag(ENTITY_NBT_IS_TAMED);
    }

    public void setTamed(byte tamed) {
        setByteTag(ENTITY_NBT_IS_TAMED, tamed);
    }

    public byte isTrusting() {
        return getByteTag(ENTITY_NBT_IS_TRUSTING);
    }

    public void setTrusting(byte trusting) {
        setByteTag(ENTITY_NBT_IS_TRUSTING, trusting);
    }

    public byte getLootDropped() {
        return getByteTag(ENTITY_NBT_LOOT_DROPPED);
    }

    public void setLootDropped(byte lootDropped) {
        setByteTag(ENTITY_NBT_LOOT_DROPPED, lootDropped);
    }

    public int getMarkVariant() {
        return getIntTag(ENTITY_NBT_MARK_VARIANT);
    }

    public void setMarkVariant(int markVariant) {
        setIntTag(ENTITY_NBT_MARK_VARIANT, markVariant);
    }

    public byte isOnGround() {
        return getByteTag(ENTITY_NBT_ON_GROUND);
    }

    public void setOnGround(byte onGround) {
        setByteTag(ENTITY_NBT_ON_GROUND, onGround);
    }

    public long getOwnerNew() {
        return getLongTag(ENTITY_NBT_OWNER_NEW);
    }

    public void setOwnerNew(long ownerNew) {
        setLongTag(ENTITY_NBT_OWNER_NEW, ownerNew);
    }

    public int getPortalCooldown() {
        return getIntTag(ENTITY_NBT_PORTAL_COOLDOWN);
    }

    public void setPortalCooldown(int portalCooldown) {
        setIntTag(ENTITY_NBT_PORTAL_COOLDOWN, portalCooldown);
    }

    public byte isSaddled() {
        return getByteTag(ENTITY_NBT_SADDLED);
    }

    public void setSaddled(byte saddled) {
        setByteTag(ENTITY_NBT_SADDLED, saddled);
    }

    public byte isSheared() {
        return getByteTag(ENTITY_NBT_SHEARED);
    }

    public void setSheared(byte sheared) {
        setByteTag(ENTITY_NBT_SHEARED, sheared);
    }

    public byte getShowBottom() {
        return getByteTag(ENTITY_NBT_SHOW_BOTTOM);
    }

    public void setShowBottom(byte showBottom) {
        setByteTag(ENTITY_NBT_SHOW_BOTTOM, showBottom);
    }

    public byte isSitting() {
        return getByteTag(ENTITY_NBT_SITTING);
    }

    public void setSitting(byte sitting) {
        setByteTag(ENTITY_NBT_SITTING, sitting);
    }

    public int getSkinID() {
        return getIntTag(ENTITY_NBT_SKIN_ID);
    }

    public void setSkinID(int skinID) {
        setIntTag(ENTITY_NBT_SKIN_ID, skinID);
    }

    public int getStrength() {
        return getIntTag(ENTITY_NBT_STRENGTH);
    }

    public void setStrength(int strength) {
        setIntTag(ENTITY_NBT_STRENGTH, strength);
    }

    public int getStrengthMax() {
        return getIntTag(ENTITY_NBT_STRENGTH_MAX);
    }

    public void setStrengthMax(int strengthMax) {
        setIntTag(ENTITY_NBT_STRENGTH_MAX, strengthMax);
    }

    public long getUniqueID() {
        return getLongTag(ENTITY_NBT_UNIQUE_ID);
    }

    public void setUniqueID(long uniqueID) {
        setLongTag(ENTITY_NBT_UNIQUE_ID, uniqueID);
    }

    public int getVariant() {
        return getIntTag(ENTITY_NBT_VARIANT);
    }

    public void setVariant(int variant) {
        setIntTag(ENTITY_NBT_VARIANT, variant);
    }

    /**
     * Retrieves a {@link ListTag} from the {@link #parentCompoundTag} in this class, and casts it to a
     * Java {@link List}.
     * @param name The name of the {@link ListTag} in the {@link #parentCompoundTag}
     * @param tagType The tag type that the list represents
     * @param <T> The generic class that the Java {@link List} should represent.
     * @return The Java {@link List} that represents the tag
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getList(String name, TagType tagType) {
        // Create a function from the tag type, that maps a tag to the generic class
        Function<Tag, T> function = null;
        switch (tagType) {
            case TAG_BYTE:
                function = tag -> (T) ((Byte) tag.getAsByte().getValue());
                break;
            case TAG_SHORT:
                function = tag -> (T) ((Short) tag.getAsShort().getValue());
                break;
            case TAG_INT:
                function = tag -> (T) ((Integer) tag.getAsInt().getValue());
                break;
            case TAG_LONG:
                function = tag -> (T) ((Long) tag.getAsLong().getValue());
                break;
            case TAG_FLOAT:
                function = tag -> (T) ((Float) tag.getAsFloat().getValue());
                break;
            case TAG_DOUBLE:
                function = tag -> (T) ((Double) tag.getAsDouble().getValue());
                break;
            case TAG_STRING:
                function = tag -> (T) tag.getAsString().getValue();
                break;
            case TAG_END:
            case TAG_BYTE_ARRAY:
            case TAG_LIST:
            case TAG_COMPOUND:
            case TAG_INT_ARRAY:
            default:
                break;
        }

        // Throw an error if the tag type is invalid/not supported
        if (function == null) {
            throw new InvalidTagTypeException(tagType);
        }
        // Return the NBT tag, but mapped to the Java list
        return getNbtTag(name).getAsList().getElements().stream().map(function).collect(Collectors.toList());
    }

    /**
     * Converts the Java {@link List} to a {@link ListTag} and adds it to the {@link #parentCompoundTag}
     * @param name The name of the {@link ListTag} in the {@link #parentCompoundTag}
     * @param list The Java {@link List} that is required for the conversion.
     * @param tagType The tag type that the list represents
     * @param <T> The generic class that the Java {@link List} represents.
     */
    protected <T> void setList(String name, List<T> list, TagType tagType) {
        // Create a function that maps the Java object to a tag
        Function<T, Tag> function = null;
        switch (tagType) {
            case TAG_BYTE:
                function = value -> new ByteTag(null, (Byte) value);
                break;
            case TAG_SHORT:
                function = value -> new ShortTag(null, (Short) value);
                break;
            case TAG_INT:
                function = value -> new IntTag(null, (Integer) value);
                break;
            case TAG_LONG:
                function = value -> new LongTag(null, (Long) value);
                break;
            case TAG_FLOAT:
                function = value -> new FloatTag(null, (Float) value);
                break;
            case TAG_DOUBLE:
                function = value -> new DoubleTag(null, (Double) value);
                break;
            case TAG_STRING:
                function = value -> new StringTag(null, (String) value);
                break;
            case TAG_END:
            case TAG_BYTE_ARRAY:
            case TAG_LIST:
            case TAG_COMPOUND:
            case TAG_INT_ARRAY:
            default:
                break;
        }

        // Throw an error if the tag type is invalid/not supported
        if (function == null) {
            throw new InvalidTagTypeException(tagType);
        }
        // Edit the parentCompoundTag with the new ListTag
        parentCompoundTag.change(name, new ListTag<>(name, tagType, list.stream().map(function).collect(Collectors.toList())));
    }

    /**
     * Retrieves the NBT tag with the given name from the {@link #parentCompoundTag}
     * @param name The NBT tag name
     * @return The resulting {@link Tag}
     */
    protected Tag getNbtTag(String name) {
        Optional<Tag> tag = parentCompoundTag.getByName(name);
        // If the tag did not exist, throw an exception
        if (!tag.isPresent()) {
            throw new NoSuchTagException(name);
        } else return tag.get();
    }

    // These are some getter methods, that get the tag with the given name, and return it as the requested variable

    protected byte getByteTag(String name) {
        return getNbtTag(name).getAsByte().getValue();
    }

    protected short getShortTag(String name) {
        return getNbtTag(name).getAsShort().getValue();
    }

    protected int getIntTag(String name) {
        return getNbtTag(name).getAsInt().getValue();
    }

    protected long getLongTag(String name) {
        return getNbtTag(name).getAsLong().getValue();
    }

    protected float getFloatTag(String name) {
        return getNbtTag(name).getAsFloat().getValue();
    }

    protected double getDoubleTag(String name) {
        return getNbtTag(name).getAsDouble().getValue();
    }

    protected String getStringTag(String name) {
        return getNbtTag(name).getAsString().getValue();
    }

    // These are some setter methods, that set the tag with the given name, and change it in the parentCompoundTag

    protected void setByteTag(String name, byte value) {
        parentCompoundTag.change(name, new ByteTag(name, value));
    }

    protected void setShortTag(String name, short value) {
        parentCompoundTag.change(name, new ShortTag(name, value));
    }

    protected void setIntTag(String name, int value) {
        parentCompoundTag.change(name, new IntTag(name, value));
    }

    protected void setLongTag(String name, long value) {
        parentCompoundTag.change(name, new LongTag(name, value));
    }

    protected void setFloatTag(String name, float value) {
        parentCompoundTag.change(name, new FloatTag(name, value));
    }

    protected void setDoubleTag(String name, double value) {
        parentCompoundTag.change(name, new DoubleTag(name, value));
    }

    protected void setStringTag(String name, String value) {
        parentCompoundTag.change(name, new StringTag(name, value));
    }
}
