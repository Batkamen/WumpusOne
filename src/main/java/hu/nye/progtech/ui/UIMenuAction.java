package hu.nye.progtech.ui;

import java.util.function.Function;
import java.util.function.Predicate;

import hu.nye.progtech.bl.GameContext;

/**
 * UI Menu action, specific implemengtation.
 */
public class UIMenuAction implements UIAction {
    private final String text;

    private final Function<GameContext, String> actionDelegate;

    private final Predicate<GameContext> enabledDelegate;

    private UIMenuAction(String text, Function<GameContext, String> actionDelegate, Predicate<GameContext> enabledDelegate) {
        this.text = text;
        this.actionDelegate = actionDelegate;
        this.enabledDelegate = enabledDelegate;
    }

    public static UIAction create(String text, Function<GameContext, String> actionDelegate, Predicate<GameContext> enabledDelegate) {
        return new UIMenuAction(text, actionDelegate, enabledDelegate);
    }

    public static UIAction create(String text, Function<GameContext, String> actionDelegate) {
        return new UIMenuAction(text, actionDelegate, null);
    }

    @Override
    public String getText() {
        return text;
    }


    @Override
    public String execute(GameContext context) {
        return this.actionDelegate.apply(context);
    }

    @Override
    public boolean isEnabled(GameContext context) {
        return enabledDelegate == null || enabledDelegate.test(context);
    }
}
