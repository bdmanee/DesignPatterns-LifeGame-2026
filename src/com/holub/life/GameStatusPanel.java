package com.holub.life;

import javax.swing.*;
import java.awt.*;

public class GameStatusPanel extends JPanel implements GameStateListener {

    private final JLabel generationLabel;
    private final JLabel aliveCellsLabel;

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

        add(generationLabel);
        add(aliveCellsLabel);
    }

    @Override
    public void onGameStateChanged(int generation, int aliveCells) {

        generationLabel.setText("Generation: " + generation);
        aliveCellsLabel.setText("Alive Cells: " + aliveCells);
    }
}
