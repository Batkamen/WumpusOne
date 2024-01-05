package hu.nye.progtech.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import hu.nye.progtech.bl.GameContext;

/**
 * State definition in the UI state machine.
 */
public class UIState {
    public static final int HEADER_GAP_SIZE = 20;
    private final String name;

    private final Function<GameContext, List<String>> headerGenerator;

    private List<UIAction> actions;

    private final Function<GameContext, List<UIAction>> actionGenerator;

    public UIState(String name, Function<GameContext, List<String>> headerGenerator, UIAction... actions) {
        this.name = name;
        this.headerGenerator = headerGenerator;
        this.actionGenerator = null;
        this.actions = Arrays.stream(actions).toList();
    }

    public UIState(String name, Function<GameContext, List<String>> headerGenerator,
                   Function<GameContext, List<UIAction>> actionGenerator) {
        this.name = name;
        this.headerGenerator = headerGenerator;
        this.actionGenerator = actionGenerator;
        this.actions = new ArrayList<>();
    }


    /**
     * Print UI state.
     *
     * @param out - utput
     * @param context - game context
     */
    public void print(Consumer<String> out, GameContext context) {
        if (headerGenerator != null) {
            for (int i = 0; i < HEADER_GAP_SIZE; i++) {
                out.accept("");
            }
            headerGenerator.apply(context).forEach(out);
        }
        out.accept("");

        if (actionGenerator != null) {
            this.actions = actionGenerator.apply(context);
        }

        for (int i = 0; i < actions.size(); i++) {
            UIAction action = actions.get(i);
            out.accept("[%s] %s".formatted((action.isEnabled(context) ? i : "X"), action.getText()));
        }
    }

    public String getName() {
        return name;
    }

    public boolean hasAction(int menuOption, GameContext context) {
        return menuOption >= 0 && menuOption < this.actions.size()
                && this.actions.get(menuOption).isEnabled(context);
    }

    public String handleAction(int menuOption, GameContext context) {
        UIAction action =  this.actions.get(menuOption);
        return action.execute(context);
    }
}
