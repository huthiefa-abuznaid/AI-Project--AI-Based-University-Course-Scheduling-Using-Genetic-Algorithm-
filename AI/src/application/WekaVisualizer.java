package application;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class WekaVisualizer extends JPanel {

    private final List<Double> fitnessValues = new ArrayList<>();

    public WekaVisualizer(String csvFilePath) {
        loadData(csvFilePath);
    }

    private void loadData(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.toLowerCase().contains("generation") || line.toLowerCase().contains("fitness")) {
                    continue;
                }
                String[] parts = line.split(",");
                double val = Double.parseDouble(parts.length > 1 ? parts[1].trim() : parts[0].trim());
                fitnessValues.add(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fitnessValues.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;

        g2.drawLine(padding, height - padding, width - padding, height - padding);
        g2.drawLine(padding, padding, padding, height - padding);

        g2.drawString("Generation", width / 2, height - 15);
        g2.drawString("Best Fitness", 10, padding - 10);

        double minFitness = fitnessValues.stream().min(Double::compare).orElse(0.0);
        double maxFitness = fitnessValues.stream().max(Double::compare).orElse(1.0);

        if (maxFitness == minFitness) maxFitness += 0.001;

        int numPoints = fitnessValues.size();
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2f));

        for (int i = 0; i < numPoints - 1; i++) {
            int x1 = padding + (int) ((double) i / (numPoints - 1) * (width - 2 * padding));
            int y1 = height - padding - (int) ((fitnessValues.get(i) - minFitness) / (maxFitness - minFitness) * (height - 2 * padding));

            int x2 = padding + (int) ((double) (i + 1) / (numPoints - 1) * (width - 2 * padding));
            int y2 = height - padding - (int) ((fitnessValues.get(i + 1) - minFitness) / (maxFitness - minFitness) * (height - 2 * padding));

            g2.drawLine(x1, y1, x2, y2);
        }
    }

    public static void plotFitnessConvergence(String csvFilePath) {
        JFrame frame = new JFrame("21. Fitness Convergence Graph");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new WekaVisualizer(csvFilePath));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> plotFitnessConvergence("Fitness_History.csv"));
    }
}