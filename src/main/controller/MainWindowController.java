package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.Chart2D;
import main.file.ReadTxtFile;
import main.file.ReadXlsFile;
import main.model.AppData;
import main.model.DividingLine;
import main.model.Point;
import main.model.enums.AlertEnum;
import main.service.SpaceDivideService;
import main.service.SpaceVectorService;
import main.viewHelp.AlertWindow;
import main.viewHelp.ExcelView;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class MainWindowController {
	private String filePath;
	private AppData appData = AppData.getInstance();
	private Point pointToClassify;

	@FXML
	private AnchorPane pane;

	@FXML
	private void handleOpenTxtFile(ActionEvent event) throws Exception {
		ReadTxtFile readFile = new ReadTxtFile();
		try {
			readFile.readFile();
			filePath = readFile.getFilePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		appData.setTitles(readFile.getTitles());
		appData.setData(readFile.getMap());

		updateTableView();
	}

	@FXML
	private void handleOpenXlsFile(ActionEvent event) throws Exception {
		ReadXlsFile readFile = new ReadXlsFile();
		try {
			readFile.readFile();
			filePath = readFile.getFilePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		appData.setTitles(readFile.getTitles());
		appData.setData(readFile.getMap());

		updateTableView();
	}

	@FXML
	private void handleDivide(ActionEvent event) throws Exception {
		doDivideSpace();
	}

	@FXML
	private void handleClassify(ActionEvent event) throws Exception {
		if (appData.getVectors().isEmpty()) {
			doDivideSpace();
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PointClassification.fxml"));
		Parent root = loader.load();
		PointClassificationController c = loader.getController();
		Scene newScene = new Scene(root);
		Stage newStage = new Stage();
		newStage.setScene(newScene);
		c.setSceneController(this);
		newStage.showAndWait();

		BigDecimal group = BigDecimal.ZERO;

		AlertEnum alert = AlertEnum.OUTPUT;
		alert.setText("Specified point is in group: " + group);
		new AlertWindow().show(alert);
	}

	@FXML
	private void handleShow2D(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
		Scene newScene = new Scene(loader.load());
		Stage newStage = new Stage();
		newStage.setScene(newScene);

		Chart2D chart = new Chart2D(newStage);
		try {
			chart.showInWindow(0, 1, 2);
		} catch (NumberFormatException nfex) {
			new AlertWindow().show(AlertEnum.NOT_NUMERIC_VALUE);
		}
	}

	private void updateTableView() {
		ExcelView excelView = new ExcelView();
		SpreadsheetView spreadsheetView = excelView.getView(appData.getDataWithTitles());
		spreadsheetView.setPrefSize(pane.getWidth(), pane.getHeight());
		spreadsheetView.setEditable(false);
		pane.getChildren().add(spreadsheetView);
	}

	private void doDivideSpace() throws IOException {
		SpaceDivideService divideService = new SpaceDivideService();
		List<DividingLine> lines = divideService.divide();
		appData.removeAllPoints(divideService.getAllDeletedPointsList());
		if (appData.getTitles().size() == 3) {
			showChartWithLines(lines);
		}
		SpaceVectorService vectorService = new SpaceVectorService(lines);
		appData.setVectors(vectorService.createVectors());
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
}
