package hu.nye.progtech.bl;

import java.util.Collections;
import java.util.List;

import hu.nye.progtech.data.GameBoard;
import hu.nye.progtech.data.GameBoardColumn;
import hu.nye.progtech.data.GameBoardSlotType;
import hu.nye.progtech.data.HeroDirection;
import hu.nye.progtech.data.HeroStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business logic of the game.
 */
public class GameBL {

    private static final Logger logger = LoggerFactory.getLogger(GameBL.class);
    private GameContext context;

    public static final List<String> GAME_HEADER = Collections.unmodifiableList(FileHelper.readLines("headers/wumpus.logo.txt"));

    public GameContext getContext() {
        return context;
    }

    public void setContext(GameContext context) {
        this.context = context;
    }

    /**
     * Rotate HERO to left.
     */
    public void rotateLeft() {
        context.getHero().setDirection(context.getHero().getDirection().rotateToLeft());
        logger.info("Hero rotated left, direction changed to {}", context.getHero().getDirection());
        context.recordMove();
    }

    /**
     * Rotate HERO to right.
     */
    public void rotateRight() {
        context.getHero().setDirection(context.getHero().getDirection().rotateToRight());
        logger.info("Hero rotated right, direction changed to {}", context.getHero().getDirection());
        context.recordMove();
    }

    /**
     * Move the HERO one step in the direction it is facing.
     */
    public void move() {
        HeroStatus hero = context.getHero();
        GameBoard board = context.getGameBoard();
        int newRow = hero.getRow() + hero.getDirection().getRowOffset();
        int newColumn = hero.getColumn() + hero.getDirection().getColumnOffset();

        if (board.canMoveTo(newRow, newColumn)) {
            hero.setRow(newRow);
            hero.setColumn(newColumn);
            logger.info("Hero moved to row={}, column={}", newRow, newColumn);
            context.recordMove();
        } else {
            logger.warn("Hero cannot move to row={}, column={}. Position invalid!", newRow, newColumn);
        }
        //check died
        checkHeroDied();
        if (hero.hasDied()) {
            //game over stop timer
            context.stop();
            logger.info("Hero has died, so game has ended!");
            return;
        }

        if (heroInPit()) {
            hero.loseArrow();
            logger.info("Hero has fallen into the pit, and lost one arrow!");
        }

        if (isGameCompleted()) {
            context.stop();
            logger.info("Hero has collected gold and is back on initial location! Game Over.");
        }
    }

    private boolean isGameCompleted() {
        return this.context.gameWon();
    }

    private boolean heroInPit() {
        HeroStatus hero = context.getHero();
        GameBoard board = context.getGameBoard();

        GameBoardSlotType slot = board.getItemOnLocation(hero.getRow(), hero.getColumn());
        return slot == GameBoardSlotType.PIT;
    }

    private void checkHeroDied() {
        HeroStatus hero = context.getHero();
        GameBoard board = context.getGameBoard();

        GameBoardSlotType slot = board.getItemOnLocation(hero.getRow(), hero.getColumn());
        hero.setDied(hero.hasDied() || slot == GameBoardSlotType.WUMPUS);
        logger.info("Hero died={}", hero.hasDied());
    }

    /**
     * Shoot with arrow in the direction Hero is facing.
     *
     * @return true if Wumpus hit, and false otherwise.
     */
    public boolean shoot() {
        HeroStatus hero = context.getHero();
        if (hero.getArrows()  == 0) {
            logger.warn("Hero cannot shoot, has no arrows!");
            return false;
        }
        //decrease arrow count
        hero.setArrows(hero.getArrows() - 1);

        GameBoard board = context.getGameBoard();
        GameBoardSlotType[] slotsInFrontOfHero = board.listSlots(hero.getRow(),
                hero.getColumn(),
                hero.getDirection().getRowOffset(),
                hero.getDirection().getColumnOffset());

        int wumpusDeadOnPosition = -1;
        for (int i = 0; i < slotsInFrontOfHero.length; i++) {
            if (GameBoardSlotType.WUMPUS == slotsInFrontOfHero[i]) {
                wumpusDeadOnPosition = i;
                break;
            }
        }

        if (wumpusDeadOnPosition > -1) {
            int wumpusRow = hero.getRow() + (wumpusDeadOnPosition + 1) * hero.getDirection().getRowOffset();
            int wumpusColumn = hero.getColumn() + (wumpusDeadOnPosition + 1) * hero.getDirection().getColumnOffset();

            logger.info("Hero killed on position:{}-{}", wumpusRow, wumpusColumn);
            board.setItemOnLocation(wumpusRow, wumpusColumn, GameBoardSlotType.EMPTY);
            return true;
        }
        return false;
    }

    /**
     * Pick up Gold if Gold is available in current location.
     *
     * @return true of gold is picked
     */
    public boolean pickUpGold() {
        HeroStatus hero = context.getHero();
        if (hero.hasGoldCollected()) {
            logger.warn("Hero already has gold collected!");
            return false;
        }
        GameBoard board = context.getGameBoard();
        GameBoardSlotType slot = board.getItemOnLocation(hero.getRow(), hero.getColumn());
        hero.setGoldCollected(slot == GameBoardSlotType.GOLD);

        if (hero.hasGoldCollected()) {
            //clear gold
            board.setItemOnLocation(hero.getRow(), hero.getColumn(), GameBoardSlotType.EMPTY);
            logger.info("Hero collected gold!");
        } else {
            logger.info("No Gold in position {}-{}", hero.getRow(), hero.getColumn());
        }
        return hero.hasGoldCollected();
    }

    /**
     * Init Game from Text file format.
     *
     * @param gameFromFile - the contents of the file in List format.
     */
    public void initFromTextFormat(List<String> gameFromFile) {
        this.context = null;
        String firstLine = gameFromFile.stream().filter(l -> !l.isBlank()).findFirst().orElse(null);
        GameContext newContext = new GameContext();
        if (firstLine != null) {
            //first line contains Hero location and board size
            String[] elements = firstLine.split(" ");
            if (elements.length < 4) {
                logger.warn("Invalid header format {}", firstLine);
                return;
            }
            //first element is size of the board
            try {
                int boardSize = Integer.parseInt(elements[0].trim());
                newContext.setGameBoard(GameBoard.create(boardSize));
            } catch (Exception e) {
                logger.warn("Invalid board size!", e);
                return;
            }
            HeroStatus hero = new HeroStatus();
            newContext.setHero(hero);
            //elements 2,3,4 are hero Column, Row, direction
            GameBoardColumn column = GameBoardColumn.fromLabel(elements[1].trim().charAt(0));
            if (column == null) {
                logger.warn("Invalid column! {}", elements[1]);
                return;
            }

            try {
                int row = Integer.parseInt(elements[2].trim());
                hero.setRow(row - 1); //text file is 1 based.
                hero.setColumn(column.ordinal());
            } catch (Exception e) {
                logger.warn("Invalid hero column!", e);
                return;
            }

            HeroDirection direction = HeroDirection.forCompassDirection(elements[3].trim().charAt(0));
            if (direction == null) {
                logger.warn("Invalid direction specified {}, defaulting to UP", elements[3]);
                direction = HeroDirection.UP;
            }
            hero.setDirection(direction);

            //fill board and set arrows
            int boardSize = newContext.getGameBoard().getSize();
            List<String> boardLines = gameFromFile.stream().filter(l -> l.startsWith("W") && l.endsWith("W")).toList();
            if (boardLines.size() < boardSize) {
                logger.warn("Invalid board definition! {}", boardLines);
                return;
            }
            newContext.getGameBoard().initFromString(boardLines);

            //set arrows
            hero.setArrows(newContext.getGameBoard().countWumpus());

            logger.info("Game initialized {}", newContext);
            this.context = newContext;
        } else {
            logger.warn("Failed to init from text {}", gameFromFile);
        }
    }

    public void start() {
        this.context.start();
    }

}
