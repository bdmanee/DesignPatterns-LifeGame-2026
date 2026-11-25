package com.holub.life;

public class PlacePatternCommand extends SnapshotCommand {

    private final LifePattern pattern;
    private final int row;
    private final int column;

    public PlacePatternCommand(LifeBoard board, LifePattern pattern, int row, int column) {
        super(board);
        this.pattern = pattern.clone();
        this.row = row;
        this.column = column;
    }

    @Override
    protected void perform() {
        board.placePattern(pattern, row, column);
    }

    @Override
    public String getName() {
        return "Place " + pattern.getName();
    }
}
