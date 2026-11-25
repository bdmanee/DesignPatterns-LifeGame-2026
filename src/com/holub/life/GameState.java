package com.holub.life;

/**
 * State of the game: running or paused. GameFacade forwards the
 * requests to its current state object, so the "what happens if I
 * press Play while already running" logic is in one place per state
 * instead of if (running) checks everywhere.
 */
public interface GameState {

    void play(GameFacade game, int speed);

    void pause(GameFacade game);

    void step(GameFacade game);

    /** Called before the user changes the board. */
    void beforeEdit(GameFacade game);

    /** Called after the user changed the board. */
    void afterEdit(GameFacade game);

    boolean isRunning();

    String getName();
}
