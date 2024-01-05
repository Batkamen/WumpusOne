package hu.nye.progtech.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing the contents of the board.
 */
public class GameBoard {

    public static final int MIN_SIZE = 6;
    public static final int MAX_SIZE = 20;
    private final GameBoardSlotType[][] board;

    private GameBoard(int size) {
        board = new GameBoardSlotType[size][size];
    }


    /**
     * Return the size of the board.
     *
     * @return size of the board.
     */
    public int getSize() {
        return board.length;
    }

    /**
     * Obtain the element on given location.
     *
     * @param row 0 based
     * @param column 0 base
     * @return FieldType on given location
     * @throws IllegalArgumentException if row or column not in the allowed range
     */
    public GameBoardSlotType getItemOnLocation(int row, int column) throws IllegalArgumentException {
        validateCoordinate(row, column);
        return this.board[row][column];
    }


    /**
     * Set the value of the specified coordinate.
     *
     * @param row 0 based index
     * @param column 0 based index
     * @param slotType type of the slot
     * @throws IllegalArgumentException if row or column not in the allowed range or @slotType is null
     */
    public void setItemOnLocation(int row, int column, GameBoardSlotType slotType) throws IllegalArgumentException {
        validateCoordinate(row, column);
        if (slotType == null) {
            throw new IllegalArgumentException("Field type cannot be null!");
        }

        if (GameBoardSlotType.WALL == this.board[row][column]) {
            throw new IllegalArgumentException("Wall cannot be changed!");
        }

        this.board[row][column] = slotType;
    }

    /**
     * Validate if the specified coordinate is in the allowed range.
     *
     * @param row 0 based
     * @param column 0 based
     * @throws IllegalArgumentException if the coordinate is not in the allowed range
     */
    private void validateCoordinate(int row, int column) throws  IllegalArgumentException {
        if (row < 0 || row >= getSize()) {
            throw new IllegalArgumentException("Invalid row value! Should be between 0<=x<" + getSize());
        }
        if (column < 0 || column >= getSize()) {
            throw new IllegalArgumentException("Invalid column value! Should be between 0<=x<" + getSize());
        }
    }

    /**
     * Create an empty bopard with the specified size, filled with empty slots and surrounded by walls.
     *
     * @param size of the board
     * @return created {@link GameBoard} never null
     * @throws IllegalArgumentException if the size is less then {@link #MIN_SIZE} and bigger than {@link #MAX_SIZE}
     */
    public static GameBoard create(int size) throws IllegalArgumentException {
        if (size < MIN_SIZE) {
            throw new IllegalArgumentException("Board cannot be smaller than 6");
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException("Board cannot be larger than 20");
        }
        GameBoard ret = new GameBoard(size);
        //fill whole with empty
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                ret.board[row][column] = GameBoardSlotType.EMPTY;
            }
        }
        //create walls
        for (int column = 0; column < size; column++) {
            ret.board[0][column] = GameBoardSlotType.WALL;
            ret.board[size - 1][column] = GameBoardSlotType.WALL;
        }
        for (int row = 0; row < size; row++) {
            ret.board[row][0] = GameBoardSlotType.WALL;
            ret.board[row][size - 1] = GameBoardSlotType.WALL;
        }

        return ret;
    }

    /**
     * Check if Hero can move to specified location.
     *
     * @param newRow - new row
     * @param newColumn - new column
     * @return true if movement is possible
     */
    public boolean canMoveTo(int newRow, int newColumn) {
        try {
            validateCoordinate(newRow, newColumn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * List the slots from given location with specified offsets.
     *
     * @param fromRow - fromRow
     * @param fromColumn - to
     * @param rowOffset - row offset
     * @param columnOffset - col offset
     * @return Array of slots
     */
    public GameBoardSlotType[] listSlots(int fromRow, int fromColumn, int rowOffset, int columnOffset) {
        List<GameBoardSlotType> ret = new ArrayList<>();
        boolean wallHit = false;

        if (rowOffset == 0 && columnOffset == 0) {
            //just to prevent inifinite loop
            return  new GameBoardSlotType[0];
        }

        if (columnOffset == 0) {
            for (int r = fromRow + rowOffset; r >= 0 && r < board.length; r += rowOffset) {
                GameBoardSlotType slot = board[r][fromColumn];
                if (GameBoardSlotType.WALL == slot) {
                    break;
                }
                ret.add(slot);
            }
        } else {
            for (int c = fromColumn + columnOffset; c >= 0 && c < board.length; c += columnOffset) {
                GameBoardSlotType slot = board[fromRow][c];
                if (GameBoardSlotType.WALL == slot) {
                    break;
                }
                ret.add(slot);

            }
        }
        return ret.toArray(new GameBoardSlotType[ret.size()]);
    }

    /**
     * Count how many wumpus-es are on board.
     *
     * @return the number counted
     */
    public int countWumpus() {
        int ret = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == GameBoardSlotType.WUMPUS) {
                    ret++;
                }
            }
        }
        return ret;
    }

    /**
     * Obtain the board in string format.
     *
     * @return the board in string format line by line.
     */
    public List<String> getBoardAsString() {
        List<String> ret = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (GameBoardSlotType[] gameBoardSlotTypes : board) {
            sb.setLength(0);
            for (int column = 0; column < board.length; column++) {
                sb.append(gameBoardSlotTypes[column].getValue());
            }
            ret.add(sb.toString());
        }
        return ret;
    }


    /**
     * Init board from string lines.
     *
     * @param boardLines lines
     */
    public void initFromString(List<String> boardLines) {
        for (int i = 1; i < board.length - 1; i++) {
            String line = boardLines.get(i);
            for (int j = 1; j < board.length - 1; j++) {
                if (line.length() > j) {
                    setItemOnLocation(i, j, GameBoardSlotType.fromValue(line.charAt(j) + ""));
                }
            }
        }

    }

    @Override
    public String toString() {
        return "GameBoard{" +
                "board="  + String.join("|", getBoardAsString()) +
                '}';
    }
}
