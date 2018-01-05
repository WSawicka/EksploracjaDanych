package main.viewHelp;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Objects;

public class ScatterXChart<X, Y> extends ScatterChart<X, Y> {

    private ObservableList<Data<X, Y>> horizontalMarkers;
    private ObservableList<Data<X, Y>> verticalMarkers;

    public ScatterXChart(Axis<X> xAxis, Axis<Y> yAxis) {
        super(xAxis, yAxis);
        horizontalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        horizontalMarkers.addListener((InvalidationListener) observable -> layoutPlotChildren());

        verticalMarkers = FXCollections.observableArrayList(d -> new Observable[]{d.YValueProperty()});
        verticalMarkers.addListener((InvalidationListener) observable -> layoutPlotChildren());
    }

    public void addValueMarker(Data<X, Y> marker, boolean isOnX) {
        Objects.requireNonNull(marker, "the marker must not be null");
        if (horizontalMarkers.contains(marker)) return;
        Line line = new Line(Double.parseDouble(marker.getXValue().toString()), Double.parseDouble(marker.getXValue().toString()),
                Double.parseDouble(marker.getYValue().toString()), Double.parseDouble(marker.getYValue().toString()));
        line.setStroke(Color.BLUEVIOLET);
        line.setStrokeWidth(2);
        marker.setNode(line);
        getPlotChildren().add(line);
        if(isOnX) {
            verticalMarkers.add(marker);
        } else {
            horizontalMarkers.add(marker);
        }
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        for (Data<X, Y> horizontalMarker : horizontalMarkers) {
            double lower = ((ValueAxis) getXAxis()).getLowerBound();
            X lowerX = getXAxis().toRealValue(lower);
            double upper = ((ValueAxis) getXAxis()).getUpperBound();
            X upperX = getXAxis().toRealValue(upper);
            
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(getXAxis().getDisplayPosition(lowerX));
            line.setEndX(getXAxis().getDisplayPosition(upperX));
            line.setStartY(getYAxis().getDisplayPosition(horizontalMarker.getYValue()));
            line.setEndY(line.getStartY());

        }

        for (Data<X, Y> horizontalMarker : verticalMarkers) {
            double lower = ((ValueAxis) getYAxis()).getLowerBound();
            Y lowerY = getYAxis().toRealValue(lower);
            double upper = ((ValueAxis) getYAxis()).getUpperBound();
            Y upperY = getYAxis().toRealValue(upper);

            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(getXAxis().getDisplayPosition(horizontalMarker.getXValue()));
            line.setEndX(line.getStartX());
            line.setStartY(getYAxis().getDisplayPosition(lowerY));
            line.setEndY(getYAxis().getDisplayPosition(upperY));
        }
    }
}