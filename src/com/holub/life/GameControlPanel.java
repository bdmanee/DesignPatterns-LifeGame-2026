
package com.holub.life;

import javax.swing.*;
import java.awt.*;

public class GameControlPanel extends JPanel {

    public GameControlPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");

        GameFacade game = new GameFacade();

        GameCommand playCommand = new PlayCommand(game, () -> 150);
        GameCommand pauseCommand = new PauseCommand(game);
        GameCommand stepCommand = new StepCommand(game);

        playButton.addActionListener(e -> playCommand.execute());
        pauseButton.addActionListener(e -> pauseCommand.execute());
        stepButton.addActionListener(e -> stepCommand.execute());

        add(playButton);
        add(pauseButton);
        add(stepButton);

        JComboBox<DrawingMode> drawingModeBox =
                new JComboBox<>(DrawingMode.values());

        drawingModeBox.addActionListener(e -> {
            DrawingMode selected =
                    (DrawingMode) drawingModeBox.getSelectedItem();

            if (selected != null) {
                game.setDrawingMode(selected);
            }

        });

        add(new JLabel("Drawing Mode:"));
        add(drawingModeBox);

    }
}
