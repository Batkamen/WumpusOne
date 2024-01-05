package hu.nye.progtech.bl;

import hu.nye.progtech.data.HeroDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBLTest {

    GameBL bl = null;

    @BeforeEach
    void setup(){
        bl = new GameBL();
        bl.initFromTextFormat(FileHelper.readLines("wumpus.board-test.txt"));

    }

    @Test
    void rotateLeft() {
        bl.getContext().getHero().setDirection(HeroDirection.UP);
        bl.rotateLeft();
        assertEquals(HeroDirection.LEFT, bl.getContext().getHero().getDirection());
        bl.getContext().getHero().setDirection(HeroDirection.DOWN);
        bl.rotateLeft();
        assertEquals(HeroDirection.RIGHT, bl.getContext().getHero().getDirection());
    }

    @Test
    void rotateRight() {
        bl.getContext().getHero().setDirection(HeroDirection.UP);
        bl.rotateRight();
        assertEquals(HeroDirection.RIGHT, bl.getContext().getHero().getDirection());
        bl.getContext().getHero().setDirection(HeroDirection.DOWN);
        bl.rotateRight();
        assertEquals(HeroDirection.LEFT, bl.getContext().getHero().getDirection());
    }

    @Test
    void move() {
        bl.start();
        //up
        int r = bl.getContext().getHero().getRow();
        int c = bl.getContext().getHero().getColumn();

        bl.getContext().getHero().setDirection(HeroDirection.UP);
        bl.move();
        assertEquals(r-1, bl.getContext().getHero().getRow());
        assertEquals(c, bl.getContext().getHero().getColumn());

        bl.getContext().getHero().setRow(r);
        bl.getContext().getHero().setColumn(c);


        bl.getContext().getHero().setDirection(HeroDirection.RIGHT);
        bl.move();
        assertEquals(r, bl.getContext().getHero().getRow());
        assertEquals(c+1, bl.getContext().getHero().getColumn());

        bl.getContext().getHero().setRow(r);
        bl.getContext().getHero().setColumn(c);

        //step into PIT
        bl.getContext().getHero().setRow(4);
        bl.getContext().getHero().setColumn(2);
        bl.getContext().getHero().setDirection(HeroDirection.RIGHT);
        int arrowCount = bl.getContext().getHero().getArrows();
        bl.move();
        assertEquals(arrowCount - 1, bl.getContext().getHero().getArrows());

        //step on wumpus
        bl.getContext().getHero().setRow(3);
        bl.getContext().getHero().setColumn(1);
        bl.getContext().getHero().setDirection(HeroDirection.UP);
        assertFalse(bl.getContext().getHero().hasDied());
        bl.move();
        assertTrue(bl.getContext().isStopped());
        assertTrue(bl.getContext().getHero().hasDied());



    }

    @Test
    void shoot() {
        bl.getContext().getHero().setDirection(HeroDirection.UP);
        int arrowCount = bl.getContext().getHero().getArrows();
        assertTrue(bl.shoot());
        assertEquals(arrowCount -1, bl.getContext().getHero().getArrows());

        bl.getContext().getHero().setDirection(HeroDirection.RIGHT);
        assertFalse(bl.shoot());
        assertEquals(arrowCount -1, bl.getContext().getHero().getArrows());


    }

    @Test
    void pickUpGold() {
        bl.pickUpGold();
        assertFalse(bl.getContext().getHero().hasGoldCollected());

        bl.getContext().getHero().setRow(2);
        bl.getContext().getHero().setColumn(2);
        bl.pickUpGold();
        assertTrue(bl.getContext().getHero().hasGoldCollected());
    }

    @Test
    void start() {
        assertFalse(bl.getContext().isStarted());
        assertNotEquals(bl.getContext().getHeroInitialRow(), bl.getContext().getHero().getRow());
        assertNotEquals(bl.getContext().getHeroInitialColumn(), bl.getContext().getHero().getColumn());
        bl.start();
        assertTrue(bl.getContext().isStarted());
        assertEquals(bl.getContext().getHeroInitialRow(), bl.getContext().getHero().getRow());
        assertEquals(bl.getContext().getHeroInitialColumn(), bl.getContext().getHero().getColumn());

    }

    @Test
    void gameWon() {
        bl.start();
        bl.getContext().getHero().setRow(2);
        bl.getContext().getHero().setColumn(2);
        bl.pickUpGold();
        bl.getContext().getHero().setRow(3);
        bl.getContext().getHero().setColumn(1);
        bl.getContext().getHero().setDirection(HeroDirection.DOWN);
        bl.move();

        assertTrue(bl.getContext().gameWon());

    }
}