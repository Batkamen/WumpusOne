package hu.nye.progtech.data;

import java.util.Arrays;

/**
 * Direction of the HERO.
 */
public enum HeroDirection {
    UP(-1, 0,  'N'),
    RIGHT(0, 1, 'E'),
    DOWN(1, 0, 'S'),
    LEFT(0, -1, 'W');

    private final int rowOffset;
    private final int columnOffset;
    private final char compassEquivalent;

    HeroDirection(int rowOffset, int columnOffset, char compassEquivalent) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
        this.compassEquivalent = compassEquivalent;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public char getCompassEquivalent() {
        return compassEquivalent;
    }

    /**
     * Get the direction in simple arrow directions.
     *
     * @return String format of the arrow.
     */
    public String getAsArrowText() {
        String ret = null;
        switch (this) {
            case UP -> {
                ret = "/\\";
                break;
            }
            case DOWN -> {
                ret = "\\/";
                break;
            }
            case LEFT -> {
                ret = " <";
                break;
            }
            case RIGHT -> {
                ret =  " >";
                break;
            }
            default -> {
                ret =  "/\\";
            }
        }
        return ret;
    }


    public HeroDirection rotateToLeft() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }

    public HeroDirection rotateToRight() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Create for compass direction N,E,S,W.
     *
     * @param compassDirection - N,E,S,W
     *
     * @return identified {@link HeroDirection}
     */
    public static HeroDirection forCompassDirection(char compassDirection) {
        return Arrays.stream(values())
                .filter(e -> e.compassEquivalent == compassDirection)
                .findFirst()
                .orElse(null);
    }
}
