import com.holub.life.AgeColorStrategy;
import com.holub.life.CellColorStrategy;
import com.holub.life.CellColors;
import com.holub.life.ClassicColorStrategy;
import com.holub.life.LifeBoard;
import com.holub.life.NeonTheme;
import com.holub.life.PatternFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

public class CellAgeTest {

    @AfterEach
    void resetColors() {
        CellColors.reset();
    }

    @Test
    void newCellHasAgeZero() {
        LifeBoard board = new LifeBoard(2, 8);
        board.setAlive(3, 3, true);

        assertEquals(0, board.getAge(3, 3));
    }

    @Test
    void stableBlockGetsOlderEveryGeneration() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("block"), 5, 5);

        for (int i = 0; i < 10; i++) {
            board.tick();
        }

        assertEquals(10, board.getAge(5, 5));
        assertEquals(10, board.getAge(6, 6));
    }

    @Test
    void blinkerCenterIsOldButEndsAreAlwaysNew() {
        LifeBoard board = new LifeBoard(4, 8);
        // horizontal blinker in row 11, columns 10..12
        board.placePattern(PatternFactory.createPattern("blinker"), 10, 10);

        for (int i = 0; i < 4; i++) {
            board.tick();
        }

        assertEquals(4, board.getAge(11, 11), "center never dies");
        assertEquals(0, board.getAge(11, 10), "end cell was just born again");
    }

    @Test
    void deadCellHasAgeZero() {
        LifeBoard board = new LifeBoard(2, 8);
        board.setAlive(3, 3, true);
        board.tick(); // lonely cell dies

        assertFalse(board.isAlive(3, 3));
        assertEquals(0, board.getAge(3, 3));
    }

    @Test
    void editingACellResetsItsAge() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("block"), 5, 5);
        board.tick();
        board.tick();

        board.setAlive(5, 5, false);
        board.setAlive(5, 5, true);

        assertEquals(0, board.getAge(5, 5));
    }

    @Test
    void classicStrategyIgnoresAge() {
        CellColorStrategy classic = new ClassicColorStrategy();

        assertEquals(classic.colorFor(true, 0), classic.colorFor(true, 100));
        assertEquals(NeonTheme.BACKGROUND, classic.colorFor(false, 0));
    }

    @Test
    void ageStrategyChangesColorWithAge() {
        CellColorStrategy byAge = new AgeColorStrategy(30);

        assertEquals(Color.WHITE, byAge.colorFor(true, 0));
        assertEquals(NeonTheme.PINK, byAge.colorFor(true, 30));
        assertEquals(NeonTheme.PINK, byAge.colorFor(true, 500), "old cells keep last color");
        assertNotEquals(byAge.colorFor(true, 5), byAge.colorFor(true, 20));
        assertEquals(NeonTheme.BACKGROUND, byAge.colorFor(false, 10));
    }

    @Test
    void strategyCanBeSwitched() {
        CellColors.setStrategy(new AgeColorStrategy());
        assertTrue(CellColors.getStrategy() instanceof AgeColorStrategy);

        CellColors.reset();
        assertTrue(CellColors.getStrategy() instanceof ClassicColorStrategy);

        assertThrows(IllegalArgumentException.class, () -> CellColors.setStrategy(null));
    }
}
