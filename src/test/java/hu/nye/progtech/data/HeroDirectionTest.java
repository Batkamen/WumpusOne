package hu.nye.progtech.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeroDirectionTest {

    @Test
    void getToLeft() {

        assertEquals(HeroDirection.LEFT, HeroDirection.UP.rotateToLeft());
        assertEquals(HeroDirection.DOWN, HeroDirection.LEFT.rotateToLeft());
        assertEquals(HeroDirection.RIGHT, HeroDirection.DOWN.rotateToLeft());
        assertEquals(HeroDirection.UP, HeroDirection.RIGHT.rotateToLeft());
    }

    @Test
    void getToRight() {
        assertEquals(HeroDirection.LEFT, HeroDirection.DOWN.rotateToRight());
        assertEquals(HeroDirection.UP, HeroDirection.LEFT.rotateToRight());
        assertEquals(HeroDirection.RIGHT, HeroDirection.UP.rotateToRight());
        assertEquals(HeroDirection.DOWN, HeroDirection.RIGHT.rotateToRight());
    }

    @Test
    void getCompassEquivalent() {
        assertEquals(HeroDirection.DOWN,HeroDirection.forCompassDirection('S'));
        assertEquals(HeroDirection.UP,HeroDirection.forCompassDirection('N'));
        assertEquals(HeroDirection.RIGHT,HeroDirection.forCompassDirection('E'));
        assertEquals(HeroDirection.LEFT,HeroDirection.forCompassDirection('W'));
    }

    @Test
    void getAsArrowText() {
        assertEquals("/\\",HeroDirection.UP.getAsArrowText());
        assertEquals("\\/",HeroDirection.DOWN.getAsArrowText());
        assertEquals(" <",HeroDirection.LEFT.getAsArrowText());
        assertEquals(" >",HeroDirection.RIGHT.getAsArrowText());

    }


    @Test
    void forCompassDirection() {
        assertEquals(HeroDirection.UP,HeroDirection.forCompassDirection('N'));
        assertEquals(HeroDirection.DOWN,HeroDirection.forCompassDirection('S'));
        assertEquals(HeroDirection.LEFT,HeroDirection.forCompassDirection('W'));
        assertEquals(HeroDirection.RIGHT,HeroDirection.forCompassDirection('E'));
        assertNull(HeroDirection.forCompassDirection('X'));
    }
}