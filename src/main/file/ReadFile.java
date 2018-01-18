package main.file;

import com.google.common.collect.Multimap;

import java.io.IOException;
import java.util.Map;

public interface ReadFile {
	void readFile() throws IOException;

	String getFilePath();

	Map<Integer, String> getTitles();

	Multimap<Integer, Object> getMap();
}
