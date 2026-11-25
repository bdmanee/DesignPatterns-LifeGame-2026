package com.holub.life;

/**
 * Turns one cell on or off. Undo doesn't need a snapshot here:
 * toggling the same cell again is the exact inverse.
 */
public class ToggleCellCommand implements UndoableCommand {

    private final LifeBoard board;
    private final int row;
    private final int column;

    public ToggleCellCommand(LifeBoard board, int row, int column) {
        this.board = board;
        this.row = row;
        this.column = column;
    }

    @Override
    public void execute() {
        board.toggle(row, column);
    }

    @Override
    public void undo() {
        board.toggle(row, column);
    }

    @Override
    public String getName() {
        return "Toggle Cell";
    }
}
