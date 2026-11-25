package com.holub.life;

/**
 * What GameFacade needs from a clock. Clock implements it; tests use
 * a fake clock so they don't need a Swing timer or the menu bar.
 */
public interface GameClock {

    void startTicking(int millisecondsBetweenTicks);

    void stop();

    void tick();

    void addClockListener(Clock.Listener listener);
}
