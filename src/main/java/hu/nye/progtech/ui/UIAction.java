package hu.nye.progtech.ui;

import hu.nye.progtech.bl.GameContext;

/**
 * Definition of an UI state action.
 */
public interface UIAction {
    /**
     * Get the text to be shown on the action.
     *
     * @return text
     */
    String getText();

    /**
     * Execute action and return name of next state to transition to.
     *
     * @return nextState
     */
    String execute(GameContext context);

    /**
     * Check if action is enabled.
     *
     * @param context - context
     *
     * @return true if the action is enabled
     */
    boolean isEnabled(GameContext context);
}

