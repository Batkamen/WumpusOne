package hu.nye.progtech.bl;

import java.time.LocalDateTime;
import java.util.UUID;

import hu.nye.progtech.data.GameBoard;
import hu.nye.progtech.data.HeroStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class holding the current status of the Game.
 */
public class GameContext {

    private static final Logger logger = LoggerFactory.getLogger(GameContext.class);

    private String gameId = UUID.randomUUID().toString();
    private String playerName;
    private HeroStatus hero;
    private GameBoard gameBoard;

    private int heroInitialRow = -1;
    private int heroInitialColumn = -1;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int moves = 0;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public HeroStatus getHero() {
        return hero;
    }

    public void setHero(HeroStatus hero) {
        this.hero = hero;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public void recordMove() {
        this.moves++;
    }

    public boolean isStarted() {
        return startTime != null;
    }

    /**
     * Record the start of the game. This will start the
     */
    public void start() {
        if (isStarted()) {
            logger.warn("Game already started at {}", startTime);
            return;
        }
        if (heroInitialRow == -1) {
            heroInitialRow = this.hero.getRow();
        }
        if (heroInitialColumn == -1) {
            heroInitialColumn = this.hero.getColumn();
        }
        startTime = LocalDateTime.now();
        logger.info("Game started at {}", startTime);
    }

    /**
     * Record stop of the game.
     */
    public void stop() {
        if (!isStarted()) {
            logger.warn("Cannot stop a not started game!");
            return;
        }
        if (isStopped()) {
            logger.warn("Game already stopped at {}", endTime);
            return;
        }

        endTime = LocalDateTime.now();
        logger.info("Game ended at {}", endTime);
    }

    public boolean isStopped() {
        return isStarted() && endTime != null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setHeroInitialRow(int heroInitialRow) {
        this.heroInitialRow = heroInitialRow;
    }

    public void setHeroInitialColumn(int heroInitialColumn) {
        this.heroInitialColumn = heroInitialColumn;
    }

    public int getHeroInitialRow() {
        return heroInitialRow;
    }

    public int getHeroInitialColumn() {
        return heroInitialColumn;
    }


    /**
     * Check if the gave has been wor.
     *
     * @return true if gold collected and hero is on initial location.
     */
    public boolean gameWon() {
        return isStarted() && getHero().hasGoldCollected()
                &&  getHero().getRow() == getHeroInitialRow()
                && getHero().getColumn() == getHeroInitialColumn();
    }

    @Override
    public String toString() {
        return "GameContext{" +
                "gameId='" + gameId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", hero=" + hero +
                ", gameBoard=" + gameBoard +
                ", heroInitialRow=" + heroInitialRow +
                ", heroInitialColumn=" + heroInitialColumn +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", moves=" + moves +
                '}';
    }
}
