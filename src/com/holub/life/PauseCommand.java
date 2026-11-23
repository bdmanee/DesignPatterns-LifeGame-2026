package com.holub.life;

public class PauseCommand implements GameCommand {

    private final GameFacade game;

    public PauseCommand(GameFacade game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.pause();
    }
}
