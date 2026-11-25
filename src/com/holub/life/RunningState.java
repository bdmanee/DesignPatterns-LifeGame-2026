package com.holub.life;

public class RunningState implements GameState {

    @Override
    public void play(GameFacade game, int speed) {
        // already running, just use the new speed
        game.startClock(speed);
    }

    @Override
    public void pause(GameFacade game) {
        game.stopClock();
        game.finishRunRecording();
        game.setState(GameFacade.PAUSED);
    }

    @Override
    public void step(GameFacade game) {
        pause(game);
        game.getState().step(game);
    }

    @Override
    public void beforeEdit(GameFacade game) {
        // the generations before the edit become their own undo step
        game.finishRunRecording();
    }

    @Override
    public void afterEdit(GameFacade game) {
        game.startRunRecording();
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public String getName() {
        return "Running";
    }
}
