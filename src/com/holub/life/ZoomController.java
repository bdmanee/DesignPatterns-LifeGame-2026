package com.holub.life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;

public class ZoomController {

    private final Universe universe;
    private final JScrollPane scrollPane;

    public ZoomController(Universe universe, JScrollPane scrollPane) {
        this.universe = universe;
        this.scrollPane = scrollPane;

        universe.addMouseWheelListener(this::handleWheel);
    }


    private void handleWheel(MouseWheelEvent event) {

        if (!event.isControlDown()) {
            return;
        }

        event.consume();

        JViewport viewport = scrollPane.getViewport();

        // Mouse position inside the visible viewport
        Point mouse = SwingUtilities.convertPoint(
                universe,
                event.getPoint(),
                viewport
        );

        Point oldPosition = viewport.getViewPosition();

        double oldZoom = universe.getZoomLevel();

        double factor = Math.pow(
                1.1,
                -event.getPreciseWheelRotation()
        );

        universe.setZoomLevel(oldZoom * factor);

        double newZoom = universe.getZoomLevel();

        if (Math.abs(newZoom - oldZoom) < 0.000001) {
            return;
        }

        double scale = newZoom / oldZoom;

        // Preserve the grid point under the cursor
        int targetX = (int) Math.round(
                (oldPosition.x + mouse.x) * scale - mouse.x
        );

        int targetY = (int) Math.round(
                (oldPosition.y + mouse.y) * scale - mouse.y
        );

        SwingUtilities.invokeLater(() -> {

            viewport.doLayout();

            Dimension viewSize = universe.getPreferredSize();
            Dimension extent = viewport.getExtentSize();

            int maxX = Math.max(
                    0,
                    viewSize.width - extent.width
            );

            int maxY = Math.max(
                    0,
                    viewSize.height - extent.height
            );

            viewport.setViewPosition(new Point(
                    Math.max(0, Math.min(targetX, maxX)),
                    Math.max(0, Math.min(targetY, maxY))
            ));
        });
    }
}