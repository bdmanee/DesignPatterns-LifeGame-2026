import com.holub.life.BirthSurvivalRule;
import com.holub.life.ConwayRule;
import com.holub.life.HighLifeRule;
import com.holub.life.LifeBoard;
import com.holub.life.LifeRule;
import com.holub.life.PatternFactory;
import com.holub.life.RuleBuilder;
import com.holub.life.RuleCatalog;
import com.holub.life.RuleManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RuleBuilderTest {

    @AfterEach
    void restoreDefaultRule() {
        RuleManager.reset();
    }

    // checks every (alive, neighbors) combination
    private void assertSameRule(LifeRule expected, LifeRule actual) {
        for (int n = 0; n <= 8; n++) {
            assertEquals(expected.nextState(true, n), actual.nextState(true, n),
                    "alive cell, " + n + " neighbors");
            assertEquals(expected.nextState(false, n), actual.nextState(false, n),
                    "dead cell, " + n + " neighbors");
        }
    }

    @Test
    void builtConwayBehavesLikeConwayRule() {
        LifeRule rule = new RuleBuilder()
                .named("Conway")
                .bornWith(3)
                .survivesWith(2, 3)
                .build();

        assertSameRule(new ConwayRule(), rule);
        assertEquals("Conway", rule.getName());
    }

    @Test
    void notationGivesSameRuleAsHighLifeClass() {
        LifeRule rule = RuleBuilder.fromNotation("B36/S23").build();

        assertSameRule(new HighLifeRule(), rule);
    }

    @Test
    void ruleWithoutNameUsesItsNotation() {
        LifeRule rule = RuleBuilder.fromNotation("b36 / s23").build();

        assertEquals("B36/S23", rule.getName());
    }

    @Test
    void partsCanBeInEitherOrder() {
        assertEquals(RuleBuilder.fromNotation("B3/S23").build(),
                     RuleBuilder.fromNotation("S23/B3").build());
    }

    @Test
    void seedsHasNoSurvivors() {
        LifeRule seeds = RuleBuilder.fromNotation("B2/S").build();

        for (int n = 0; n <= 8; n++) {
            assertFalse(seeds.nextState(true, n));
        }
        assertTrue(seeds.nextState(false, 2));
    }

    @Test
    void notationRoundTrip() {
        BirthSurvivalRule rule =
                (BirthSurvivalRule) RuleBuilder.fromNotation("S34678/B3678").build();

        assertEquals("B3678/S34678", rule.getNotation());
    }

    @Test
    void rejectsBadNotation() {
        assertThrows(IllegalArgumentException.class,
                () -> RuleBuilder.fromNotation("B39/S23"));
        assertThrows(IllegalArgumentException.class,
                () -> RuleBuilder.fromNotation("hello"));
        assertThrows(IllegalArgumentException.class,
                () -> RuleBuilder.fromNotation("B3/B3"));
        assertThrows(IllegalArgumentException.class,
                () -> RuleBuilder.fromNotation(null));
    }

    @Test
    void rejectsBadNeighborCounts() {
        assertThrows(IllegalArgumentException.class,
                () -> new RuleBuilder().bornWith(9));
        assertThrows(IllegalArgumentException.class,
                () -> new RuleBuilder().survivesWith(-1));
    }

    @Test
    void rejectsBirthOnZero() {
        assertThrows(IllegalStateException.class,
                () -> RuleBuilder.fromNotation("B03/S23").build());
    }

    @Test
    void builtRuleIsNotChangedByLaterBuilderCalls() {
        RuleBuilder builder = new RuleBuilder().bornWith(3).survivesWith(2, 3);
        LifeRule first = builder.build();

        builder.bornWith(6);

        assertFalse(first.nextState(false, 6));
    }

    @Test
    void catalogHasUniqueNames() {
        long unique = RuleCatalog.presets().stream()
                .map(LifeRule::getName).distinct().count();

        assertEquals(RuleCatalog.presets().size(), unique);
        assertEquals("Conway", RuleCatalog.presets().get(0).getName());
    }

    @Test
    void lifeWithoutDeathOnlyGrows() {
        RuleManager.setRule(
                RuleBuilder.fromNotation("B3/S012345678").build());

        LifeBoard board = new LifeBoard(4, 8);
        board.placePattern(PatternFactory.createPattern("glider"), 10, 10);

        int before = board.getAliveCellCount();
        for (int i = 0; i < 10; i++) {
            board.tick();
            assertTrue(board.getAliveCellCount() >= before);
            before = board.getAliveCellCount();
        }
    }
}
