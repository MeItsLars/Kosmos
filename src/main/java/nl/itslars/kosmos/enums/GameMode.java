package nl.itslars.kosmos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for representing each game mode.
 */
@AllArgsConstructor
@Getter
public enum GameMode {

    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    ;

    // The game mode id.
    private final int id;

    // The list of all game modes
    private static final GameMode[] GAME_MODES = GameMode.values();

    /**
     * Retrieves the GameMode enum based on its id
     * @param id The game mode ID
     * @return The associated enum value
     */
    public static GameMode fromId(int id) {
        for (GameMode gameMode : GAME_MODES) {
            if (gameMode.getId() == id) return gameMode;
        }
        return null;
    }
}
