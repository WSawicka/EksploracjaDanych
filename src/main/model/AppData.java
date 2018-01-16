package main.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.util.*;

public class AppData {
	private static AppData instance;
	private Map<Integer, String> titles;
	private Multimap<Integer, Object> data;
	private List<SpaceVector> vectors;

	private AppData() {
		titles = new HashMap<>();
		data = ArrayListMultimap.create();
		vectors = new ArrayList<>();
	}

	public static AppData getInstance() {
		if (instance == null) {
			instance = new AppData();
		}
		return instance;
	}

	public Map<Integer, String> getTitles() {
		return titles;
	}

	public void setTitles(Map<Integer, String> titles) {
		this.titles = titles;
	}

	public Multimap<Integer, Object> getData() {
		return data;
	}

	public void setData(Multimap<Integer, Object> data) {
		this.data = data;
	}

	public Multimap<Integer, Object> getDataWithTitles() {
		Multimap<Integer, Object> allData = ArrayListMultimap.create();
		titles.forEach(allData::put);
		allData.putAll(data);
		return allData;
	}

	public List<Point> getDataAsPoints() {
		List<Point> points = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < data.get(0).size(); rowIndex++) {
			Point point = new Point();
			List<BigDecimal> vector = new ArrayList<>();
			for (int columnIndex = 0; columnIndex < data.keySet().size(); columnIndex++) {
				if (columnIndex + 1 == data.keySet().size()) {
					try {
						point.setGroup(Integer.parseInt(data.get(columnIndex).toArray()[rowIndex].toString()));
					} catch (NumberFormatException ex) {
						Double val = Double.parseDouble(data.get(columnIndex).toArray()[rowIndex].toString());
						point.setGroup(val.intValue());
					}
				} else {
					BigDecimal value = new BigDecimal(data.get(columnIndex).toArray()[rowIndex].toString());
					vector.add(value);
				}
			}
			point.setVector(vector);
			points.add(point);
		}
		return points;
	}

	public List<SpaceVector> getVectors() {
		return vectors;
	}

	public void setVectors(List<SpaceVector> vectors) {
		this.vectors = vectors;
	}

	public void removeAllPoints(Set<Point> points) {
		List<Point> dataPoints = getDataAsPoints();
		dataPoints.removeAll(points);

		Multimap<Integer, Object> map = ArrayListMultimap.create();
		for (Point point : dataPoints) {
			for (int columnIndex = 0; columnIndex < point.getVector().size(); columnIndex++) {
				map.put(columnIndex, point.getVector().get(columnIndex));
			}
			map.put(point.getVector().size(), point.getGroup());
		}
		this.data = map;
	}
}
