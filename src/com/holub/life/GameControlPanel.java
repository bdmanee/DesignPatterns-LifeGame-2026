
package com.holub.life;

import javax.swing.*;
import java.awt.*;

public class GameControlPanel extends JPanel {

    public GameControlPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");

        setBackground(NeonTheme.BACKGROUND);

        JButton[] buttons = {playButton, pauseButton, stepButton};

        for (JButton button : buttons) {
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
            button.setForeground(NeonTheme.TEXT);
            button.setBackground(NeonTheme.PANEL);
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(NeonTheme.PURPLE, 2),
                    BorderFactory.createEmptyBorder(8, 18, 8, 18)
            ));
        }

        playButton.setForeground(NeonTheme.CYAN);
        pauseButton.setForeground(NeonTheme.PINK);
        stepButton.setForeground(NeonTheme.TEXT);

        JSlider spacingSlider = new JSlider(1, 10, 4);
        spacingSlider.setBackground(NeonTheme.BACKGROUND);
        spacingSlider.setForeground(NeonTheme.CYAN);
        spacingSlider.setPreferredSize(new Dimension(120, 35));
        spacingSlider.setToolTipText("Distance between pattern stamps");

        GameFacade game = new GameFacade();

        GameCommand playCommand = new PlayCommand(
                game,
                () -> Clock.instance().getSelectedSpeed()
        );

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


        JLabel drawingLabel = new JLabel("Drawing Mode:");
        drawingLabel.setForeground(NeonTheme.CYAN);
        drawingLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        drawingModeBox.setBackground(NeonTheme.PANEL);
        drawingModeBox.setForeground(NeonTheme.TEXT);
        drawingModeBox.setFont(new Font("SansSerif", Font.BOLD, 13));
        drawingModeBox.setFocusable(false);
        drawingModeBox.setBorder(
                BorderFactory.createLineBorder(NeonTheme.PURPLE, 1)
        );

        add(drawingLabel);
        add(drawingModeBox);


        JLabel spacingLabel = new JLabel("Pattern Spacing: 4");
        spacingLabel.setForeground(NeonTheme.CYAN);
        spacingLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        spacingSlider.addChangeListener(e -> {
            int spacing = spacingSlider.getValue();
            spacingLabel.setText("Pattern Spacing: " + spacing);
            game.setPatternSpacing(spacing);
        });

        add(spacingLabel);
        add(spacingSlider);


    }
}
