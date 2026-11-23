
package com.holub.life;

import java.awt.Point;

public class PatternStampTool {

    private int spacing;
    private Point lastPoint;

    public PatternStampTool(int spacing) {
        if (spacing < 1) {
            throw new IllegalArgumentException("Spacing must be positive");
        }

        this.spacing = spacing;
    }

    public void start() {
        lastPoint = null;
    }

    public void setSpacing(int spacing) {
        if (spacing < 1) {
            throw new IllegalArgumentException("Spacing must be positive");
        }

        this.spacing = spacing;
    }

    public int getSpacing() {
        return spacing;
    }

    public boolean shouldStamp(int row, int column) {
        if (lastPoint == null) {
            lastPoint = new Point(column, row);
            return true;
        }

        double distance = lastPoint.distance(column, row);

        if (distance >= spacing) {
            lastPoint = new Point(column, row);
            return true;
        }

        return false;
    }
}
