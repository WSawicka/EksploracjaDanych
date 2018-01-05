package main;

import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import main.model.AppData;
import main.model.DividingLine;
import main.model.enums.AlertEnum;
import main.model.enums.DimensionEnum;
import main.viewHelp.AlertWindow;
import main.viewHelp.ScatterXChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Chart2D {
	private Stage stage;
	private AppData excelData = AppData.getInstance();
	private List<DividingLine> lines = new ArrayList<>();

	public Chart2D(Stage stage) {
		this.stage = stage;
	}

	public void showInWindow(Integer column1, Integer column2, int classDiffColumnIndex) {
		stage.setTitle("2D chart");

		String titleX = excelData.getTitles().get(column1);
		String titleY = excelData.getTitles().get(column2);

		List<BigDecimal> dataX = excelData.getData().get(column1).stream().map(d -> new BigDecimal(d.toString())).collect(Collectors.toList());
		List<BigDecimal> dataY = excelData.getData().get(column2).stream().map(d -> new BigDecimal(d.toString())).collect(Collectors.toList());
		List<Object> dataClassType = new ArrayList<>(excelData.getData().get(classDiffColumnIndex));
		Set<String> classTypes = excelData.getData().get(classDiffColumnIndex).stream().map(Object::toString).collect(Collectors.toSet());

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(titleX);
		yAxis.setLabel(titleY);

		final ScatterXChart<Number, Number> scatterChart = new ScatterXChart<>(xAxis, yAxis);

		if (!lines.isEmpty()) {
			lines.forEach(line -> scatterChart.addValueMarker(new XYChart.Data<>(line.getValue(), line.getValue()), line.getCoordinate().equals(DimensionEnum.X)));
		}

		scatterChart.setTitle("Chart: " + titleX + " of " + titleY);
		Scene scene = new Scene(scatterChart, 800, 600);

		for (String classType : classTypes) {
			XYChart.Series series = new XYChart.Series();
			series.setName(classType);

			if (dataX.size() != dataY.size()) {
				new AlertWindow().show(AlertEnum.NOT_MATCHING_COLUMN_SIZE);
			}

			for (int i = 0; i < dataX.size(); i++) {
				if (dataClassType.get(i).toString().equals(classType)) {
					series.getData().add(new XYChart.Data(dataX.get(i).doubleValue(), dataY.get(i).doubleValue()));
				}
			}
			scatterChart.getData().add(series);
		}

		scene.getStylesheets().add(getClass().getResource("/styles/chart.css").toExternalForm());

		stage.setScene(scene);
		stage.show();
	}

	public List<DividingLine> getLines() {
		return lines;
	}

	public void setLines(List<DividingLine> lines) {
		this.lines = lines;
	}
}
