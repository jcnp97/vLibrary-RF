package asia.virtualmc.vLibrary.utilities.miscellaneous;

import org.bukkit.plugin.Plugin;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;

public class ChartUtils {

    /**
     * Creates a line graph from a list of double values and saves it as a PNG image to the specified path.
     * The graph displays price over time, with styling based on whether the final value increased or decreased
     * compared to the initial value (green for increase, red for decrease).
     *
     * @param plugin The plugin used to determine the data folder for saving the image.
     * @param values The list of double values to plot on the graph (X-axis: index, Y-axis: value).
     * @param outputPath The relative path (inside the plugin's data folder) to save the graph image.
     * @param width The width of the output image in pixels.
     * @param height The height of the output image in pixels.
     */
    public static void createGraph(Plugin plugin, List<Double> values, String outputPath, int width, int height) {
        XYSeries series = new XYSeries("Data");
        for (int i = 0; i < values.size(); i++) {
            series.add(i, values.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Time", "Price", dataset,
                PlotOrientation.VERTICAL, false, true, false);

        XYPlot plot = chart.getXYPlot();

        chart.setBackgroundPaint(Color.BLACK);
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLabelPaint(Color.WHITE);
        domainAxis.setTickLabelPaint(Color.WHITE);
        domainAxis.setAxisLinePaint(Color.WHITE);
        domainAxis.setTickLabelsVisible(false);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelPaint(Color.WHITE);
        rangeAxis.setTickLabelPaint(Color.WHITE);
        rangeAxis.setAxisLinePaint(Color.WHITE);
        rangeAxis.setTickLabelsVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesStroke(0, new BasicStroke(1.5f));

        double initialValue = series.getY(0).doubleValue();
        double finalValue = series.getY(series.getItemCount() - 1).doubleValue();
        if (finalValue >= initialValue) {
            renderer.setSeriesPaint(0, Color.GREEN);
        } else {
            renderer.setSeriesPaint(0, Color.RED);
        }
        plot.setRenderer(renderer);

        File directory = new File(plugin.getDataFolder(), outputPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, "graph.png");

        BufferedImage chartImage = chart.createBufferedImage(width, height);
        try {
            ImageIO.write(chartImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
