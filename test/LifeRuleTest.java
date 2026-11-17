import com.holub.life.LifeRule;
import com.holub.life.ConwayRule;
import com.holub.life.HighLifeRule;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LifeRuleTest {

    private final LifeRule conway = new ConwayRule();
    private final LifeRule highLife = new HighLifeRule();

    @Test
    void conwayDeadCellWithThreeNeighborsBecomesAlive() {
        assertTrue(conway.nextState(false, 3));
    }

    @Test
    void conwayDeadCellWithSixNeighborsStaysDead() {
        assertFalse(conway.nextState(false, 6));
    }

    @Test
    void highLifeDeadCellWithSixNeighborsBecomesAlive() {
        assertTrue(highLife.nextState(false, 6));
    }

    @Test
    void highLifeDeadCellWithThreeNeighborsBecomesAlive() {
        assertTrue(highLife.nextState(false, 3));
    }

    @Test
    void bothRulesKeepLivingCellAliveWithTwoNeighbors() {
        assertTrue(conway.nextState(true, 2));
        assertTrue(highLife.nextState(true, 2));
    }

    @Test
    void bothRulesKillLivingCellWithFourNeighbors() {
        assertFalse(conway.nextState(true, 4));
        assertFalse(highLife.nextState(true, 4));
    }
}
