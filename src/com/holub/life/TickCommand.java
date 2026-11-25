package com.holub.life;

/** One generation forward (the Step button). */
public class TickCommand extends SnapshotCommand {

    public TickCommand(LifeBoard board) {
        super(board);
    }

    @Override
    protected void perform() {
        board.tick();
    }

    @Override
    public String getName() {
        return "Step";
    }
}
