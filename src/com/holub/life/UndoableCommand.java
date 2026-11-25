package com.holub.life;

public interface UndoableCommand extends GameCommand {

    void undo();

    /** Do the command again after an undo. */
    default void redo() {
        execute();
    }

    /** Short name shown in the Undo/Redo tooltip, e.g. "Place Glider". */
    String getName();
}
