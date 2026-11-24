import com.holub.life.GameStateListener;
import com.holub.life.LifeBoard;
import com.holub.life.PatternFactory;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the model without any Swing window.
 */
public class LifeBoardTest {

    private Set<Point> alive(LifeBoard board) {
        return new HashSet<>(board.getAliveCells());
    }

    @Test
    void defaultBoardHasTheOriginalSize() {
        LifeBoard board = new LifeBoard();

        assertEquals(256, board.getSize());
        assertEquals(0, board.getAliveCellCount());
        assertEquals(0, board.getGeneration());
    }

    @Test
    void blinkerOscillatesWithPeriodTwo() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("blinker"), 10, 10);

        Set<Point> start = alive(board);

        board.tick();
        assertNotEquals(start, alive(board));
        assertEquals(3, board.getAliveCellCount());

        board.tick();
        assertEquals(start, alive(board));
        assertEquals(2, board.getGeneration());
    }

    @Test
    void blockIsStable() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("block"), 3, 3);

        Set<Point> start = alive(board);

        for (int i = 0; i < 5; i++) {
            board.tick();
        }

        assertEquals(start, alive(board));
    }

    @Test
    void gliderMovesAcrossNeighborhoodBorders() {
        // Neighborhoods are 8 cells wide, so after 4 * 8 = 32
        // generations the glider has crossed several borders.
        LifeBoard board = new LifeBoard(8, 8);
        board.placePattern(PatternFactory.createPattern("glider"), 2, 2);

        Set<Point> start = alive(board);

        for (int i = 0; i < 32; i++) {
            board.tick();
        }

        // a glider moves one cell down and right every 4 generations
        Set<Point> expected = new HashSet<>();
        for (Point p : start) {
            expected.add(new Point(p.x + 8, p.y + 8));
        }

        assertEquals(5, board.getAliveCellCount());
        assertEquals(expected, alive(board));
    }

    @Test
    void patternIsClippedAtTheEdge() {
        LifeBoard board = new LifeBoard(2, 4);

        board.placePattern(PatternFactory.createPattern("block"), 7, 7);

        assertEquals(1, board.getAliveCellCount());
        assertTrue(board.isAlive(7, 7));
    }

    @Test
    void toggleTurnsCellOnAndOff() {
        LifeBoard board = new LifeBoard(2, 4);

        board.toggle(1, 1);
        assertTrue(board.isAlive(1, 1));

        board.toggle(1, 1);
        assertFalse(board.isAlive(1, 1));
    }

    @Test
    void clearRemovesCellsAndResetsGeneration() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("glider"), 0, 0);
        board.tick();

        board.clear();

        assertEquals(0, board.getAliveCellCount());
        assertEquals(0, board.getGeneration());
    }

    @Test
    void listenersAreNotifiedWithGenerationAndCount() {
        LifeBoard board = new LifeBoard(4, 8);
        List<int[]> events = new ArrayList<>();
        GameStateListener listener =
                (generation, aliveCells) -> events.add(new int[] {generation, aliveCells});

        board.addGameStateListener(listener);
        board.placePattern(PatternFactory.createPattern("blinker"), 5, 5);
        board.tick();

        assertEquals(2, events.size());
        assertArrayEquals(new int[] {0, 3}, events.get(0));
        assertArrayEquals(new int[] {1, 3}, events.get(1));

        board.removeGameStateListener(listener);
        board.tick();
        assertEquals(2, events.size());
    }

    @Test
    void settingSameStateTwiceDoesNotNotify() {
        LifeBoard board = new LifeBoard(2, 4);
        int[] calls = {0};
        board.addGameStateListener((g, a) -> calls[0]++);

        assertTrue(board.setAlive(2, 2, true));
        assertFalse(board.setAlive(2, 2, true));

        assertEquals(1, calls[0]);
    }

    @Test
    void storeAndLoadKeepsTheSameCells() throws Exception {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("glider"), 4, 9);
        Set<Point> saved = alive(board);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        board.store(out);

        LifeBoard other = new LifeBoard(4, 8);
        other.placePattern(PatternFactory.createPattern("block"), 0, 0);
        other.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(saved, alive(other));
    }
}
