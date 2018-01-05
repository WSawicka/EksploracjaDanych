package main.file;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class ReadTxtFile {
    private String filePath;
    private Map<Integer, String> titles;
    private Multimap<Integer, Object> map;

    public ReadTxtFile() {
        titles = new HashMap<>();
        map = ArrayListMultimap.create();
    }

    public void readFile() throws IOException {
        filePath = getFile();

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> {
                //String replacedTabs = line.replaceAll("\t", " ");
                List<String> splitted = Arrays.stream(line.split("\\s+")).collect(Collectors.toList());
                for (int columnIndex = 0; columnIndex < splitted.size(); columnIndex++) {
                    map.put(columnIndex, splitted.get(columnIndex));
                }
            });
        }
        for (int i = 0; i < map.keySet().size(); i++) {
            titles.put(i, "x" + (i + 1));
        }
    }

    private String getFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Stage stage = new Stage();
        fileChooser.setTitle("Wybierz plik");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.toString();
        } else {
            throw new FileNotFoundException();
        }
    }
}
