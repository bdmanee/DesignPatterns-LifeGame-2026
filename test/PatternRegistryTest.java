import com.holub.life.LifeBoard;
import com.holub.life.LifePattern;
import com.holub.life.PatternRegistry;

import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PatternRegistryTest {

    @Test
    void createReturnsACopyNotThePrototype() {
        PatternRegistry registry = new PatternRegistry();
        registry.register(LifePattern.fromRows("Dot", "O"));

        LifePattern first = registry.create("dot");
        LifePattern second = registry.create("DOT");

        assertNotSame(first, second);
        assertTrue(first.sameShapeAs(second));
    }

    @Test
    void changingTheRegisteredObjectDoesNotChangeThePrototype() {
        boolean[][] cells = {{true, false}};
        PatternRegistry registry = new PatternRegistry();
        registry.register(new LifePattern("Pair", cells));

        cells[0][1] = true;

        assertEquals(1, registry.create("Pair").getAliveCount());
    }

    @Test
    void newPatternsCanBeAddedAtRuntime() {
        PatternRegistry registry = new PatternRegistry();
        List<String> heard = new ArrayList<>();
        registry.addListener(p -> heard.add(p.getName()));

        assertFalse(registry.contains("Diehard"));

        registry.register(LifePattern.fromRows("Diehard",
                "......O.",
                "OO......",
                ".O...OOO"));

        assertTrue(registry.contains("diehard"));
        assertEquals(List.of("Diehard"), heard);
        assertEquals(List.of("Diehard"), registry.getNames());
    }

    @Test
    void unknownPatternThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new PatternRegistry().create("nothing"));
    }

    @Test
    void defaultRegistryHasBuiltInPatterns() {
        List<String> names = PatternRegistry.getDefault().getNames();

        assertTrue(names.containsAll(List.of(
                "Glider", "Blinker", "Block", "LWSS", "Pulsar", "Gosper Glider Gun")));
    }

    @Test
    void rotatingFourTimesGivesTheSameShape() {
        LifePattern glider = PatternRegistry.getDefault().create("glider");

        LifePattern rotated = glider.rotateClockwise();
        assertFalse(rotated.sameShapeAs(glider));

        LifePattern back = rotated.rotateClockwise().rotateClockwise().rotateClockwise();
        assertTrue(back.sameShapeAs(glider));
    }

    @Test
    void rotateSwapsWidthAndHeight() {
        LifePattern toad = PatternRegistry.getDefault().create("toad");
        LifePattern rotated = toad.rotateClockwise();

        assertEquals(toad.getWidth(), rotated.getHeight());
        assertEquals(toad.getHeight(), rotated.getWidth());
        assertEquals(toad.getAliveCount(), rotated.getAliveCount());
    }

    @Test
    void flipTwiceGivesTheSameShape() {
        LifePattern glider = PatternRegistry.getDefault().create("glider");

        assertFalse(glider.flipHorizontal().sameShapeAs(glider));
        assertTrue(glider.flipHorizontal().flipHorizontal().sameShapeAs(glider));
    }

    // ---- check that the built-in shapes are really what they claim to be

    private Set<Point> run(String name, int generations) {
        LifeBoard board = new LifeBoard(8, 8);
        board.placePattern(PatternRegistry.getDefault().create(name), 10, 10);
        for (int i = 0; i < generations; i++) {
            board.tick();
        }
        return new HashSet<>(board.getAliveCells());
    }

    @Test
    void pulsarHasPeriodThree() {
        assertEquals(run("Pulsar", 0), run("Pulsar", 3));
        assertNotEquals(run("Pulsar", 0), run("Pulsar", 1));
    }

    @Test
    void lwssMovesTwoCellsEveryFourGenerations() {
        Set<Point> start = run("LWSS", 0);
        Set<Point> later = run("LWSS", 4);

        assertEquals(start.size(), later.size());

        Set<Point> shifted = new HashSet<>();
        for (Point p : start) {
            shifted.add(new Point(p.x - 2, p.y));
        }
        assertEquals(shifted, later);
    }

    @Test
    void gliderGunMakesAGliderEveryThirtyGenerations() {
        // gun has 36 cells; after 30 generations it is back to the same
        // shape and one 5 cell glider has left it
        assertEquals(36, run("Gosper Glider Gun", 0).size());
        assertEquals(41, run("Gosper Glider Gun", 30).size());
    }
}
