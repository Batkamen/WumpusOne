package hu.nye.progtech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import hu.nye.progtech.bl.FileHelper;
import hu.nye.progtech.bl.GameBL;
import hu.nye.progtech.bl.GameContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private static final String DB_URL = "jdbc:h2:mem:testdb;INIT=RUNSCRIPT FROM 'classpath:db/create.sql'";

    @BeforeEach
    void setup() throws Exception{
        if(GameDAO.connection != null) {
            GameDAO.connection.close();
            GameDAO.connection = null;
        }
        GameDAO.connection = DriverManager.getConnection(DB_URL,"sa","");
    }

    @Test
    void testDbPersistence() {
        assertEquals(new ArrayList<>(), GameDAO.listSavedGames());
        GameBL bl = new GameBL();
        bl.initFromTextFormat(FileHelper.readLines("wumpus.board-test.txt"));
        bl.getContext().setPlayerName("Hello1");
        GameContext ctx1 = bl.getContext();
        GameDAO.saveGame(bl.getContext());

        bl.initFromTextFormat(FileHelper.readLines("wumpus.board-test.txt"));
        bl.getContext().setPlayerName("Hello2");
        GameContext ctx2 = bl.getContext();
        GameDAO.saveGame(bl.getContext());

        List<GameContext> saved = GameDAO.listSavedGames();

        GameContext ctx1Saved = saved.stream().filter(c->c.getGameId().equals(ctx1.getGameId())).findFirst().orElse(null);
        assertNotNull(ctx1Saved);

        GameContext ctx2Saved = saved.stream().filter(c->c.getGameId().equals(ctx2.getGameId())).findFirst().orElse(null);
        assertNotNull(ctx2Saved);

        GameDAO.deleteGame(ctx2);
        saved = GameDAO.listSavedGames();

        ctx1Saved = saved.stream().filter(c->c.getGameId().equals(ctx1.getGameId())).findFirst().orElse(null);
        assertNotNull(ctx1Saved);

        ctx2Saved = saved.stream().filter(c->c.getGameId().equals(ctx2.getGameId())).findFirst().orElse(null);
        assertNull(ctx2Saved);

    }

    @Test
    void testGetConnection() throws  Exception{
        Connection c = DriverManager.getConnection(DB_URL,"sa","");
        GameDAO.connection = c;

        assertEquals(c, GameDAO.getConnection());

        GameDAO.connection = null;

        assertNotNull(GameDAO.getConnection());

    }

}