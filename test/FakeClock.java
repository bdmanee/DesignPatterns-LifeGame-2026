import com.holub.life.Clock;
import com.holub.life.GameClock;

import java.util.ArrayList;
import java.util.List;

/** Clock for tests: no timer, ticks only when the test calls tick(). */
public class FakeClock implements GameClock {

    private final List<Clock.Listener> listeners = new ArrayList<>();
    private boolean ticking = false;
    private int speed = 0;

    @Override
    public void startTicking(int millisecondsBetweenTicks) {
        ticking = millisecondsBetweenTicks > 0;
        speed = millisecondsBetweenTicks;
    }

    @Override
    public void stop() {
        ticking = false;
    }

    @Override
    public void tick() {
        for (Clock.Listener listener : listeners) {
            listener.tick();
        }
    }

    /** Simulate the timer firing n times. */
    public void run(int ticks) {
        for (int i = 0; i < ticks; i++) {
            tick();
        }
    }

    @Override
    public void addClockListener(Clock.Listener listener) {
        listeners.add(listener);
    }

    public boolean isTicking() {
        return ticking;
    }

    public int getSpeed() {
        return speed;
    }
}
