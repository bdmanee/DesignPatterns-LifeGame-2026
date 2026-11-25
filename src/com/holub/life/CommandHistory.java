package com.holub.life;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Undo and redo stacks for UndoableCommands. */
public class CommandHistory {

    public interface Listener {
        void historyChanged(CommandHistory history);
    }

    public static final int DEFAULT_LIMIT = 100;

    private final int limit;
    private final Deque<UndoableCommand> undoStack = new ArrayDeque<>();
    private final Deque<UndoableCommand> redoStack = new ArrayDeque<>();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    public CommandHistory() {
        this(DEFAULT_LIMIT);
    }

    public CommandHistory(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        this.limit = limit;
    }

    /** Run the command and remember it. */
    public void execute(UndoableCommand command) {
        command.execute();
        record(command);
    }

    /** Remember a command that has already been done. */
    public void record(UndoableCommand command) {
        undoStack.push(command);
        redoStack.clear();

        // forget the oldest command when the history is full
        while (undoStack.size() > limit) {
            undoStack.removeLast();
        }

        fireChanged();
    }

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        UndoableCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        fireChanged();
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }

        UndoableCommand command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        fireChanged();
        return true;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public String getUndoName() {
        return undoStack.isEmpty() ? null : undoStack.peek().getName();
    }

    public String getRedoName() {
        return redoStack.isEmpty() ? null : redoStack.peek().getName();
    }

    public int size() {
        return undoStack.size();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
        fireChanged();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private void fireChanged() {
        for (Listener listener : listeners) {
            listener.historyChanged(this);
        }
    }
}
