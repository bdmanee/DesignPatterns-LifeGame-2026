package com.holub.life;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Side panel with numbers and a population graph. */
public class StatisticsPanel extends JPanel implements StatisticsListener {

    private final JLabel generationLabel = makeLabel(NeonTheme.CYAN);
    private final JLabel populationLabel = makeLabel(NeonTheme.PINK);
    private final JLabel peakLabel = makeLabel(NeonTheme.TEXT);
    private final JLabel oldestLabel = makeLabel(NeonTheme.TEXT);
    private final JLabel averageLabel = makeLabel(NeonTheme.TEXT);
    private final JLabel sizeLabel = makeLabel(NeonTheme.TEXT);
    private final PopulationGraph graph = new PopulationGraph();

    public StatisticsPanel(StatisticsTracker tracker) {
        setLayout(new BorderLayout(0, 10));
        setBackground(NeonTheme.PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, NeonTheme.PURPLE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setPreferredSize(new Dimension(240, 400));

        JLabel title = new JLabel("Statistics");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(NeonTheme.CYAN);

        JPanel numbers = new JPanel(new GridLayout(0, 1, 0, 4));
        numbers.setOpaque(false);
        numbers.add(title);
        numbers.add(generationLabel);
        numbers.add(populationLabel);
        numbers.add(peakLabel);
        numbers.add(oldestLabel);
        numbers.add(averageLabel);
        numbers.add(sizeLabel);

        add(numbers, BorderLayout.NORTH);
        add(graph, BorderLayout.CENTER);

        tracker.addStatisticsListener(this);
    }

    @Override
    public void statisticsChanged(StatisticsTracker tracker) {
        BoardStatistics stats = tracker.getLatest();

        generationLabel.setText("Generation: " + stats.getGeneration());
        populationLabel.setText("Population: " + stats.getPopulation());
        peakLabel.setText("Peak: " + tracker.getPeakPopulation()
                + " (gen " + tracker.getPeakGeneration() + ")");
        oldestLabel.setText("Oldest cell: " + stats.getOldestAge());
        averageLabel.setText(String.format("Average age: %.1f", stats.getAverageAge()));
        sizeLabel.setText("Size: " + stats.getBoundingWidth()
                + " x " + stats.getBoundingHeight());

        graph.setHistory(tracker.getPopulationHistory(), tracker.getFirstGeneration());
    }

    private static JLabel makeLabel(Color color) {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(color);
        return label;
    }

    /** Line graph of population over the last generations. */
    private static class PopulationGraph extends JComponent {

        private List<Integer> history = java.util.Collections.emptyList();
        private int firstGeneration = 0;

        PopulationGraph() {
            setPreferredSize(new Dimension(220, 160));
        }

        void setHistory(List<Integer> history, int firstGeneration) {
            this.history = history;
            this.firstGeneration = firstGeneration;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 5;
            int bottom = height - 18;
            int top = 5;

            g.setColor(NeonTheme.BACKGROUND);
            g.fillRect(0, 0, width, height);

            g.setColor(NeonTheme.GRID);
            g.drawLine(left, bottom, width - 5, bottom);

            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.setColor(NeonTheme.TEXT);
            g.drawString("population / generation", left, height - 4);

            if (history.size() >= 2) {
                int max = 1;
                for (int value : history) {
                    max = Math.max(max, value);
                }

                int count = history.size();
                int[] xs = new int[count];
                int[] ys = new int[count];

                for (int i = 0; i < count; i++) {
                    xs[i] = left + (int) ((long) i * (width - 10 - left) / (count - 1));
                    ys[i] = bottom - (int) ((long) history.get(i) * (bottom - top) / max);
                }

                g.setColor(NeonTheme.CYAN);
                g.setStroke(new BasicStroke(2f));
                g.drawPolyline(xs, ys, count);

                g.setColor(NeonTheme.TEXT);
                g.drawString(String.valueOf(max), left + 2, top + 10);
                String last = "gen " + (firstGeneration + count - 1);
                g.drawString(last, width - 5 - g.getFontMetrics().stringWidth(last), top + 10);
            }

            g.dispose();
        }
    }
}
