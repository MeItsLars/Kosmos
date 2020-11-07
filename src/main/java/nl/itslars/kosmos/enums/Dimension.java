package nl.itslars.kosmos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
/**
 * Enum for representing each Minecraft dimension.
 */
public enum Dimension {

    NETHER(1),
    END(2),
    OVERWORLD(-1),
    ;

    // The dimension id.
    private int id;

    // The list of all dimensions
    private static final Dimension[] DIMENSIONS = Dimension.values();

    /**
     * Retrieves the Dimension enum based on its id
     * @param id The dimension ID
     * @return The associated enum value
     */
    public static Dimension fromId(int id) {
        for (Dimension dimension : DIMENSIONS) {
            if (dimension.getId() == id) return dimension;
        }
        return null;
    }
}
