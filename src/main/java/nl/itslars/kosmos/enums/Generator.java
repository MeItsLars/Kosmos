package nl.itslars.kosmos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for representing each Minecraft generator.
 */
@AllArgsConstructor
@Getter
public enum Generator {

    OLD(0),
    INFINITE(1),
    FLAT(2),
    ;

    // The generator id.
    private final int id;

    // The list of all generators
    private static final Generator[] GENERATORS = Generator.values();

    /**
     * Retrieves the Generator enum based on its id
     * @param id The generator ID
     * @return The associated enum value
     */
    public static Generator fromId(int id) {
        for (Generator generator : GENERATORS) {
            if (generator.getId() == id) return generator;
        }
        return null;
    }
}
