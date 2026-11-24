package com.holub.life;

public class ZoomModel {

    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 3.0;
    private static final double ZOOM_FACTOR = 1.25;

    private double zoomLevel = 1.0;

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoom) {
        if (!Double.isFinite(zoom)) {
            throw new IllegalArgumentException(
                    "Zoom must be a finite number"
            );
        }

        zoomLevel = Math.max(
                MIN_ZOOM,
                Math.min(MAX_ZOOM, zoom)
        );
    }

    public void zoomIn() {
        setZoomLevel(zoomLevel * ZOOM_FACTOR);
    }

    public void zoomOut() {
        setZoomLevel(zoomLevel / ZOOM_FACTOR);
    }

    public void resetZoom() {
        setZoomLevel(1.0);
    }
}
