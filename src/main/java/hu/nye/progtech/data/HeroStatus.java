package hu.nye.progtech.data;

/**
 * Represents the status of the hero.
 */
public class HeroStatus {

    int row = 0;
    int column = 0;
    HeroDirection direction = HeroDirection.UP;
    boolean goldCollected = false;
    int arrows = 0;
    boolean died = false;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public HeroDirection getDirection() {
        return direction;
    }

    public void setDirection(HeroDirection direction) {
        this.direction = direction;
    }

    public boolean hasGoldCollected() {
        return goldCollected;
    }

    public void setGoldCollected(boolean goldCollected) {
        this.goldCollected = goldCollected;
    }

    public int getArrows() {
        return arrows;
    }

    public void setArrows(int arrows) {
        this.arrows = arrows;
    }

    public boolean hasDied() {
        return died;
    }

    public void setDied(boolean died) {
        this.died = died;
    }

    /**
     * Loose an arrow...decrease arrow
     */
    public void loseArrow() {
        if (arrows > 0) {
            arrows--;
        }
    }

    @Override
    public String toString() {
        return "HeroStatus{" +
                "row=" + row +
                ", column=" + column +
                ", direction=" + direction +
                ", goldCollected=" + goldCollected +
                ", arrows=" + arrows +
                ", died=" + died +
                '}';
    }
}
