package hu.nye.progtech.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeroStatusTest {

    @Test
    void loseArrow() {
        HeroStatus hs = new HeroStatus();
        hs.setArrows(5);
        hs.loseArrow();
        assertEquals(4,hs.getArrows());

        hs.setArrows(0);
        hs.loseArrow();
        assertEquals(0, hs.getArrows());
    }
}