package nl.itslars.kosmos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for representing each Minecraft difficulty.
 */
@AllArgsConstructor
@Getter
public enum Difficulty {

    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3),
    ;

    // The difficulty id.
    private final int id;

    // The list of all dimensions
    private static final Difficulty[] DIFFICULTIES = Difficulty.values();

    /**
     * Retrieves the Difficulty enum based on its id
     * @param id The difficulty ID
     * @return The associated enum value
     */
    public static Difficulty fromId(int id) {
        for (Difficulty difficulty : DIFFICULTIES) {
            if (difficulty.getId() == id) return difficulty;
        }
        return null;
    }
}
