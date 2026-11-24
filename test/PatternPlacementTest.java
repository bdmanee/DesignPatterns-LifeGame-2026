import com.holub.life.Neighborhood;
import com.holub.life.Resident;
import com.holub.life.Cell;
import com.holub.life.AliveCellCounter;
import com.holub.life.AliveCellCollector;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatternPlacementTest {

    // same nesting as the real board but smaller (2 x 4 = 8 cells wide)
    private Neighborhood makeBoard() {
        return new Neighborhood(2, new Neighborhood(4, new Resident()));
    }

    @Test
    void placingCellTwiceDoesNotTurnItOff() {
        Neighborhood board = new Neighborhood(3, new Resident());

        assertTrue(board.setAliveAt(0, 0, true));
        assertTrue(board.edge(0, 0).isAlive());

        assertFalse(board.setAliveAt(0, 0, true),
                "second call should report no change");
        assertTrue(board.edge(0, 0).isAlive());
    }

    @Test
    void setAliveAtWorksThroughNestedNeighborhoods() {
        Neighborhood board = makeBoard();

        board.setAliveAt(5, 6, true);

        assertTrue(board.isAliveAt(5, 6));
        assertFalse(board.isAliveAt(6, 5));
    }

    @Test
    void positionsOutsideTheBoardAreIgnored() {
        Neighborhood board = makeBoard();

        assertFalse(board.setAliveAt(-1, 0, true));
        assertFalse(board.setAliveAt(0, 8, true));
        assertFalse(board.isAliveAt(100, 100));
    }

    @Test
    void counterVisitorCountsLivingCells() {
        Neighborhood board = makeBoard();
        board.setAliveAt(0, 0, true);
        board.setAliveAt(3, 4, true);
        board.setAliveAt(7, 7, true);

        AliveCellCounter counter = new AliveCellCounter();
        board.accept(counter, 0, 0);

        assertEquals(3, counter.getCount());
    }

    @Test
    void collectorVisitorReportsAbsolutePositions() {
        Neighborhood board = makeBoard();
        board.setAliveAt(5, 6, true);

        AliveCellCollector collector = new AliveCellCollector();
        board.accept(collector, 0, 0);

        List<Point> cells = collector.getAliveCells();
        assertEquals(1, cells.size());
        assertEquals(new Point(6, 5), cells.get(0));
    }

    @Test
    void dummyCellIgnoresEverything() {
        assertFalse(Cell.DUMMY.setAliveAt(0, 0, true));
        assertFalse(Cell.DUMMY.isAliveAt(0, 0));
    }
}
