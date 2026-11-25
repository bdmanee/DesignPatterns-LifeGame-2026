import com.holub.life.CommandHistory;
import com.holub.life.GameFacade;
import com.holub.life.GameState;
import com.holub.life.LifeBoard;
import com.holub.life.PatternFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private LifeBoard board;
    private FakeClock clock;
    private CommandHistory history;
    private GameFacade game;
    private List<String> changes;

    @BeforeEach
    void setUp() {
        board = new LifeBoard(4, 8);
        clock = new FakeClock();
        history = new CommandHistory();
        game = new GameFacade(clock, board, history);
        changes = new ArrayList<>();
        game.addRunStateListener(state -> changes.add(state.getName()));

        game.placePattern(PatternFactory.createPattern("glider"), 5, 5);
    }

    @Test
    void startsPaused() {
        assertSame(GameFacade.PAUSED, game.getState());
        assertFalse(game.isRunning());
    }

    @Test
    void playStartsTheClockAndChangesState() {
        game.play(70);

        assertSame(GameFacade.RUNNING, game.getState());
        assertTrue(clock.isTicking());
        assertEquals(70, clock.getSpeed());
        assertEquals(List.of("Running"), changes);
    }

    @Test
    void playWhileRunningOnlyChangesSpeed() {
        game.play(150);
        clock.run(3);
        game.play(30);
        clock.run(3);
        game.pause();

        assertEquals(List.of("Running", "Paused"), changes, "no extra state changes");
        assertEquals(30, clock.getSpeed());
        // still only one Run step in the history
        game.undo();
        assertEquals(0, board.getGeneration());
    }

    @Test
    void pauseWhilePausedDoesNothing() {
        int before = history.size();

        game.pause();

        assertTrue(changes.isEmpty());
        assertEquals(before, history.size());
    }

    @Test
    void stepWhileRunningPausesThenSteps() {
        game.play(100);
        clock.run(4);

        game.step();

        assertSame(GameFacade.PAUSED, game.getState());
        assertFalse(clock.isTicking());
        assertEquals(5, board.getGeneration());
        assertEquals("Step", history.getUndoName());
    }

    @Test
    void stepWhilePausedAdvancesOneGeneration() {
        game.step();
        game.step();

        assertEquals(2, board.getGeneration());
        assertTrue(changes.isEmpty());
    }

    @Test
    void editWhileRunningSplitsTheRun() {
        game.play(100);
        clock.run(4);
        game.toggleCell(25, 25);
        clock.run(4);
        game.pause();

        // undo stack (newest first): Run, Toggle, Run, Place
        assertEquals("Run", history.getUndoName());
        game.undo();
        assertEquals("Toggle Cell", history.getUndoName());
        assertEquals(4, board.getGeneration());
        game.undo();
        assertEquals("Run", history.getUndoName());
        game.undo();
        assertEquals(0, board.getGeneration());
    }

    @Test
    void stateNamesForTheStatusBar() {
        GameState paused = GameFacade.PAUSED;
        GameState running = GameFacade.RUNNING;

        assertEquals("Paused", paused.getName());
        assertEquals("Running", running.getName());
        assertTrue(running.isRunning());
    }
}
