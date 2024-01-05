package hu.nye.progtech.bl;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {

    @Test
    void readLines() {
        List<String> lines = FileHelper.readLines("wumpus.board-test.txt");

        assertEquals("6 B 5 E",lines.get(0));
        assertEquals("WUGP_W",lines.get(3));
    }
}