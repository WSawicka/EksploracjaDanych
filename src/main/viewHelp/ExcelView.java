package main.viewHelp;

import com.google.common.collect.Multimap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import java.util.ArrayList;
import java.util.List;

public class ExcelView {
    private SpreadsheetView theView;


    private void initializeView(Multimap<Integer, Object> data) {
        GridBase grid = excelToGrid(data);
        theView = new SpreadsheetView(grid);
    }

    public SpreadsheetView getView(Multimap<Integer, Object> data) {
        initializeView(data);
        return theView;
    }

    private GridBase excelToGrid(Multimap<Integer, Object> data) {
        GridBase grid = new GridBase(data.get(0).size(), data.keySet().size());

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();

        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                List<Object> columnList = new ArrayList(data.get(column));
                SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, columnList.get(row).toString());
                if (row == 0) {
                    cell.setStyle("-fx-font-weight: bold; -fx-background-color: lightgray");
                }
                list.add(cell);
            }
            rows.add(list);
        }
        grid.setRows(rows);
        return grid;
    }
}
