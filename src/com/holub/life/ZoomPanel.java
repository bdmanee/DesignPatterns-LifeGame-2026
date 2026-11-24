package com.holub.life;

import javax.swing.*;
import java.awt.*;

/** The "- 100% +" bar above the board. */
public class ZoomPanel extends JPanel {

    public ZoomPanel(ZoomModel zoomModel) {
        super(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        setBackground(NeonTheme.PANEL);

        JButton zoomOutButton = new JButton("-");
        JButton zoomInButton = new JButton("+");
        JLabel zoomLabel = new JLabel(zoomModel.getZoomPercent() + "%");

        for (JButton button : new JButton[] {zoomOutButton, zoomInButton}) {
            button.setBackground(NeonTheme.PANEL);
            button.setForeground(NeonTheme.CYAN);
        }
        zoomLabel.setForeground(NeonTheme.TEXT);

        zoomInButton.addActionListener(e -> zoomModel.zoomIn());
        zoomOutButton.addActionListener(e -> zoomModel.zoomOut());

        // The label listens to the model, so it is also updated
        // when zooming with ctrl + mouse wheel.
        zoomModel.addZoomListener(
                zoom -> zoomLabel.setText(zoomModel.getZoomPercent() + "%")
        );

        add(zoomOutButton);
        add(zoomLabel);
        add(zoomInButton);
    }
}
