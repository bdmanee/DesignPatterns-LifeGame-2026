package com.holub.life;

import java.io.IOException;
import java.io.InputStream;

/**
 * One simple entry point for the UI. Buttons, menus and the mouse
 * handler call this class instead of talking to the Clock, the
 * LifeBoard and the CommandHistory themselves.
 *
 * Everything that changes the board goes through the CommandHistory,
 * so it can be undone.
 */
public class GameFacade {

    private final GameClock clock;
    private final LifeBoard board;
    private final CommandHistory history;

    private boolean running = false;
    private BoardSnapshot runStart = null;
    private BoardSnapshot strokeStart = null;

    public GameFacade(GameClock clock, LifeBoard board, CommandHistory history) {
        this.clock = clock;
        this.board = board;
        this.history = history;

        clock.addClockListener(board::tick);
    }

    // ---- running -----------------------------------------------------

    public void play(int speed) {
        if (!running) {
            runStart = board.createSnapshot();
            running = true;
        }
        clock.startTicking(speed);
    }

    public void pause() {
        if (running) {
            clock.stop();
            running = false;
            recordRun();
        }
    }

    public void step() {
        pause();
        history.execute(new TickCommand(board));
    }

    public boolean isRunning() {
        return running;
    }

    /** The generations played while running become one "Run" command. */
    private void recordRun() {
        if (runStart != null) {
            RecordedChangeCommand run = new RecordedChangeCommand(board, "Run", runStart);
            if (run.changedAnything()) {
                history.record(run);
            }
            runStart = null;
        }
    }

    // ---- editing -----------------------------------------------------

    private void doEdit(UndoableCommand command) {
        // an edit while running splits the run in two, so undo
        // gives back the board just before the edit
        recordRun();
        history.execute(command);
        if (running) {
            runStart = board.createSnapshot();
        }
    }

    public void toggleCell(int row, int column) {
        doEdit(new ToggleCellCommand(board, row, column));
    }

    public void placePattern(LifePattern pattern, int row, int column) {
        doEdit(new PlacePatternCommand(board, pattern, row, column));
    }

    public void clear() {
        pause();
        history.execute(new ClearBoardCommand(board));
    }

    /** Call when the mouse is pressed for a drawing stroke. */
    public void beginStroke() {
        recordRun();
        strokeStart = board.createSnapshot();
    }

    /** Call when the mouse is released. The whole stroke is one undo step. */
    public void endStroke(String name) {
        if (strokeStart == null) {
            return;
        }

        RecordedChangeCommand stroke = new RecordedChangeCommand(board, name, strokeStart);
        if (stroke.changedAnything()) {
            history.record(stroke);
        }
        strokeStart = null;

        if (running) {
            runStart = board.createSnapshot();
        }
    }

    public void load(InputStream in) throws IOException {
        pause();
        BoardSnapshot before = board.createSnapshot();
        board.load(in);
        history.record(new RecordedChangeCommand(board, "Load", before));
    }

    // ---- undo / redo -------------------------------------------------

    public boolean undo() {
        pause();
        return history.undo();
    }

    public boolean redo() {
        pause();
        return history.redo();
    }

    public CommandHistory getHistory() {
        return history;
    }

    public LifeBoard getBoard() {
        return board;
    }

    // ---- other settings ----------------------------------------------

    public void setDrawingMode(DrawingMode mode) {
        Universe.instance().setDrawingMode(mode);
    }

    public void setPatternSpacing(int spacing) {
        Universe.instance().setPatternSpacing(spacing);
    }

    public int getAliveCellCount() {
        return board.getAliveCellCount();
    }
}
