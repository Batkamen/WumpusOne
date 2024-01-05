package hu.nye.progtech.data;

import java.util.Arrays;

/**
 * Definition of the possible items of the Wumpus game board.
 */
public enum GameBoardSlotType {

    EMPTY("_"),
    GOLD("G"),
    HERO("H"),
    WUMPUS("U"),
    PIT("P"),
    WALL("W");

    private final String value;

    GameBoardSlotType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Identify {@link GameBoardSlotType} for a given value.
     *
     * @param value - value
     * @return identified {@link GameBoardSlotType} or null
     */
    public static GameBoardSlotType fromValue(String value) {
        return Arrays.stream(values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "GameBoardSlotType{" +
                "value='" + value + '\'' +
                '}';
    }
}
