import com.holub.life.BoardSnapshot;
import com.holub.life.CommandHistory;
import com.holub.life.GameFacade;
import com.holub.life.LifeBoard;
import com.holub.life.PatternFactory;
import com.holub.life.PlacePatternCommand;
import com.holub.life.ToggleCellCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UndoRedoTest {

    private LifeBoard board;
    private CommandHistory history;
    private FakeClock clock;
    private GameFacade game;

    @BeforeEach
    void setUp() {
        board = new LifeBoard(4, 8);
        history = new CommandHistory();
        clock = new FakeClock();
        game = new GameFacade(clock, board, history);
    }

    private Set<Point> cells() {
        return new HashSet<>(board.getAliveCells());
    }

    // ---- memento

    @Test
    void snapshotRestoresCellsGenerationAndAge() {
        board.placePattern(PatternFactory.createPattern("block"), 3, 3);
        board.tick();
        board.tick();
        BoardSnapshot snapshot = board.createSnapshot();
        Set<Point> saved = cells();

        board.clear();
        board.placePattern(PatternFactory.createPattern("glider"), 10, 10);

        board.restore(snapshot);

        assertEquals(saved, cells());
        assertEquals(2, board.getGeneration());
        assertEquals(2, board.getAge(3, 3));
    }

    @Test
    void restoredBoardKeepsEvolvingCorrectly() {
        // after a restore the neighborhoods must be marked active,
        // otherwise the glider would freeze
        board.placePattern(PatternFactory.createPattern("glider"), 5, 5);
        BoardSnapshot start = board.createSnapshot();
        for (int i = 0; i < 8; i++) {
            board.tick();
        }
        Set<Point> expected = cells();

        board.restore(start);
        for (int i = 0; i < 8; i++) {
            board.tick();
        }

        assertEquals(expected, cells());
    }

    // ---- single commands

    @Test
    void toggleCommandUndoesWithoutSnapshot() {
        ToggleCellCommand toggle = new ToggleCellCommand(board, 2, 2);

        toggle.execute();
        assertTrue(board.isAlive(2, 2));

        toggle.undo();
        assertFalse(board.isAlive(2, 2));
    }

    @Test
    void placePatternUndoKeepsCellsThatWereAlreadyThere() {
        board.setAlive(1, 1, true);   // overlaps the block
        PlacePatternCommand place =
                new PlacePatternCommand(board, PatternFactory.createPattern("block"), 1, 1);

        place.execute();
        assertEquals(4, board.getAliveCellCount());

        place.undo();
        assertEquals(1, board.getAliveCellCount());
        assertTrue(board.isAlive(1, 1));
    }

    // ---- history

    @Test
    void undoAndRedoThroughTheFacade() {
        game.placePattern(PatternFactory.createPattern("glider"), 5, 5);
        Set<Point> withGlider = cells();
        game.toggleCell(20, 20);

        assertTrue(game.undo());
        assertEquals(withGlider, cells());

        assertTrue(game.undo());
        assertEquals(0, board.getAliveCellCount());
        assertFalse(game.undo(), "nothing left to undo");

        assertTrue(game.redo());
        assertTrue(game.redo());
        assertTrue(board.isAlive(20, 20));
        assertFalse(game.redo());
    }

    @Test
    void stepCanBeUndone() {
        game.placePattern(PatternFactory.createPattern("blinker"), 5, 5);
        Set<Point> before = cells();

        game.step();
        assertEquals(1, board.getGeneration());
        assertNotEquals(before, cells());

        game.undo();
        assertEquals(0, board.getGeneration());
        assertEquals(before, cells());
    }

    @Test
    void redoOfStepGivesSameResult() {
        game.placePattern(PatternFactory.createPattern("R-pentomino"), 12, 12);
        game.step();
        Set<Point> afterStep = cells();

        game.undo();
        game.redo();

        assertEquals(afterStep, cells());
        assertEquals(1, board.getGeneration());
    }

    @Test
    void clearCanBeUndone() {
        game.placePattern(PatternFactory.createPattern("glider"), 5, 5);
        game.step();
        Set<Point> before = cells();

        game.clear();
        assertEquals(0, board.getAliveCellCount());

        game.undo();
        assertEquals(before, cells());
        assertEquals(1, board.getGeneration());
    }

    @Test
    void wholeRunIsOneUndoStep() {
        game.placePattern(PatternFactory.createPattern("glider"), 5, 5);
        Set<Point> beforeRun = cells();

        game.play(100);
        clock.run(20);
        game.pause();

        assertEquals(20, board.getGeneration());
        assertEquals("Run", history.getUndoName());

        game.undo();
        assertEquals(beforeRun, cells());
        assertEquals(0, board.getGeneration());
    }

    @Test
    void undoWhileRunningPausesFirst() {
        game.placePattern(PatternFactory.createPattern("glider"), 5, 5);
        game.play(100);
        clock.run(5);

        game.undo();   // undoes the run

        assertFalse(clock.isTicking());
        assertFalse(game.isRunning());
        assertEquals(0, board.getGeneration());
    }

    @Test
    void drawingStrokeIsOneUndoStep() {
        game.beginStroke();
        for (int column = 0; column < 10; column++) {
            board.setAlive(3, column, true);
        }
        game.endStroke("Draw");

        assertEquals(1, history.size());
        assertEquals("Draw", history.getUndoName());

        game.undo();
        assertEquals(0, board.getAliveCellCount());
    }

    @Test
    void emptyStrokeIsNotRecorded() {
        game.beginStroke();
        game.endStroke("Draw");

        assertFalse(history.canUndo());
    }

    @Test
    void newCommandClearsRedo() {
        game.toggleCell(1, 1);
        game.undo();
        assertTrue(history.canRedo());

        game.toggleCell(2, 2);
        assertFalse(history.canRedo());
    }

    @Test
    void historyForgetsOldestWhenFull() {
        CommandHistory small = new CommandHistory(3);
        for (int i = 0; i < 5; i++) {
            small.execute(new ToggleCellCommand(board, i, i));
        }

        assertEquals(3, small.size());

        while (small.undo()) {
            // undo everything we can
        }
        assertTrue(board.isAlive(0, 0), "first two toggles can't be undone anymore");
        assertTrue(board.isAlive(1, 1));
        assertFalse(board.isAlive(2, 2));
    }

    @Test
    void historyListenerIsNotified() {
        int[] calls = {0};
        history.addListener(h -> calls[0]++);

        game.toggleCell(1, 1);
        game.undo();
        game.redo();

        assertEquals(3, calls[0]);
    }
}
