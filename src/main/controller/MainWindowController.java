package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.model.DividingLine;
import main.service.SpaceVectorService;
import main.viewHelp.AlertWindow;
import main.Chart2D;
import main.viewHelp.ExcelView;
import main.file.ReadTxtFile;
import main.file.ReadXlsFile;
import main.model.enums.AlertEnum;
import main.model.AppData;
import main.service.SpaceDivideService;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.io.IOException;
import java.util.List;

public class MainWindowController {
	private String filePath;
	private AppData appData = AppData.getInstance();

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
		SpaceDivideService divideService = new SpaceDivideService();
		List<DividingLine> lines = divideService.divide();
		System.out.println("\nGOT: " + lines.size() + " lines.");
		SpaceVectorService vectorService = new SpaceVectorService(lines);
		vectorService.createVectors();
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
}
