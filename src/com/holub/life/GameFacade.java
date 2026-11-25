package com.holub.life;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public static final GameState PAUSED = new PausedState();
    public static final GameState RUNNING = new RunningState();

    private GameState state = PAUSED;
    private final List<RunStateListener> stateListeners = new CopyOnWriteArrayList<>();

    private BoardSnapshot runStart = null;
    private BoardSnapshot strokeStart = null;

    public GameFacade(GameClock clock, LifeBoard board, CommandHistory history) {
        this.clock = clock;
        this.board = board;
        this.history = history;

        clock.addClockListener(board::tick);
    }

    // ---- running (forwarded to the current state) -------------------

    public void play(int speed) {
        state.play(this, speed);
    }

    public void pause() {
        state.pause(this);
    }

    public void step() {
        state.step(this);
    }

    public boolean isRunning() {
        return state.isRunning();
    }

    public GameState getState() {
        return state;
    }

    public void addRunStateListener(RunStateListener listener) {
        stateListeners.add(listener);
    }

    // ---- used by the state objects ------------------------------------

    void setState(GameState newState) {
        if (newState == state) {
            return;
        }

        state = newState;
        for (RunStateListener listener : stateListeners) {
            listener.runStateChanged(state);
        }
    }

    void startClock(int speed) {
        clock.startTicking(speed);
    }

    void stopClock() {
        clock.stop();
    }

    void executeStep() {
        history.execute(new TickCommand(board));
    }

    void startRunRecording() {
        runStart = board.createSnapshot();
    }

    /** The generations played while running become one "Run" command. */
    void finishRunRecording() {
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
        state.beforeEdit(this);
        history.execute(command);
        state.afterEdit(this);
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
        state.beforeEdit(this);
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

        state.afterEdit(this);
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
