package com.example.chartlinechart;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class DoubleXYChart<X, Y> extends StackPane
    {

    private final XYChart<X, Y> primaryChart;
    private final XYChart<X, Y> secondaryChart;

    public DoubleXYChart(XYChart<X, Y> primaryChart, XYChart<X, Y> secondaryChart)
    {
        checkCharts(primaryChart, secondaryChart);
        this.primaryChart = primaryChart;
        this.secondaryChart = secondaryChart;
        initCharts(primaryChart, secondaryChart);
        this.getChildren().addAll(
                wrapChart(primaryChart, secondaryChart, true),
                wrapChart(secondaryChart, primaryChart, false)
        );
    }

    private static void checkCharts(Chart... charts)
    {
        for(Chart chart : charts)
        {
            if(chart.getLegendSide() == Side.LEFT || chart.getLegendSide() == Side.RIGHT)
                throw new IllegalArgumentException("Unsupported chart legend side");
            if(chart.getTitleSide() == Side.LEFT || chart.getTitleSide() == Side.RIGHT)
                throw new IllegalArgumentException("Unsupported title side");
        }
    }

    private static boolean disablePickOnBounds(Node n)
    {
        n.setPickOnBounds(false);

        boolean isContainPlotContent = containsPlotContent(n);

        if (! isContainPlotContent && (n instanceof Parent) ) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) {
                if(disablePickOnBounds(c)){
                    isContainPlotContent = true;
                }
            }
        }

        n.setMouseTransparent(!isContainPlotContent);
        return isContainPlotContent;
    }

    private static boolean containsPlotContent(Node node)
    {
        return node.getStyleClass().contains("plot-content");
    }

    /*
        This uses fillers to properly handle chart's min width/aligning.
        We could use StackPane aligning or translation for chart aligning, but there are some problems we could encounter:
            * window minimization/maximization will not always resize the charts correctly
            * the charts will desync when the width is too small
     */
    private static <X, Y> HBox wrapChart(XYChart<X, Y> chart1, XYChart<X, Y> chart2, boolean primary)
    {
        Pane filler = new Pane();
        filler.managedProperty().bind(chart2.visibleProperty());
        filler.visibleProperty().bind(chart2.visibleProperty());
        filler.minWidthProperty().bind(chart2.getYAxis().widthProperty());
        filler.maxWidthProperty().bind(chart2.getYAxis().widthProperty());
        HBox.setHgrow(chart1, Priority.ALWAYS);
        HBox wrapper = primary ? new HBox(chart1, filler) : new HBox(filler, chart1);
        if(!primary) disablePickOnBounds(wrapper);
        return wrapper;
    }

    private static <X, Y> void initCharts(XYChart<X, Y> primaryChart, XYChart<X, Y> secondaryChart)
    {
        secondaryChart.getYAxis().setSide(Side.RIGHT);
        secondaryChart.setHorizontalGridLinesVisible(false);
        secondaryChart.setVerticalGridLinesVisible(false);
        secondaryChart.setHorizontalZeroLineVisible(false);
        secondaryChart.setVerticalZeroLineVisible(false);
        secondaryChart.getXAxis().setOpacity(0);
        secondaryChart.lookupAll(".chart-title").forEach(t->t.setOpacity(0));

        for(Node n : primaryChart.lookupAll(".chart-legend"))
            n.translateXProperty().bind(n.layoutXProperty().divide(2).multiply(-1));
        for(Node n : secondaryChart.lookupAll(".chart-legend"))
            n.translateXProperty().bind(n.layoutXProperty().divide(2));
        for(Node n : primaryChart.lookupAll(".chart-title"))
            n.translateXProperty().bind(secondaryChart.getYAxis().widthProperty().divide(2));
        for(Node n : secondaryChart.lookupAll(".chart-title"))
            n.translateXProperty().bind(primaryChart.getYAxis().widthProperty().multiply(-1).divide(2));
        setDecorationMode(primaryChart, secondaryChart);
    }

    private static <X, Y> void setDecorationMode(XYChart<X, Y> primaryChart, XYChart<X, Y> secondaryChart)
    {
        if(primaryChart.isVisible() && secondaryChart.isVisible())
        {
            for(Node n : primaryChart.lookupAll(".chart-legend"))
                n.translateXProperty().bind(n.layoutXProperty().divide(2).multiply(-1));
            for(Node n : secondaryChart.lookupAll(".chart-legend"))
                n.translateXProperty().bind(n.layoutXProperty().divide(2));
            for(Node n : primaryChart.lookupAll(".chart-title"))
                n.translateXProperty().bind(secondaryChart.getYAxis().widthProperty().divide(2));
            for(Node n : secondaryChart.lookupAll(".chart-title"))
                n.translateXProperty().bind(primaryChart.getYAxis().widthProperty().multiply(-1).divide(2));
        }
        else
        {
            for(Node n : primaryChart.lookupAll(".chart-legend")) {
                n.translateXProperty().unbind();
                n.setTranslateX(0);
            }
            for(Node n : secondaryChart.lookupAll(".chart-legend")) {
                n.translateXProperty().unbind();
                n.setTranslateX(0);
            }
            for(Node n : primaryChart.lookupAll(".chart-title")) {
                n.translateXProperty().unbind();
                n.setTranslateX(0);
            }
            for(Node n : secondaryChart.lookupAll(".chart-title")) {
                n.translateXProperty().unbind();
                n.setTranslateX(0);
            }
        }
    }

    public XYChart<X, Y> getPrimaryChart()
    {
        return primaryChart;
    }

    public XYChart<X, Y> getSecondaryChart()
    {
        return secondaryChart;
    }

    public void setTitle(String title)
    {
        primaryChart.setTitle(title);
        secondaryChart.setTitle(title);
    }

    public void setVisibility(boolean primary, boolean secondary)
    {
        primaryChart.setVisible(primary);
        secondaryChart.setVisible(secondary);
        setDecorationMode(primaryChart, secondaryChart);
    }

}