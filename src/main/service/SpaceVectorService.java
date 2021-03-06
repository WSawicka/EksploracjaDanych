package main.service;

import main.model.AppData;
import main.model.DividingLine;
import main.model.Point;
import main.model.SpaceVector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpaceVectorService {
	private List<DividingLine> lines;
	private int coordinatesAmount;
	private List<Point> points;

	public SpaceVectorService(List<DividingLine> lines) {
		this.lines = lines;

		points = AppData.getInstance().getDataAsPoints();
		coordinatesAmount = points.get(0).getVector().size();
	}

	public List<SpaceVector> createVectors() {
		List<List<BigDecimal>> sortedLineValues = new ArrayList<>();

		for (int coordinate = 0; coordinate < coordinatesAmount; coordinate++) {
			final int finalCoord = coordinate;
			List<BigDecimal> coordinateValueList = new ArrayList<>();

			coordinateValueList.add(BigDecimal.valueOf(Double.MAX_VALUE * -1));
			coordinateValueList.addAll(lines.stream().filter(line -> line.getCoordinate() == finalCoord).map(DividingLine::getValue)
					.sorted(Comparator.comparingDouble(BigDecimal::doubleValue)).collect(Collectors.toList()));
			coordinateValueList.add(BigDecimal.valueOf(Double.MAX_VALUE));
			sortedLineValues.add(coordinateValueList);
		}

		List<SpaceVector> vectors = new ArrayList<>();

		if (coordinatesAmount == 2) {
			for (int i = 0; i < sortedLineValues.get(0).size() - 1; i++) {
				for (int j = 0; j < sortedLineValues.get(1).size() - 1; j++) {
					SpaceVector vector = new SpaceVector();
					vector.getCoordinateBorders().put(0, new SpaceVector.Border(sortedLineValues.get(0).get(i), sortedLineValues.get(0).get(i + 1)));
					vector.getCoordinateBorders().put(1, new SpaceVector.Border(sortedLineValues.get(1).get(j), sortedLineValues.get(1).get(j + 1)));
					vectors.add(vector);
				}
			}
		} else if (coordinatesAmount == 3) {
			for (int i = 0; i < sortedLineValues.get(0).size() - 1; i++) {
				for (int j = 0; j < sortedLineValues.get(1).size() - 1; j++) {
					for (int k = 0; k < sortedLineValues.get(2).size() - 1; k++) {
						SpaceVector vector = new SpaceVector();
						vector.getCoordinateBorders().put(0, new SpaceVector.Border(sortedLineValues.get(0).get(i), sortedLineValues.get(0).get(i + 1)));
						vector.getCoordinateBorders().put(1, new SpaceVector.Border(sortedLineValues.get(1).get(j), sortedLineValues.get(1).get(j + 1)));
						vector.getCoordinateBorders().put(2, new SpaceVector.Border(sortedLineValues.get(2).get(k), sortedLineValues.get(2).get(k + 1)));
						vectors.add(vector);
					}
				}
			}
		} else if (coordinatesAmount == 4) {
			for (int i = 0; i < sortedLineValues.get(0).size() - 1; i++) {
				for (int j = 0; j < sortedLineValues.get(1).size() - 1; j++) {
					for (int k = 0; k < sortedLineValues.get(2).size() - 1; k++) {
						for (int l = 0; l < sortedLineValues.get(3).size() - 1; l++) {
							SpaceVector vector = new SpaceVector();
							vector.getCoordinateBorders().put(0, new SpaceVector.Border(sortedLineValues.get(0).get(i), sortedLineValues.get(0).get(i + 1)));
							vector.getCoordinateBorders().put(1, new SpaceVector.Border(sortedLineValues.get(1).get(j), sortedLineValues.get(1).get(j + 1)));
							vector.getCoordinateBorders().put(2, new SpaceVector.Border(sortedLineValues.get(2).get(k), sortedLineValues.get(2).get(k + 1)));
							vector.getCoordinateBorders().put(3, new SpaceVector.Border(sortedLineValues.get(3).get(l), sortedLineValues.get(3).get(l + 1)));
							vectors.add(vector);
						}
					}
				}
			}
		} else {
			return new ArrayList<>();
		}

		for (DividingLine line : lines) {
			for (SpaceVector vector : vectors) {
				BigDecimal min = vector.getCoordinateBorders().get(line.getCoordinate()).getMin();
				BigDecimal max = vector.getCoordinateBorders().get(line.getCoordinate()).getMax();
				vector.getVector().add((min.doubleValue() == line.getValue().doubleValue() && !line.isAsc() ||
						max.doubleValue() == line.getValue().doubleValue() && line.isAsc()));
			}
		}

		for (SpaceVector vector : vectors) {
			Set<Integer> group;
			if (coordinatesAmount == 2) {
				group =points.stream()
						.filter(point ->
								point.getVector().get(0).doubleValue() >= vector.getCoordinateBorders().get(0).getMin().doubleValue() &&
										point.getVector().get(0).doubleValue() <= vector.getCoordinateBorders().get(0).getMax().doubleValue())
						.filter(point ->
								point.getVector().get(1).doubleValue() >= vector.getCoordinateBorders().get(1).getMin().doubleValue() &&
										point.getVector().get(1).doubleValue() <= vector.getCoordinateBorders().get(1).getMax().doubleValue())
						.map(Point::getGroup).collect(Collectors.toSet());
			}
			else if (coordinatesAmount ==3) {
				group =points.stream()
						.filter(point ->
								point.getVector().get(0).doubleValue() >= vector.getCoordinateBorders().get(0).getMin().doubleValue() &&
										point.getVector().get(0).doubleValue() <= vector.getCoordinateBorders().get(0).getMax().doubleValue())
						.filter(point ->
								point.getVector().get(1).doubleValue() >= vector.getCoordinateBorders().get(1).getMin().doubleValue() &&
										point.getVector().get(1).doubleValue() <= vector.getCoordinateBorders().get(1).getMax().doubleValue())
						.filter(point ->
								point.getVector().get(2).doubleValue() >= vector.getCoordinateBorders().get(2).getMin().doubleValue() &&
										point.getVector().get(2).doubleValue() <= vector.getCoordinateBorders().get(2).getMax().doubleValue())
						.map(Point::getGroup).collect(Collectors.toSet());
			}
			else {
				group =points.stream()
					.filter(point ->
							point.getVector().get(0).doubleValue() >= vector.getCoordinateBorders().get(0).getMin().doubleValue() &&
									point.getVector().get(0).doubleValue() <= vector.getCoordinateBorders().get(0).getMax().doubleValue())
					.filter(point ->
							point.getVector().get(1).doubleValue() >= vector.getCoordinateBorders().get(1).getMin().doubleValue() &&
									point.getVector().get(1).doubleValue() <= vector.getCoordinateBorders().get(1).getMax().doubleValue())
						.filter(point ->
								point.getVector().get(2).doubleValue() >= vector.getCoordinateBorders().get(2).getMin().doubleValue() &&
										point.getVector().get(2).doubleValue() <= vector.getCoordinateBorders().get(2).getMax().doubleValue())
						.filter(point ->
								point.getVector().get(3).doubleValue() >= vector.getCoordinateBorders().get(3).getMin().doubleValue() &&
										point.getVector().get(3).doubleValue() <= vector.getCoordinateBorders().get(3).getMax().doubleValue())
					.map(Point::getGroup).collect(Collectors.toSet());
			}
			if (group.stream().findFirst().isPresent()) {
				vector.setGroup(group.stream().findFirst().get());
			} else {
				for (DividingLine line : lines) {
					if ((!line.isAsc() && line.getValue().compareTo(vector.getCoordinateBorders().get(line.getCoordinate()).getMin()) != 1) ||
							(line.isAsc() && line.getValue().compareTo(vector.getCoordinateBorders().get(line.getCoordinate()).getMax()) != -1)) {
						vector.setGroup(line.getGroupAccepted());
						break;
					}
				}
			}
		}

		vectors.forEach(vector -> {
			vector.getCoordinateBorders().forEach((k, v) -> System.out.print(v.toString() + " "));
			System.out.println("\t" + vector.getVectortoString() + " -> " + ((vector.getGroup() == null) ? "-" : vector.getGroup()));
		});

		return vectors;
	}
}
