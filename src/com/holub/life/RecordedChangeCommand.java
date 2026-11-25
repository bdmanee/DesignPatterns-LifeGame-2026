package com.holub.life;

/**
 * A change that already happened, for example a drawing stroke made
 * of many mouse events or a run of the clock. It is created when the
 * change is finished, from the snapshot taken when it started.
 */
public class RecordedChangeCommand implements UndoableCommand {

    private final LifeBoard board;
    private final String name;
    private final BoardSnapshot before;
    private final BoardSnapshot after;

    public RecordedChangeCommand(LifeBoard board, String name, BoardSnapshot before) {
        this.board = board;
        this.name = name;
        this.before = before;
        this.after = board.createSnapshot();
    }

    /** Re-applies the recorded result. */
    @Override
    public void execute() {
        board.restore(after);
    }

    @Override
    public void undo() {
        board.restore(before);
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean changedAnything() {
        return !before.sameCellsAs(after)
                || before.getGeneration() != after.getGeneration();
    }
}
