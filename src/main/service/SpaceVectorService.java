package main.service;

import main.model.AppData;
import main.model.DividingLine;
import main.model.Point;
import main.model.SpaceVector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceVectorService {
	private List<DividingLine> lines;
	private int coordinatesAmount;

	public SpaceVectorService(List<DividingLine> lines) {
		this.lines = lines;

		List<Point> points = AppData.getInstance().getDataAsPoints();
		coordinatesAmount = points.get(0).getVector().size();
	}

	public void createVectors() {
		List<List<BigDecimal>> sortedLineValues = new ArrayList<>();

		for (int coordinate = 0; coordinate < coordinatesAmount; coordinate++) {
			final int finalCoord = coordinate;
			List<BigDecimal> coordinateValueList = new ArrayList<>();

			coordinateValueList.add(BigDecimal.valueOf(Double.MIN_VALUE));
			coordinateValueList.addAll(lines.stream().filter(line -> line.getCoordinate() == finalCoord).map(DividingLine::getValue)
					.sorted(Comparator.comparingDouble(BigDecimal::doubleValue)).collect(Collectors.toList()));
			coordinateValueList.add(BigDecimal.valueOf(Double.MAX_VALUE));
			sortedLineValues.add(coordinateValueList);
		}

		List<SpaceVector> vectors = new ArrayList<>();

		for (int i = 0; i < sortedLineValues.get(0).size() - 1; i++) {
			for (int j = 0; j < sortedLineValues.get(1).size() - 1; j++) {
				SpaceVector vector = new SpaceVector();
				vector.getCoordinateBorders().put(0, new SpaceVector.Border(sortedLineValues.get(0).get(i), sortedLineValues.get(0).get(i + 1)));
				vector.getCoordinateBorders().put(1, new SpaceVector.Border(sortedLineValues.get(1).get(j), sortedLineValues.get(1).get(j + 1)));
				vectors.add(vector);
			}
		}

		for (DividingLine line : lines) {
			for (SpaceVector vector : vectors) {
				BigDecimal min = vector.getCoordinateBorders().get(line.getCoordinate()).getMin();
				BigDecimal max = vector.getCoordinateBorders().get(line.getCoordinate()).getMax();
				if (min.doubleValue() == line.getValue().doubleValue() && !line.isAsc() ||
						max.doubleValue() == line.getValue().doubleValue() && line.isAsc()) {
					vector.getVector().add(true);
					vector.setGroup(line.getGroupAccepted());
				} else {
					vector.getVector().add(false);
				}
			}
		}

		vectors.forEach(vector -> {
			vector.getCoordinateBorders().forEach((k,v) -> System.out.print(v.toString() + " "));
			System.out.println("\t" + vector.getVectortoString() + " -> " + ((vector.getGroup() == null) ? "-" : vector.getGroup()));
		});
	}
}
