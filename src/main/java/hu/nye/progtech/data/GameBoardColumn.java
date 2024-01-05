package hu.nye.progtech.data;

import java.util.Arrays;

/**
 * The charater format of the column A being 0, B - 1,...and so on.
 * Just for simplicity of parsing.
 */
public enum GameBoardColumn {
    A('A', 'a'),
    B('B', 'b'),
    C('C', 'c'),
    D('D', 'd'),
    E('E', 'e'),
    F('F', 'f'),
    G('G', 'g'),
    H('H', 'h'),
    I('I', 'i'),
    J('J', 'j'),
    K('K', 'k'),
    L('L', 'l'),
    M('M', 'm'),
    N('N', 'n'),
    O('O', 'o'),
    P('P', 'p'),
    Q('Q', 'q'),
    R('R', 'r'),
    S('S', 's'),
    T('T', 't'),
    ;
    char label;
    char alt;

    GameBoardColumn(char label, char alt) {
        this.label = label;
        this.alt = alt;
    }

    /**
     * Return Index.
     *
     * @return index/ordinal
     */
    public int index() {
        return this.ordinal();
    }

    char label() {
        return this.label;
    }

    char altLabel() {
        return this.alt;
    }

    /**
     * Find and return a {@link GameBoardColumn} for given label.
     *
     * @param columnLabel of char
     * @return GameBoardColumn or null
     */
    public static GameBoardColumn fromLabel(char columnLabel) {
        return Arrays.stream(values())
                .filter(i -> i.label == columnLabel || i.alt == columnLabel)
                .findAny()
                .orElse(null);
    }

}
