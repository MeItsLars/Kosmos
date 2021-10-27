package nl.itslars.kosmos.objects.entity;

import nl.itslars.mcpenbt.enums.TagType;
import nl.itslars.mcpenbt.tags.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static nl.itslars.kosmos.util.EntityNBTConstants.ENTITY_NBT_IDENTIFIER;

/**
 * A simple class for adding new custom entities to the world.
 */
public class CustomEntity extends Entity {


    public CustomEntity(CompoundTag parentCompoundTag) {
        super(parentCompoundTag);
    }

    /**
     *
     * @param identifier entity identifier
     * @param x x
     * @param y y
     * @param z z
     * @param componentGroups added component groups
     */
    public CustomEntity(String identifier, float x, float y, float z, String... componentGroups) {
        this(identifier, x, y, z, Arrays.asList(componentGroups));
    }

    /**
     *
     * @param identifier entity identifier
     * @param x x
     * @param y y
     * @param z z
     * @param componentGroups added component groups
     */
    public CustomEntity(String identifier, float x, float y, float z, List<String> componentGroups) {
        super(getEmpty());
        setIdentifier(identifier);
        ArrayList<String> defs = new ArrayList<>();
        defs.add("+" + identifier);
        defs.addAll(componentGroups.stream().map(s -> "+" + s).collect(Collectors.toList()));
        setDefinitions(defs);
        setPosition(x, y, z);
        setRotation(0, 0);
    }

    protected void setIdentifier(String identifier) {
        setStringTag(ENTITY_NBT_IDENTIFIER, identifier);
    }

    private static CompoundTag getEmpty() {
        return new CompoundTag("", new ArrayList<>(Arrays.asList(
                new ByteTag("canPickupItems", (byte) 0),
                new ByteTag("Chested", (byte) 0),
                new ByteTag("Color", (byte) 0),
                new ByteTag("Color2", (byte) 0),
                new ByteTag("CustomNameVisible", (byte) 0),
                new ByteTag("Dead", (byte) 0),
                new ByteTag("hasBoundOrigin", (byte) 0),
                new ByteTag("hasSetCanPickupItems", (byte) 0),
                new ByteTag("Invulnerable", (byte) 0),
                new ByteTag("IsAngry", (byte) 0),
                new ByteTag("IsAutonomous", (byte) 0),
                new ByteTag("IsBaby", (byte) 0),
                new ByteTag("IsEating", (byte) 0),
                new ByteTag("IsGliding", (byte) 0),
                new ByteTag("IsGlobal", (byte) 0),
                new ByteTag("IsIllagerCaptain", (byte) 0),
                new ByteTag("IsOrphaned", (byte) 0),
                new ByteTag("IsOutOfControl", (byte) 0),
                new ByteTag("IsPregnant", (byte) 0),
                new ByteTag("IsRoaring", (byte) 0),
                new ByteTag("IsScared", (byte) 0),
                new ByteTag("IsStunned", (byte) 0),
                new ByteTag("IsSwimming", (byte) 0),
                new ByteTag("IsTamed", (byte) 0),
                new ByteTag("IsTrusting", (byte) 0),
                new ByteTag("LootDropped", (byte) 0),
                new ByteTag("NaturalSpawn", (byte) 0),
                new ByteTag("OnGround", (byte) 0),
                new ByteTag("Persistent", (byte) 1),
                new ByteTag("Saddled", (byte) 0),
                new ByteTag("Sheared", (byte) 0),
                new ByteTag("ShowBottom", (byte) 0),
                new ByteTag("Sitting", (byte) 0),
                new ByteTag("Surface", (byte) 0),

                new FloatTag("BodyRot", 0),
                new FloatTag("FallDistance", 0),

                new IntTag("boundX", 0),
                new IntTag("boundY", 0),
                new IntTag("boundZ", 0),
                new IntTag("LastDimensionId", 0),
                new IntTag("MarkVariant", 0),
                new IntTag("PortalCooldown", 0),
                new IntTag("SkinID", 0),
                new IntTag("Strength", 0),
                new IntTag("StrengthMax", 0),
                new IntTag("TradeExperience", 0),
                new IntTag("TradeTier", 0),
                new IntTag("Variant", 0),

                new ListTag<>("Armor", TagType.TAG_COMPOUND, new ArrayList<>()),
                new ListTag<>("Attributes", TagType.TAG_COMPOUND, new ArrayList<>()),
                new ListTag<>("definitions", TagType.TAG_STRING, new ArrayList<>()),
                new ListTag<>("Mainhand", TagType.TAG_COMPOUND, new ArrayList<>()),
                new ListTag<>("Offhand", TagType.TAG_COMPOUND, new ArrayList<>()),
                new ListTag<>("Pos", TagType.TAG_FLOAT, new ArrayList<>(Arrays.asList(new FloatTag("", 0), new FloatTag("", 0), new FloatTag("", 0)))),
                new ListTag<>("Rotation", TagType.TAG_FLOAT, new ArrayList<>(Arrays.asList(new FloatTag("", 0), new FloatTag("", 0)))),
                new ListTag<>("Tags", TagType.TAG_END, new ArrayList<>()),

                new LongTag("LeasherID", -1),
                new LongTag("OwnerNew", -1),
                new LongTag("TargetID", -1),
                new LongTag("UniqueID", new Random().nextLong()),

                new ShortTag("AttackTime", (short) 0),
                new ShortTag("DeathTime", (short) 0),
                new ShortTag("Fire", (short) 0),
                new ShortTag("HurtTime", (short) 0),

                new StringTag("CustomName", ""),
                new StringTag("identifier", "")
        )));
    }

}
