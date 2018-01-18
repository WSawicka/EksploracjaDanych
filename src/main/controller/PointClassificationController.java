package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.model.Point;
import main.model.enums.AlertEnum;
import main.viewHelp.AlertWindow;

import java.math.BigDecimal;

public class PointClassificationController {
	private MainWindowController mainController;

	@FXML
	private TextField x;
	@FXML
	private TextField y;

	public void setSceneController(MainWindowController mainController) {
		this.mainController = mainController;
	}

	@FXML
	public void handleSave() {
		Stage stage = (Stage) x.getScene().getWindow();
		try {
			mainController.setPointToClassify(new Point());
			mainController.getPointToClassify().getVector().add(new BigDecimal(x.getText()));
			mainController.getPointToClassify().getVector().add(new BigDecimal(y.getText()));
		} catch (NumberFormatException nfex) {
			new AlertWindow().show(AlertEnum.NOT_NUMERIC_VALUE);
		}
		stage.close();
	}
}
