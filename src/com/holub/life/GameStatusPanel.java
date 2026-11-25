package com.holub.life;

import javax.swing.*;
import java.awt.*;

public class GameStatusPanel extends JPanel
        implements GameStateListener, RunStateListener {

    private final JLabel generationLabel;
    private final JLabel aliveCellsLabel;
    private final JLabel stateLabel;

    public GameStatusPanel() {

        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        // Neon dark background
        setBackground(NeonTheme.PANEL);
        setOpaque(true);

        // Purple bottom border
        setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 2, 0, NeonTheme.PURPLE
                )
        );

        generationLabel = new JLabel("Generation: 0");
        aliveCellsLabel = new JLabel("Alive Cells: 0");

        Font font = new Font("SansSerif", Font.BOLD, 14);

        generationLabel.setFont(font);
        aliveCellsLabel.setFont(font);

        // Neon text colors
        generationLabel.setForeground(NeonTheme.CYAN);
        aliveCellsLabel.setForeground(NeonTheme.PINK);

        stateLabel = new JLabel("Paused");
        stateLabel.setFont(font);
        stateLabel.setForeground(NeonTheme.TEXT);

        add(generationLabel);
        add(aliveCellsLabel);
        add(stateLabel);
    }

    @Override
    public void onGameStateChanged(int generation, int aliveCells) {

        generationLabel.setText("Generation: " + generation);
        aliveCellsLabel.setText("Alive Cells: " + aliveCells);
    }

    @Override
    public void runStateChanged(GameState newState) {
        stateLabel.setText(newState.getName());
        stateLabel.setForeground(newState.isRunning() ? NeonTheme.CYAN : NeonTheme.TEXT);
    }
}
