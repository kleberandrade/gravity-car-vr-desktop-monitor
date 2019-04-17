package br.fatec.gravitycarvrmonitor.charts;

import br.fatec.gravitycarvrmonitor.utils.MovingAverage;
import br.fatec.gravitycarvrmonitor.utils.StatisticsTracker;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RealTimeLineChart {
    
    private static final int MOVING_AVERAGE = 20;

    private final XYSeries mSeries = new XYSeries("Series");
    private final XYSeries mSeriesAvg = new XYSeries("Moving Average");

    private StatisticsTracker mStatisticsTracker = new StatisticsTracker();
    private final MovingAverage mMovingAverage = new MovingAverage(MOVING_AVERAGE);

    public RealTimeLineChart(final String title, final String xLabel, final String yLabel, JPanel panel) {

        XYDataset dataset = createDataset();

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        format(chart, title);

        ChartPanel chartPanel = new ChartPanel(chart);

        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
    }

    private void format(JFreeChart chart, String title) {
        TextTitle my_Chart_title = new TextTitle(title, new Font("Tahoma", Font.BOLD, 12));
        chart.setTitle(my_Chart_title);
        chart.setBackgroundPaint(new Color(236, 240, 241));

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(236, 240, 241));
        plot.setDomainGridlinePaint(new Color(149, 165, 166));
        plot.setRangeGridlinePaint(new Color(149, 165, 166));

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185));
        renderer.setSeriesStroke(0, new BasicStroke(2));
        renderer.setSeriesPaint(1, new Color(231, 76, 60));
        renderer.setSeriesStroke(1, new BasicStroke(2));

        Font fontLabel = new Font("Tahoma", Font.BOLD, 10);

        ValueAxis labelX = plot.getRangeAxis();
        labelX.setLabelFont(fontLabel);
        labelX.setTickLabelFont(fontLabel);

        ValueAxis labelY = plot.getDomainAxis();
        labelY.setLabelFont(fontLabel);
        labelY.setTickLabelFont(fontLabel);
    }

    public void addSeries(double xValue, double yValue, int maxItemCounts) {
        mSeries.add(xValue, yValue);
        mSeriesAvg.add(xValue, getMovingAverage(yValue));
        mStatisticsTracker.addNumber(yValue);

        if (mSeries.getItemCount() > maxItemCounts) {
            mSeries.delete(0, 1);
            mSeriesAvg.delete(0, 1);
        }
    }

    public double getMinimum() {
        return mStatisticsTracker.getMinimum();
    }

    public double getMaximum() {
        return mStatisticsTracker.getMaximum();
    }

    public double getAverage() {
        return mStatisticsTracker.getAverage();
    }

    public void clear() {
        mSeries.clear();
        mStatisticsTracker.clear();
    }

    public double getMovingAverage(double number) {
        mMovingAverage.add(number);
        return mMovingAverage.getAverage();
    }
    
    private XYDataset createDataset() {
    XYSeriesCollection dataset = new XYSeriesCollection();
    
    dataset.addSeries(mSeries);
    dataset.addSeries(mSeriesAvg);
    
    return dataset;
    
    }
}


