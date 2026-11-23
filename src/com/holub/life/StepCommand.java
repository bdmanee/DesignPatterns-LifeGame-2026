package com.holub.life;

public class StepCommand implements GameCommand {

    private final GameFacade game;

    public StepCommand(GameFacade game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.step();
    }
}
