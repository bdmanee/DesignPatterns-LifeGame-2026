import com.holub.life.LifePattern;
import com.holub.life.PatternFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PatternFactoryTest {

    @Test
    void createsGlider() {
        LifePattern pattern =
                PatternFactory.createPattern("glider");

        assertEquals("Glider", pattern.getName());

        boolean[][] cells = pattern.getCells();

        assertEquals(3, cells.length);
        assertEquals(3, cells[0].length);

        assertTrue(cells[0][1]);
        assertTrue(cells[1][2]);
        assertTrue(cells[2][0]);
        assertTrue(cells[2][1]);
        assertTrue(cells[2][2]);
        assertFalse(cells[0][0]);
    }

    @Test
    void createsBlinker() {
        LifePattern pattern =
                PatternFactory.createPattern("blinker");

        boolean[][] cells = pattern.getCells();

        assertEquals(3, cells.length);
        assertFalse(cells[0][1]);
        assertTrue(cells[1][0]);
        assertTrue(cells[1][1]);
        assertTrue(cells[1][2]);
        assertFalse(cells[2][1]);
    }

    @Test
    void createsBlock() {
        LifePattern pattern =
                PatternFactory.createPattern("block");

        boolean[][] cells = pattern.getCells();

        assertEquals(2, cells.length);
        assertEquals(2, cells[0].length);

        for (boolean[] row : cells) {
            for (boolean cell : row) {
                assertTrue(cell);
            }
        }
    }

    @Test
    void acceptsUppercasePatternName() {
        LifePattern pattern =
                PatternFactory.createPattern("GLIDER");

        assertEquals("Glider", pattern.getName());
    }

    @Test
    void rejectsUnknownPattern() {
        assertThrows(IllegalArgumentException.class,
                () -> PatternFactory.createPattern("unknown"));
    }

    @Test
    void rejectsNullPatternName() {
        assertThrows(IllegalArgumentException.class,
                () -> PatternFactory.createPattern(null));
    }

    @Test
    void protectsPatternDataFromModification() {
        LifePattern pattern =
                PatternFactory.createPattern("block");

        boolean[][] cells = pattern.getCells();
        cells[0][0] = false;

        assertTrue(pattern.getCells()[0][0],
                "Changing returned array must not change the pattern");
    }
}
