package hu.nye.progtech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import hu.nye.progtech.bl.FileHelper;
import hu.nye.progtech.bl.GameBL;
import hu.nye.progtech.bl.GameContext;
import hu.nye.progtech.data.GameBoardSlotType;
import hu.nye.progtech.db.GameDAO;
import hu.nye.progtech.ui.UIAction;
import hu.nye.progtech.ui.UIMenuAction;
import hu.nye.progtech.ui.UIState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String ST_EXIT = "EXIT";
    private static final String ST_START = "START";
    private static final String ST_NEW_GAME = "NEW_GAME";
    private static final String ST_PLAYER_NAME = "PLAYER_NAME";
    public static final String ST_GAME = "GAME";

    private static final List<String> HERO_TABLE = FileHelper.readLines("headers/hero.data.txt");

    private static final List<UIState> states = new ArrayList<>();

    private static final GameBL bl = new GameBL();
    public static final String ST_LOAD_GAME = "LOAD_GAME";
    public static final String ST_DELETE_SAVED = "DELETE_SAVED";

    /**
     * main.
     *
     * @param args - args
     */
    public static void main(String[] args) {

        Consumer<String> out = System.out::println;
        Scanner sc = new Scanner(System.in);

        buildUIStateMachine(out, sc);

        bl.setContext(new GameContext());
        UIState state = getState(ST_START);

        do {
            state.print(out, bl.getContext());
            out.accept("");
            out.accept("Menü választás:");
            int menuOption = -1;
            while (!state.hasAction(menuOption, bl.getContext())) {
                state.print(out, bl.getContext());
                out.accept("");
                out.accept("Menü választás:");
                try {
                    menuOption = Integer.parseInt(sc.next());
                } catch (Exception e) {
                    //something went wrong
                    menuOption = -1;
                }
            }

            String newStateName = state.handleAction(menuOption, bl.getContext());
            if (newStateName != null) {
                UIState newState = getState(newStateName);
                if (newState != null) {
                    logger.info("State change {} -> {}", state.getName(), newState.getName());
                    state = newState;
                }
            }
        } while (!state.getName().equals(ST_EXIT));
        //let EXIT state print its header
        state.print(out, bl.getContext());
    }


    private static void buildUIStateMachine(Consumer<String> out, Scanner scanner) {

        final UIAction exit = UIMenuAction.create("Kilépés.", context -> ST_EXIT);
        //start
        states.add(new UIState(ST_START, c -> GameBL.GAME_HEADER,
                UIMenuAction.create("Uj játék inditása.", context -> ST_NEW_GAME),
                UIMenuAction.create("Mentett játék betöltése.", context -> ST_LOAD_GAME),
                exit
        ));


        Function<GameContext, List<UIAction>> actionGenerator = (context) -> {
            List<UIAction> ret = new ArrayList<>();

            List<GameContext> savedGames = GameDAO.listSavedGames();
            for (GameContext ctx : savedGames) {
                ret.add(UIMenuAction.create(String.join(" ", ctx.getGameId(), ctx.getPlayerName(), ctx.getMoves() + ""),
                        c -> {
                    bl.setContext(ctx);
                    bl.start();
                    return ST_GAME;
                }));
            }
            ret.add(UIMenuAction.create("------------------", c -> null, c -> false)); //separator
            ret.add(UIMenuAction.create("Mentett játékok törlése", c -> ST_DELETE_SAVED));
            ret.add(UIMenuAction.create("Vissza", c -> ST_START));
            return ret;
        };

        //load game state
        states.add(new UIState(ST_LOAD_GAME, c -> FileHelper.readLines("headers/load.game.txt"), actionGenerator));

        //delete saved games
        Function<GameContext, List<UIAction>> deleteActionGenerator = (context) -> {
            List<UIAction> ret = new ArrayList<>();

            List<GameContext> savedGames = GameDAO.listSavedGames();
            for (GameContext ctx : savedGames) {
                ret.add(UIMenuAction.create(String.join(" ", ctx.getGameId(), ctx.getPlayerName(), ctx.getMoves() + ""),
                        c -> {
                    GameDAO.deleteGame(ctx);
                    return null;
                }));
            }
            ret.add(UIMenuAction.create("------------------", c -> null, c -> false)); //separator
            ret.add(UIMenuAction.create("Vissza", c -> ST_LOAD_GAME));
            return ret;
        };
        states.add(new UIState(ST_DELETE_SAVED, c -> FileHelper.readLines("headers/delete.game.txt"), deleteActionGenerator));




        //new game state
        states.add(new UIState(ST_NEW_GAME, c -> FileHelper.readLines("headers/new.game.txt"),
                UIMenuAction.create("Játékos nevének megadása.", c -> ST_PLAYER_NAME),
                exit));

        AtomicReference<String> playerName = new AtomicReference<>("");

        //add player name
        states.add(new UIState(ST_PLAYER_NAME, c -> {
            List<String> lines = new ArrayList<>(FileHelper.readLines("headers/player.name.txt"));
            lines.add("");
            lines.add("Megadott név: " + playerName.get());
            return lines;
        },
                UIMenuAction.create("Név bevitele", c -> {
                    out.accept("");
                    out.accept("Adja meg a kivánt nevet:");
                    playerName.set(scanner.next());
                    return null;
                }),
                UIMenuAction.create("Játék inditása", c -> {
                    if (playerName.get().isBlank()) {
                        out.accept("A megadott név nem megfelelő.");
                        return null;
                    }
                    startNewGame(playerName.get());
                    return ST_GAME;
                }),
                UIMenuAction.create("Elvet", c -> ST_NEW_GAME)
                        ));

        //GAME state
        states.add(new UIState(ST_GAME, c -> produceGameHeader(c),
                UIMenuAction.create("Lépés előre", c -> {
                    bl.move();
                    return null;
                    }, c -> !c.isStopped()),
                UIMenuAction.create("Fordul balra", c -> {
                    bl.rotateLeft();
                    return null;
                    }, c -> !c.isStopped()),
                UIMenuAction.create("Fordul jobbra", c -> {
                    bl.rotateRight();
                    return null;
                    }, c -> !c.isStopped()),
                UIMenuAction.create("Lövés", c -> {
                    bl.shoot();
                    return null;
                    }, c -> !c.isStopped()),
                UIMenuAction.create("Arany felvétele", c -> {
                    bl.pickUpGold();
                    return null;
                    }, c -> !c.isStopped()),
                UIMenuAction.create("------------------", c -> null, c -> false), //separator
                UIMenuAction.create("Játék mentése", c -> {
                    GameDAO.saveGame(bl.getContext());
                    return null;
                    }, c -> !c.isStopped()),
                UIMenuAction.create("------------------", c -> null, c -> false), //separator
                UIMenuAction.create("Új játék inditása", c -> {
                    startNewGame(playerName.get());
                    return ST_GAME;
                }),
                UIMenuAction.create("Vissza a főmenübe", c -> ST_START),
                exit));


        states.add(new UIState(ST_EXIT, c -> FileHelper.readLines("headers/goodbye.txt")));
    }

    private static List<String> produceGameHeader(GameContext context) {
        List<String> ret  = new ArrayList<>(GameBL.GAME_HEADER);
        ret.add("");
        ret.addAll(drawBoard(context));
        ret.add("");
        ret.addAll(drawHero(context));

        return ret;
    }

    private static List<String> drawHero(GameContext context) {
        List<String> ret = new ArrayList<>();
        Map<String, String> values = new HashMap<>();
        values.put("$X", " "  + context.getHero().getRow());
        values.put("$Y", context.getHero().getColumn() + " ");
        values.put("$D", context.getHero().getDirection().getAsArrowText());
        values.put("$A", context.getHero().getArrows() + " ");
        values.put("$G", context.getHero().hasGoldCollected() ? "I " : "N ");
        values.put("$O", context.isStopped() ? "I " : "N ");
        values.put("$W", context.gameWon() ? "I " : "N ");

        values.put("$0X", "  " + context.getHeroInitialRow());
        values.put("$0Y", context.getHeroInitialColumn() + "  ");
        values.put("$M", context.getMoves() + "");
        values.put("$NAME", context.getPlayerName());

        for (String line : HERO_TABLE) {
            String newLine = line;

            for (Map.Entry<String, String> e : values.entrySet()) {
                newLine = newLine.replace(e.getKey(), e.getValue());
            }
            ret.add(newLine);
        }
        return ret;
    }


    /**
     * Draw board.
     *
     * @param context - context
     *
     * @return list of lines to be drawn
     */
    public static List<String> drawBoard(GameContext context) {

        List<String> lines = new ArrayList<>();
        //column header
        StringBuilder sb = new StringBuilder();
        sb.append('\t').append('\t');
        for (int i = 0; i < context.getGameBoard().getSize(); i++) {
            sb.append(" ").append(i);
        }
        lines.add(sb.toString());
        sb.setLength(0);

        for (int i = 0; i < context.getGameBoard().getSize(); i++) {
            sb.append('\t').append(i).append('\t');
            for (int j = 0; j < context.getGameBoard().getSize(); j++) {
                GameBoardSlotType slot = context.getGameBoard().getItemOnLocation(i, j);
                boolean isHerowLocation = context.getHero().getRow() == i && context.getHero().getColumn() == j;
                boolean wasHerowLocation = context.getHero().getRow() == i && context.getHero().getColumn() == j - 1;
                if (wasHerowLocation) {
                    sb.append("]");
                } else if (isHerowLocation) {
                    sb.append("[");
                } else {
                    sb.append(" ");
                }
                sb.append(slot.getValue());
            }
            lines.add(sb.toString());
            sb.setLength(0);
        }

        return lines;
    }

    private static void startNewGame(String playerName) {
        bl.initFromTextFormat(FileHelper.readLines("wumpus.board.txt"));
        bl.getContext().setPlayerName(playerName);
        bl.start();
    }

    private static UIState getState(String stateName) {
        return states.stream()
                .filter(s -> stateName.equals(s.getName()))
                .findFirst()
                .orElse(null);
    }
}