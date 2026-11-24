package com.holub.life;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZoomModel {

    public interface ZoomListener {
        void zoomChanged(double zoomLevel);
    }

    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 3.0;
    private static final double ZOOM_FACTOR = 1.25;

    private double zoomLevel = 1.0;
    private final List<ZoomListener> listeners = new CopyOnWriteArrayList<>();

    public double getZoomLevel() {
        return zoomLevel;
    }

    public int getZoomPercent() {
        return (int) Math.round(zoomLevel * 100);
    }

    public void setZoomLevel(double zoom) {
        if (!Double.isFinite(zoom)) {
            throw new IllegalArgumentException(
                    "Zoom must be a finite number"
            );
        }

        double newZoom = Math.max(
                MIN_ZOOM,
                Math.min(MAX_ZOOM, zoom)
        );

        if (newZoom == zoomLevel) {
            return;
        }

        zoomLevel = newZoom;

        for (ZoomListener listener : listeners) {
            listener.zoomChanged(zoomLevel);
        }
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

    public void addZoomListener(ZoomListener listener) {
        listeners.add(listener);
    }
}
