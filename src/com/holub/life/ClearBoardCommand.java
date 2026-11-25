package com.holub.life;

public class ClearBoardCommand extends SnapshotCommand {

    public ClearBoardCommand(LifeBoard board) {
        super(board);
    }

    @Override
    protected void perform() {
        board.clear();
    }

    @Override
    public String getName() {
        return "Clear";
    }
}
