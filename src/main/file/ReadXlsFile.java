package main.file;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import main.model.enums.AlertEnum;
import main.viewHelp.AlertWindow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
@Setter
public class ReadXlsFile implements ReadFile {
	private String filePath;
	private Map<Integer, String> titles;
	private Multimap<Integer, Object> map;

	public ReadXlsFile() {
		titles = new HashMap<>();
		map = ArrayListMultimap.create();
	}

	@Override
	public void readFile() throws IOException {
		filePath = getFile();

		try {
			FileInputStream file = new FileInputStream(filePath);

			Workbook workbook = WorkbookFactory.create(file); //reference to .xlsx file
			Sheet sheet = workbook.getSheetAt(0); //first sheet from the workbook

			Iterator<Row> rowIterator = sheet.iterator();
			setTitles(rowIterator);
			setMap(rowIterator);

			file.close();
		} catch (FileNotFoundException fnfex) {
			new AlertWindow().show(AlertEnum.FILE_NOT_FOUND);
			throw new RuntimeException();
		} catch (InvalidFormatException ifex) {
			new AlertWindow().show(AlertEnum.FILE_ERROR);
		}
	}

	private void setTitles(Iterator<Row> rowIterator) {
		Row row = rowIterator.next();
		Iterator<Cell> cellIterator = row.cellIterator();

		while (cellIterator.hasNext()) {
			Cell cell1 = cellIterator.next();
			String value = cell1.getStringCellValue();
			titles.put(cell1.getColumnIndex(), value);
		}
	}

	private void setMap(Iterator<Row> rowIterator) {
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell1 = cellIterator.next();
				CellType type = cell1.getCellTypeEnum();
				if (type.equals(CellType.STRING)) {
					String value = cell1.getStringCellValue();
					map.put(cell1.getColumnIndex(), value);
				} else if (type.equals(CellType.NUMERIC)) {
					String value = String.valueOf(BigDecimal.valueOf(cell1.getNumericCellValue()).setScale(4, BigDecimal.ROUND_HALF_UP));
					map.put(cell1.getColumnIndex(), value);
				}
			}
		}
	}

	private String getFile() throws FileNotFoundException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		Stage stage = new Stage();
		fileChooser.setTitle("Wybierz plik");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XLS", "*.xls"),
				new FileChooser.ExtensionFilter("XLSX", "*.xlsx")
		);

		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			return file.toString();
		} else {
			throw new FileNotFoundException();
		}
	}
}
