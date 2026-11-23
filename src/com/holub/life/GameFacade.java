package com.holub.life;

public class GameFacade {

    public void play(int speed) {
        Clock.instance().startTicking(speed);
    }

    public void pause() {
        Clock.instance().stop();
    }

    public void step() {
        Clock.instance().stop();
        Clock.instance().tick();
    }

    public void setDrawingMode(DrawingMode mode) {
        Universe.instance().setDrawingMode(mode);
    }

    public void setPatternSpacing(int spacing) {
        Universe.instance().setPatternSpacing(spacing);
    }

    public int getAliveCellCount() {
        return Universe.instance().getAliveCellCount();
    }
}
