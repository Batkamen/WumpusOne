package hu.nye.progtech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.nye.progtech.bl.GameContext;
import hu.nye.progtech.data.GameBoard;
import hu.nye.progtech.data.HeroDirection;
import hu.nye.progtech.data.HeroStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game persistence object.
 */
public class GameDAO {

    private static final String DB_URL = "jdbc:h2:file:./game.db;INIT=RUNSCRIPT FROM 'classpath:db/create.sql'";

    private static final String INSERT = "INSERT INTO GAMES " +
            "(id , player , moves , hero_init_row , hero_init_column, " +
            "hero_row , hero_column , hero_direction , hero_gold , hero_arrows , board_size , board) " +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final Logger logger = LoggerFactory.getLogger(GameDAO.class);

    static Connection connection;

    static Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        synchronized (GameDAO.class) {
            if (connection == null) {
                try {
                    connection =  DriverManager.getConnection(DB_URL, "sa", "");
                } catch (Exception e) {
                    logger.warn("Failed to init db connection!", e);
                }
            }
        }
        return connection;
    }

    /**
     * List saved games.
     *
     * @return List of {@link GameContext}
     */
    public static List<GameContext> listSavedGames() {
        List<GameContext> ret = new ArrayList<>();

        try (PreparedStatement st = getConnection().prepareStatement("SELECT * from GAMES")) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                GameContext ctx = new GameContext();
                ctx.setGameId(rs.getString("id"));
                ctx.setPlayerName(rs.getString("player"));
                ctx.setMoves(rs.getInt("moves"));
                ctx.setHeroInitialRow(rs.getInt("hero_init_row"));
                ctx.setHeroInitialColumn(rs.getInt("hero_init_column"));
                ctx.setHero(new HeroStatus());
                ctx.getHero().setRow(rs.getInt("hero_row"));
                ctx.getHero().setColumn(rs.getInt("hero_column"));
                ctx.getHero().setDirection(HeroDirection.forCompassDirection(rs.getString("hero_direction").charAt(0)));
                ctx.getHero().setArrows(rs.getInt("hero_arrows"));
                ctx.getHero().setGoldCollected(rs.getBoolean("hero_gold"));
                int boardSize = rs.getInt("board_size");
                ctx.setGameBoard(GameBoard.create(boardSize));

                ctx.getGameBoard().initFromString(Arrays.stream(rs.getString("board").split("\\|")).toList());

                ret.add(ctx);
            }
        } catch (Exception e) {
            logger.warn("Failed to load saved games from db.", e);
        }

        return ret;
    }

    /**
     * Save game to db.
     *
     * @param context - to be saved
     * @return true if saving succeeded.
     */
    public static boolean saveGame(GameContext context) {
        boolean ret = false;
        //just drop the prev version for simplicity
        deleteGame(context);

        //insert
        try (PreparedStatement st = getConnection().prepareStatement(INSERT)) {
            st.setString(1, context.getGameId());
            st.setString(2, context.getPlayerName());
            st.setInt(3, context.getMoves());
            st.setInt(4, context.getHeroInitialRow());
            st.setInt(5, context.getHeroInitialColumn());
            st.setInt(6, context.getHero().getRow());
            st.setInt(7, context.getHero().getColumn());
            st.setString(8, context.getHero().getDirection().getCompassEquivalent() + "");
            st.setBoolean(9, context.getHero().hasGoldCollected());
            st.setInt(10, context.getHero().getArrows());
            st.setInt(11, context.getGameBoard().getSize());
            st.setString(12, String.join("|", context.getGameBoard().getBoardAsString()));
            ret = st.executeUpdate() > 0;
        } catch (Exception e) {
            logger.warn("Failed to save data! ", e);
        }
        return ret;
    }


    /**
     * Delete game, make sure the delete is executed.
     *
     * @param context - to be deleted
     */
    public static void deleteGame(GameContext context) {
        boolean ret = false;
        //just drop the prev version for simplicity
        try (PreparedStatement st = getConnection().prepareStatement("DELETE FROM GAMES WHERE id=?")) {
            st.setString(1, context.getGameId());
            ret = st.executeUpdate() > 0;
        } catch (Exception e) {
            logger.warn("Failed to clear before update!", e);
        }
    }
}
