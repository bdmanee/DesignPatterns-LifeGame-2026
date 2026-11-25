package com.holub.life;

/**
 * Base class for commands that are undone by restoring a memento
 * (BoardSnapshot) taken before the command ran. Redo restores the
 * snapshot taken after, so it gives exactly the same result even for
 * commands like "step" that depend on the rule.
 */
public abstract class SnapshotCommand implements UndoableCommand {

    protected final LifeBoard board;
    private BoardSnapshot before;
    private BoardSnapshot after;

    protected SnapshotCommand(LifeBoard board) {
        this.board = board;
    }

    /** The actual change. Subclasses implement this. */
    protected abstract void perform();

    @Override
    public final void execute() {
        before = board.createSnapshot();
        perform();
        after = board.createSnapshot();
    }

    @Override
    public void undo() {
        if (before == null) {
            throw new IllegalStateException("Command was never executed");
        }
        board.restore(before);
    }

    @Override
    public void redo() {
        if (after == null) {
            throw new IllegalStateException("Command was never executed");
        }
        board.restore(after);
    }
}
