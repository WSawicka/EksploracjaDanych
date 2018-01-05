package main.model.enums;

import javafx.scene.control.Alert;
import lombok.Getter;

@Getter
public enum AlertEnum {
    FILE_ERROR("File error.", Alert.AlertType.ERROR),
    READ_SUCCESS("File reading success.", Alert.AlertType.INFORMATION),
    WRITE_SUCCESS("Data is saved.", Alert.AlertType.INFORMATION),
    FILE_NOT_FOUND("File not found!", Alert.AlertType.ERROR),
    INVALID_FILE_FORMAT("Invalid data format!", Alert.AlertType.ERROR),
    EMPTY_FILE("File is empty.", Alert.AlertType.ERROR),
    NOT_NUMERIC_VALUE("Wrong data type - only numbers!", Alert.AlertType.ERROR),
    OUTPUT_KNN("", Alert.AlertType.INFORMATION),
    NOT_MATCHING_COLUMN_SIZE("Data size don't match! different column size.", Alert.AlertType.ERROR);

    private String text;
    private Alert.AlertType type;

    AlertEnum(String text, Alert.AlertType type) {
        this.text = text;
        this.type = type;
    }

    public void setText(String newText) {
        text = newText;
    }
}
