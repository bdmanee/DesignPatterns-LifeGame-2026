
import com.holub.life.Resident;
import com.holub.life.Cell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LifeGameBaselineTest {

    // Create a dead or alive cell
    private Resident makeCell(boolean alive) {
        Resident cell = new Resident();

        if (alive) {
            cell.userClicked(null, null);
        }

        return cell;
    }

    // Calculate the next state with a given number
    // of living neighbors
    private void nextGeneration(Resident cell, int livingNeighbors) {
        Cell[] neighbors = new Cell[8];

        for (int i = 0; i < 8; i++) {
            neighbors[i] = makeCell(i < livingNeighbors);
        }

        cell.figureNextState(
                neighbors[0], neighbors[1],
                neighbors[2], neighbors[3],
                neighbors[4], neighbors[5],
                neighbors[6], neighbors[7]
        );

        cell.transition();
    }

    @Test
    void newCellStartsDead() {
        Resident cell = new Resident();

        assertFalse(cell.isAlive());
    }

    @Test
    void deadCellWithThreeNeighborsBecomesAlive() {
        Resident cell = makeCell(false);

        nextGeneration(cell, 3);

        assertTrue(cell.isAlive());
    }

    @Test
    void deadCellWithTwoNeighborsStaysDead() {
        Resident cell = makeCell(false);

        nextGeneration(cell, 2);

        assertFalse(cell.isAlive());
    }

    @Test
    void livingCellWithTwoNeighborsSurvives() {
        Resident cell = makeCell(true);

        nextGeneration(cell, 2);

        assertTrue(cell.isAlive());
    }

    @Test
    void livingCellWithThreeNeighborsSurvives() {
        Resident cell = makeCell(true);

        nextGeneration(cell, 3);

        assertTrue(cell.isAlive());
    }

    @Test
    void livingCellWithFourNeighborsDies() {
        Resident cell = makeCell(true);

        nextGeneration(cell, 4);

        assertFalse(cell.isAlive());
    }

    @Test
    void clearMakesCellDead() {
        Resident cell = makeCell(true);

        cell.clear();

        assertFalse(cell.isAlive());
    }


    @Test
    void transitionReturnsTrueWhenCellChanges() {
        Resident cell = makeCell(false);

        Cell[] neighbors = new Cell[8];
        for (int i = 0; i < 8; i++) {
            neighbors[i] = makeCell(i < 3);
        }

        cell.figureNextState(
                neighbors[0], neighbors[1],
                neighbors[2], neighbors[3],
                neighbors[4], neighbors[5],
                neighbors[6], neighbors[7]
        );

        boolean changed = cell.transition();

        assertTrue(changed,
                "transition() should return true when cell changes");
    }

}
