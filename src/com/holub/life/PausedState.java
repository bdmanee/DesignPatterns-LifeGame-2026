package com.holub.life;

public class PausedState implements GameState {

    @Override
    public void play(GameFacade game, int speed) {
        game.startRunRecording();
        game.startClock(speed);
        game.setState(GameFacade.RUNNING);
    }

    @Override
    public void pause(GameFacade game) {
        // already paused
    }

    @Override
    public void step(GameFacade game) {
        game.executeStep();
    }

    @Override
    public void beforeEdit(GameFacade game) {
    }

    @Override
    public void afterEdit(GameFacade game) {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public String getName() {
        return "Paused";
    }
}
