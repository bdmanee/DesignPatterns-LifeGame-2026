package com.holub.life;

import javax.swing.*;
import java.awt.*;

public class GameStatusPanel extends JPanel implements GameStateListener {

    private final JLabel generationLabel;
    private final JLabel aliveCellsLabel;

    public GameStatusPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 8));

        generationLabel = new JLabel("Generation: 0");
        aliveCellsLabel = new JLabel("Alive Cells: 0");

        Font font = new Font("Arial", Font.BOLD, 14);
        generationLabel.setFont(font);
        aliveCellsLabel.setFont(font);

        add(generationLabel);
        add(aliveCellsLabel);
    }

    @Override
    public void onGameStateChanged(int generation, int aliveCells) {
        generationLabel.setText("Generation: " + generation);
        aliveCellsLabel.setText("Alive Cells: " + aliveCells);
    }
}