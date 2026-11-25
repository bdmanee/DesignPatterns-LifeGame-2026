import com.holub.life.BoardStatistics;
import com.holub.life.LifeBoard;
import com.holub.life.PatternFactory;
import com.holub.life.StatisticsTracker;
import com.holub.life.StatisticsVisitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsTest {

    @Test
    void visitorOnEmptyBoardGivesZeros() {
        LifeBoard board = new LifeBoard(2, 8);
        StatisticsVisitor visitor = new StatisticsVisitor();
        board.accept(visitor);

        BoardStatistics stats = visitor.getResult(0);
        assertEquals(0, stats.getPopulation());
        assertEquals(0, stats.getBoundingWidth());
        assertEquals(0.0, stats.getAverageAge());
    }

    @Test
    void visitorFindsPopulationAndBoundingBox() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("glider"), 3, 4);
        board.setAlive(20, 30, true);

        StatisticsVisitor visitor = new StatisticsVisitor();
        board.accept(visitor);
        BoardStatistics stats = visitor.getResult(0);

        assertEquals(6, stats.getPopulation());
        assertEquals(30 - 4 + 1, stats.getBoundingWidth());
        assertEquals(20 - 3 + 1, stats.getBoundingHeight());
    }

    @Test
    void visitorComputesAges() {
        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("block"), 2, 2);
        for (int i = 0; i < 6; i++) {
            board.tick();
        }
        board.setAlive(20, 20, true);   // a new cell with age 0

        StatisticsVisitor visitor = new StatisticsVisitor();
        board.accept(visitor);
        BoardStatistics stats = visitor.getResult(board.getGeneration());

        assertEquals(6, stats.getOldestAge());
        assertEquals((4 * 6) / 5.0, stats.getAverageAge(), 0.0001);
    }

    @Test
    void trackerRecordsOneValuePerGeneration() {
        LifeBoard board = new LifeBoard(4, 8);
        StatisticsTracker tracker = new StatisticsTracker(board);

        board.placePattern(PatternFactory.createPattern("R-pentomino"), 12, 12);
        for (int i = 0; i < 5; i++) {
            board.tick();
        }

        List<Integer> history = tracker.getPopulationHistory();
        assertEquals(6, history.size(), "generation 0 to 5");
        assertEquals(5, (int) history.get(0), "R-pentomino has 5 cells");
        assertEquals(board.getAliveCellCount(), (int) history.get(5));
        assertEquals(board.getAliveCellCount(), tracker.getLatest().getPopulation());
    }

    @Test
    void trackerKeepsPeak() {
        LifeBoard board = new LifeBoard(4, 8);
        StatisticsTracker tracker = new StatisticsTracker(board);

        board.setAlive(5, 5, true);
        board.setAlive(5, 6, true);
        board.tick();   // both die

        assertEquals(2, tracker.getPeakPopulation());
        assertEquals(0, tracker.getPeakGeneration());
        assertEquals(0, tracker.getLatest().getPopulation());
    }

    @Test
    void clearStartsANewHistory() {
        LifeBoard board = new LifeBoard(4, 8);
        StatisticsTracker tracker = new StatisticsTracker(board);
        board.placePattern(PatternFactory.createPattern("blinker"), 5, 5);
        board.tick();
        board.tick();

        board.clear();

        assertEquals(List.of(0), tracker.getPopulationHistory());
        assertEquals(0, tracker.getFirstGeneration());
    }

    @Test
    void historyIsLimited() {
        LifeBoard board = new LifeBoard(4, 8);
        StatisticsTracker tracker = new StatisticsTracker(board, 10);
        board.placePattern(PatternFactory.createPattern("blinker"), 5, 5);

        for (int i = 0; i < 25; i++) {
            board.tick();
        }

        assertEquals(10, tracker.getPopulationHistory().size());
        assertEquals(16, tracker.getFirstGeneration());
    }

    @Test
    void listenersAreToldAboutChanges() {
        LifeBoard board = new LifeBoard(4, 8);
        StatisticsTracker tracker = new StatisticsTracker(board);
        int[] calls = {0};

        tracker.addStatisticsListener(t -> calls[0]++);   // called once right away
        board.tick();

        assertEquals(2, calls[0]);

        tracker.detach();
        board.tick();
        assertEquals(2, calls[0]);
    }
}
