package main.viewHelp;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.model.enums.AlertEnum;

public class AlertWindow {
    public void show(AlertEnum alertEnumm) {
        Alert alert = new Alert(alertEnumm.getType(), alertEnumm.getText(), ButtonType.OK);
        alert.showAndWait();
    }
}