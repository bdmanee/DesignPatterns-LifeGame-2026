
package com.holub.life;

import javax.swing.*;
import java.awt.*;

public class GameControlPanel extends JPanel {

    public GameControlPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");

        playButton.addActionListener(e ->
                Clock.instance().startTicking(150));

        pauseButton.addActionListener(e ->
                Clock.instance().stop());

        stepButton.addActionListener(e -> {
            Clock.instance().stop();
            Clock.instance().tick();
        });

        add(playButton);
        add(pauseButton);
        add(stepButton);

        JComboBox<DrawingMode> drawingModeBox =
                new JComboBox<>(DrawingMode.values());

        drawingModeBox.addActionListener(e -> {
            DrawingMode selected =
                    (DrawingMode) drawingModeBox.getSelectedItem();

            if (selected != null) {
                Universe.instance().setDrawingMode(selected);
            }
        });

        add(new JLabel("Drawing Mode:"));
        add(drawingModeBox);

    }
}
