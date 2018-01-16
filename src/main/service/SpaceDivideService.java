package main.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import main.model.AppData;
import main.model.DividingDataSet;
import main.model.DividingLine;
import main.model.Point;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SpaceDivideService {
	private int coordinatesAmount;
	private Map<Integer, List<Point>> sortedPoints = new HashMap<>();
	private List<Point> pointsToDeleteList = new ArrayList<>();
	private Set<Point> allDeletedPointsList = new HashSet<>();

	public SpaceDivideService() {
		List<Point> points = AppData.getInstance().getDataAsPoints();
		coordinatesAmount = points.get(0).getVector().size();

		for (int coordinate = 0; coordinate < coordinatesAmount; coordinate++) {
			int finalCoordinate = coordinate;
			List<Point> sorted = new ArrayList<>(points);
			sorted.sort(Comparator.comparing(point -> point.getVector().get(finalCoordinate)));
			sortedPoints.put(coordinate, sorted);
		}
	}

	public List<DividingLine> divide() throws IOException {
		List<DividingLine> lines = new ArrayList<>();

		while (true) {
			DividingDataSet dataSet = getDataSet(false);

			int groupAmount = sortedPoints.get(dataSet.getMaxPointsCoord()).stream().map(Point::getGroup).collect(Collectors.toSet()).size();

			if (dataSet.getMaxPointsAmount() + 1 == sortedPoints.get(dataSet.getMaxPointsCoord()).size() || groupAmount == 1
					|| sortedPoints.get(dataSet.getMaxPointsCoord()).size() == 0) {
				break;
			}

			if (dataSet.getMaxPointsAmount() == 0) {
				dataSet = getDataSet(true);
				if (!pointsToDeleteList.isEmpty()) {
					sortedPoints.get(dataSet.getMaxPointsCoord()).removeAll(pointsToDeleteList);
					allDeletedPointsList.addAll(pointsToDeleteList);
					pointsToDeleteList.clear();
				}
				//break;
			}

			lines.add(getLineFrom(dataSet));

			int pointsAmount = sortedPoints.get(dataSet.getMaxPointsCoord()).size();
			if (dataSet.isAsc()) {
				sortedPoints.get(dataSet.getMaxPointsCoord()).subList(0, dataSet.getFirstOutPointIndex()).clear();
			} else {
				sortedPoints.get(dataSet.getMaxPointsCoord()).subList(dataSet.getLastInPointIndex(), pointsAmount).clear();
			}

			sortPointsApartFrom(sortedPoints.get(dataSet.getMaxPointsCoord()), dataSet.getMaxPointsCoord());
		}

		System.out.println("AMOUNT OF POINTS DELETED: " + allDeletedPointsList.size());
		return lines;
	}

	private DividingDataSet getDataSet(boolean withDelete) {
		DividingDataSet dataSet = new DividingDataSet();

		for (Map.Entry<Integer, List<Point>> sorted : sortedPoints.entrySet()) {
			if (sorted.getValue().size() == 0) {
				break;
			}
			DividingDataSet newDataSetAsc = (withDelete) ?
					getDataWithDeleteFor(sorted.getKey(), sorted.getValue(), true) :
					getDataFor(sorted.getKey(), sorted.getValue(), true);

			DividingDataSet newDataSetDesc = (withDelete) ?
					getDataWithDeleteFor(sorted.getKey(), sorted.getValue(), false) :
					getDataFor(sorted.getKey(), sorted.getValue(), false);

			if (dataSet.getMaxPointsAmount() < newDataSetAsc.getMaxPointsAmount()) {
				dataSet = newDataSetAsc;
			}
			if (dataSet.getMaxPointsAmount() < newDataSetDesc.getMaxPointsAmount()) {
				dataSet = newDataSetDesc;
			}
		}
		return dataSet;
	}

	private DividingDataSet getDataFor(Integer sortedCoord, List<Point> sorted, boolean isAsc) {
		DividingDataSet dataSet = new DividingDataSet();
		int pointsToDeleteListAmount = 0;
		int lastPointIndex = 0;
		int lastGroup = 0;

		if (isAsc) {
			for (int pointIndex = 0; pointIndex < sorted.size() - 1; pointIndex++) {
				Point actual = sorted.get(pointIndex);
				Point next = sorted.get(pointIndex + 1);

				if (!actual.getVector().get(sortedCoord).equals(next.getVector().get(sortedCoord))) {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteListAmount++;
						lastPointIndex = pointIndex;
						lastGroup = actual.getGroup();
					} else {
						lastPointIndex = pointIndex;
						lastGroup = actual.getGroup();
						break;
					}
				} else {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteListAmount++;
					} else {
						break;
					}
				}
			}
		} else {
			for (int pointIndex = sorted.size() - 1; pointIndex > 1; pointIndex--) {
				Point actual = sorted.get(pointIndex);
				Point next = sorted.get(pointIndex - 1);

				if (!actual.getVector().get(sortedCoord).equals(next.getVector().get(sortedCoord))) {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteListAmount++;
						lastPointIndex = pointIndex;
						lastGroup = actual.getGroup();
					} else {
						lastPointIndex = pointIndex;
						lastGroup = actual.getGroup();
						break;
					}
				} else {
					if (actual.getGroup() == next.getGroup()) {
						pointsToDeleteListAmount++;
					} else {
						System.out.print(" ");
						break;
					}
				}
			}
		}

		if (lastPointIndex == 0) {
			pointsToDeleteListAmount = 0;
		}
		dataSet.setMaxPointsAmount(pointsToDeleteListAmount);
		dataSet.setMaxPointsCoord(sortedCoord);
		dataSet.setLastInPointIndex(lastPointIndex);
		if (isAsc) {
			dataSet.setFirstOutPointIndex(lastPointIndex + 1);
			dataSet.setAsc(true);
		} else {
			dataSet.setFirstOutPointIndex(lastPointIndex - 1);
			dataSet.setAsc(false);
		}
		dataSet.setInGroup(lastGroup);
		return dataSet;
	}

	private DividingDataSet getDataWithDeleteFor(Integer sortedCoord, List<Point> sorted, boolean isAsc) {
		BigDecimal value = null;
		if (isAsc) {
			value = sorted.get(0).getVector().get(sortedCoord);
		} else {
			value = sorted.get(sorted.size() - 1).getVector().get(sortedCoord);
		}

		BigDecimal finalValue = value;
		List<Point> points = sorted.stream()
				.filter(point -> point.getVector().get(sortedCoord).equals(finalValue)).collect(Collectors.toList());
		Multimap<Integer, Point> groupedPoints = ArrayListMultimap.create();
		points.forEach(point -> groupedPoints.put(point.getGroup(), point));

		//version first - check how much can you delete when points from line from spec.group is deleted. Not working properly
		/*Map<Integer, DividingDataSet> groupedDataSets = new HashMap<>();

		for (Integer group : groupedPoints.keySet()) {
			List<Point> copySorted = new ArrayList<>(sorted);
			copySorted.removeAll(groupedPoints.get(group));
			DividingDataSet asc = getDataFor(sortedCoord, copySorted, true);
			DividingDataSet desc = getDataFor(sortedCoord, copySorted, false);
			if (asc.getMaxPointsAmount() > desc.getMaxPointsAmount()) {
				groupedDataSets.put(group, asc);
			} else {
				groupedDataSets.put(group, desc);
			}
		}

		int maxAmount = groupedDataSets.entrySet().stream()
				.map(entry -> entry.getValue().getMaxPointsAmount()).mapToInt(Integer::intValue).max().getAsInt();
		for (Map.Entry<Integer, DividingDataSet> entry : new HashMap<>(groupedDataSets).entrySet()) {
			entry.getValue().setToDelete(groupedPoints.get(entry.getKey()).size());
			if (entry.getValue().getMaxPointsAmount() != maxAmount) {
				groupedDataSets.remove(entry.getKey(), entry.getValue());
			}
		}*/

		int minToDelete = 1000000;
		int minToDeleteKey = 0;
		for (Integer key : groupedPoints.keySet()) {
			if (groupedPoints.get(key).size() < minToDelete) {
				minToDelete = groupedPoints.get(key).size();
				minToDeleteKey = key;
			}
		}

		List<Point> copySorted = new ArrayList<>(sorted);
		copySorted.removeAll(groupedPoints.get(minToDeleteKey));
		pointsToDeleteList.addAll(groupedPoints.get(minToDeleteKey));
		DividingDataSet dataSet = getDataFor(sortedCoord, copySorted, isAsc);
		//groupedDataSets.put(minToDeleteKey, asc);

		return dataSet;
	}

	private DividingLine getLineFrom(DividingDataSet dataSet) {
		DividingLine divideLine = new DividingLine();
		divideLine.setCoordinate(dataSet.getMaxPointsCoord());

		BigDecimal afterLine = sortedPoints.get(dataSet.getMaxPointsCoord()).get(dataSet.getFirstOutPointIndex()).getVector().get(dataSet.getMaxPointsCoord());

		BigDecimal beforeLine = (dataSet.getLastInPointIndex() == sortedPoints.get(dataSet.getMaxPointsCoord()).size()) ?
				BigDecimal.ZERO :
				sortedPoints.get(dataSet.getMaxPointsCoord()).get(dataSet.getLastInPointIndex()).getVector().get(dataSet.getMaxPointsCoord());

		BigDecimal lineValue = (afterLine.add(beforeLine)).divide(BigDecimal.valueOf(2.0), 6, BigDecimal.ROUND_HALF_UP);
		divideLine.setValue(lineValue);
		divideLine.setAsc(dataSet.isAsc());
		divideLine.setGroupAccepted(dataSet.getInGroup());
		return divideLine;
	}

	private void sortPointsApartFrom(List<Point> pointsUpdated, int coordinateToOmit) {
		for (int coordinate = 0; coordinate < coordinatesAmount; coordinate++) {
			if (coordinate != coordinateToOmit) {
				int finalCoordinate = coordinate;
				List<Point> sorted = new ArrayList<>(pointsUpdated);
				sorted.sort(Comparator.comparing(point -> point.getVector().get(finalCoordinate)));
				sortedPoints.put(coordinate, sorted);
			}
		}
	}

	public Set<Point> getAllDeletedPointsList() {
		return allDeletedPointsList;
	}
}
