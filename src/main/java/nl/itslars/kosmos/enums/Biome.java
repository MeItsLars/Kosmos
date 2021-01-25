package nl.itslars.kosmos.enums;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;

/**
 * Enum for representing each biome as of 1.16.
 */
@AllArgsConstructor
@Getter
@JsonAdapter(Biome.BiomeAdapter.class)
public enum Biome {

    OCEAN(42),
    LEGACY_FROZEN_OCEAN(43),
    DEEP_OCEAN(24),
    FROZEN_OCEAN(10),
    DEEP_FROZEN_OCEAN(50),
    COLD_OCEAN(46),
    DEEP_COLD_OCEAN(49),
    LUKEWARM_OCEAN(45),
    DEEP_LUKEWARM_OCEAN(48),
    WARM_OCEAN(44),
    DEEP_WARM_OCEAN(47),
    RIVER(7),
    FROZEN_RIVER(11),
    BEACH(16),
    STONE_BEACH(25),
    COLD_BEACH(26),
    FOREST(4),
    FOREST_HILLS(18),
    FLOWER_FOREST(132),
    BIRCH_FOREST(27),
    BIRCH_FOREST_HILLS(28),
    BIRCH_FOREST_MUTATED(155),
    BIRCH_FOREST_HILLS_MUTATED(156),
    ROOFED_FOREST(29),
    ROOFED_FOREST_MUTATED(157),
    JUNGLE(21),
    JUNGLE_HILLS(22),
    JUNGLE_MUTATED(149),
    JUNGLE_EDGE(23),
    JUNGLE_EDGE_MUTATED(151),
    BAMBOO_JUNGLE(168),
    BAMBOO_JUNGLE_HILLS(169),
    TAIGA(5),
    TAIGA_HILLS(19),
    TAIGA_MUTATED(133),
    COLD_TAIGA(30),
    COLD_TAIGA_HILLS(31),
    COLD_TAIGA_MUTATED(158),
    MEGA_TAIGA(32),
    MEGA_TAIGA_HILLS(33),
    REDWOOD_TAIGA_MUTATED(160),
    REDWOOD_TAIGA_HILLS_MUTATED(161),
    MUSHROOM_ISLAND(14),
    MUSHROOM_ISLAND_SHORE(15),
    SWAMPLAND(6),
    SWAMPLAND_MUTATED(134),
    SAVANNA(35),
    SAVANNA_PLATEAU(36),
    SAVANNA_MUTATED(163),
    SAVANNA_PLATEAU_MUTATED(164),
    PLAINS(1),
    SUNFLOWER_PLAINS(129),
    DESERT(2),
    DESERT_HILLS(17),
    DESERT_MUTATED(130),
    ICE_PLAINS(12),
    ICE_MOUNTAINS(13),
    ICE_PLAINS_SPIKES(140),
    EXTREME_HILLS(3),
    EXTREME_HILLS_PLUS_TREES(34),
    EXTREME_HILLS_MUTATED(131),
    EXTREME_HILLS_PLUS_TREES_MUTATED(162),
    EXTREME_HILLS_EDGE(20),
    MESA(37),
    MESA_PLATEAU(39),
    MESA_PLATEAU_MUTATED(167),
    MESA_PLATEAU_STONE(38),
    MESA_PLATEAU_STONE_MUTATED(166),
    MESA_BRYCE(165),
    HELL(8),
    CRIMSON_FOREST(179),
    WARPED_FOREST(180),
    SOUL_SAND_VALLEY(178),
    BASALT_DELTAS(181),
    THE_END(9);

    //The biome id.
    private final int id;

    // The list of all biomes
    private static final Biome[] BIOMES = Biome.values();

    /**
     * Retrieves the Biome enum based on its id
     * @param id The biome ID
     * @return The associated enum value
     */
    public static Biome fromId(int id) {
        for (Biome biome : BIOMES) {
            if (biome.getId() == id) return biome;
        }
        return null;
    }


    /**
     * Biome adapter for serializing and deserializing biome as an ID
     */
    public static class BiomeAdapter implements JsonSerializer<Biome>, JsonDeserializer<Biome> {
        @Override
        public JsonElement serialize(Biome src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.getId());
        }

        @Override
        public Biome deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            try {
                return fromId(json.getAsNumber().intValue());
            } catch (JsonParseException e) {
                e.printStackTrace();
                return PLAINS;
            }
        }
    }
}
