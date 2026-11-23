package com.holub.life;

import java.util.function.IntSupplier;

public class PlayCommand implements GameCommand {

    private final GameFacade game;
    private final IntSupplier speedSupplier;

    public PlayCommand(GameFacade game, IntSupplier speedSupplier) {
        this.game = game;
        this.speedSupplier = speedSupplier;
    }

    @Override
    public void execute() {
        game.play(speedSupplier.getAsInt());
    }
}
