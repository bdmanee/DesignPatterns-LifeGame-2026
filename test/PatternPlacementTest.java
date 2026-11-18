import com.holub.life.Neighborhood;
import com.holub.life.Resident;
import com.holub.life.Cell;

import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;

public class PatternPlacementTest {

    @Test
    void placingCellTwiceDoesNotTurnItOff() {
        Neighborhood board = new Neighborhood(3, new Resident());
        Rectangle bounds = new Rectangle(0, 0, 300, 300);
        Point position = new Point(50, 50);

        board.setAliveAt(position, bounds);
        Cell firstState = board.edge(0, 0);

        assertTrue(firstState.isAlive());

        board.setAliveAt(position, bounds);

        assertTrue(board.edge(0, 0).isAlive());
    }
}