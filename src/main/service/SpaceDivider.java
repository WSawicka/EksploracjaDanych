package main.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Chart2D;
import main.model.AppData;
import main.model.DividingDataSet;
import main.model.DividingLine;
import main.model.Point;
import main.model.enums.AlertEnum;
import main.model.enums.DimensionEnum;
import main.viewHelp.AlertWindow;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class SpaceDivider {
	private int coordinatesAmount;
	private List<Point> points = AppData.getInstance().getDataAsPoints();
	private Map<Integer, List<Point>> sortedPoints = new HashMap<>();

	public SpaceDivider() {
		coordinatesAmount = points.get(0).getVector().size();

		for (int coordinate = 0; coordinate < coordinatesAmount; coordinate++) {
			int finalCoordinate = coordinate;
			List<Point> sorted = new ArrayList<>(points);
			sorted.sort(Comparator.comparing(point -> point.getVector().get(finalCoordinate)));
			sortedPoints.put(coordinate, sorted);
		}
	}

	public List<DividingLine> divide() throws IOException {
		List<DividingLine> lines = new ArrayList<>();

		while (true) {
			DividingDataSet dataSet = getDataSet();

			if (dataSet.getMaxPointsAmount() + 1 == sortedPoints.get(dataSet.getMaxPointsCoord()).size() || dataSet.getMaxPointsAmount() == 0) {
				break;
			}

			lines.add(getLineFrom(dataSet));

			int pointsAmount = sortedPoints.get(dataSet.getMaxPointsCoord()).size();
			if (dataSet.isAsc()) {
				sortedPoints.get(dataSet.getMaxPointsCoord()).subList(0, dataSet.getFirstOutPointIndex()).clear();
			} else {
				sortedPoints.get(dataSet.getMaxPointsCoord()).subList(dataSet.getLastInPointIndex(), pointsAmount).clear();
			}

			sortPointsApartFrom(sortedPoints.get(dataSet.getMaxPointsCoord()), dataSet.getMaxPointsCoord());
		}

		if (coordinatesAmount == 2) {
			showChartWithLines(lines);
		}
		return lines;
	}

	private DividingDataSet getDataSet() {
		DividingDataSet dataSet = new DividingDataSet();

		//TODO: zrobić dodatkowe sprawdzanie wartości!!! co, jeśli na tej samej linii są punkty z dwóch grup? - PUNKTY NIE MOGĄ BYĆ NA LINII

		for (Map.Entry<Integer, List<Point>> sorted : sortedPoints.entrySet()) {
			DividingDataSet newDataSetAsc = getDataFor(sorted, true);
			DividingDataSet newDataSetDesc = getDataFor(sorted, false);

			if (dataSet.getMaxPointsAmount() < newDataSetAsc.getMaxPointsAmount()) {
				dataSet = newDataSetAsc;
			}
			if (dataSet.getMaxPointsAmount() < newDataSetDesc.getMaxPointsAmount()) {
				dataSet = newDataSetDesc;
			}
		}
		return dataSet;
	}

	private DividingDataSet getDataFor(Map.Entry<Integer, List<Point>> sorted, boolean isAsc) {
		DividingDataSet dataSet = new DividingDataSet();
		int pointsToDeleteAmount = 0;
		int lastPointIndex = 0;
		int coord = sorted.getKey();

		if (isAsc) {
			for (int pointIndex = 0; pointIndex < sorted.getValue().size() - 1; pointIndex++) {
				Point actual = sorted.getValue().get(pointIndex);
				Point next = sorted.getValue().get(pointIndex + 1);

				if (!actual.getVector().get(coord).equals(next.getVector().get(coord))) {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteAmount++;
						lastPointIndex = pointIndex;
					} else {
						lastPointIndex = pointIndex;
						break;
					}
				} else {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteAmount++;
					} else {
						break;
					}
				}
			}
		} else {
			for (int pointIndex = sorted.getValue().size() - 1; pointIndex > 1; pointIndex--) {
				Point actual = sorted.getValue().get(pointIndex);
				Point next = sorted.getValue().get(pointIndex - 1);

				if (!actual.getVector().get(coord).equals(next.getVector().get(coord))) {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteAmount++;
						lastPointIndex = pointIndex;
					} else {
						lastPointIndex = pointIndex;
						break;
					}
				} else {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteAmount++;
					} else {
						break;
					}
				}
			}
		}

		if (lastPointIndex == 0) {
			pointsToDeleteAmount = 0;
		}
		dataSet.setMaxPointsAmount(pointsToDeleteAmount);
		dataSet.setMaxPointsCoord(coord);
		dataSet.setLastInPointIndex(lastPointIndex);
		if (isAsc) {
			dataSet.setFirstOutPointIndex(lastPointIndex + 1);
			dataSet.setAsc(true);
		} else {
			dataSet.setFirstOutPointIndex(lastPointIndex - 1);
			dataSet.setAsc(false);
		}
		return dataSet;
	}

	private DividingLine getLineFrom(DividingDataSet dataSet) {
		DividingLine divideLine = new DividingLine();
		divideLine.setCoordinate(DimensionEnum.values()[dataSet.getMaxPointsCoord()]);

		BigDecimal afterLine = sortedPoints.get(dataSet.getMaxPointsCoord()).get(dataSet.getFirstOutPointIndex()).getVector().get(dataSet.getMaxPointsCoord());

		BigDecimal beforeLine = (dataSet.getLastInPointIndex() == sortedPoints.get(dataSet.getMaxPointsCoord()).size()) ?
				BigDecimal.ZERO :
				sortedPoints.get(dataSet.getMaxPointsCoord()).get(dataSet.getLastInPointIndex()).getVector().get(dataSet.getMaxPointsCoord());

		BigDecimal lineValue = (afterLine.add(beforeLine)).divide(BigDecimal.valueOf(2.0), BigDecimal.ROUND_HALF_UP);
		divideLine.setValue(lineValue);
		return divideLine;
	}

	private void showChartWithLines(List<DividingLine> lines) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
		Scene newScene = new Scene(loader.load());
		Stage newStage = new Stage();
		newStage.setScene(newScene);

		Chart2D chart = new Chart2D(newStage);
		chart.setLines(lines);
		try {
			chart.showInWindow(0, 1, 2);
		} catch (NumberFormatException nfex) {
			new AlertWindow().show(AlertEnum.NOT_NUMERIC_VALUE);
		}
	}

	private void sortPointsApartFrom(List<Point> pointsUpdated, int coordinateToOmit) {
		for (int coordinate = 0; coordinate < coordinatesAmount; coordinate++) {
			if (coordinate != coordinateToOmit) {
				int finalCoordinate = coordinate;
				List<Point> sorted = new ArrayList<>(pointsUpdated);
				sorted.sort(Comparator.comparing(point -> point.getVector().get(finalCoordinate)));
				sortedPoints.put(coordinate, sorted);
			}
		}
	}
}
