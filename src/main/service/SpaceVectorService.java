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

				String xmin = vector.getCoordinateBorders().get(0).getMin().equals(BigDecimal.valueOf(Double.MIN_VALUE)) ? "-INFINITY" : vector.getCoordinateBorders().get(0).getMin().toString();
				String xmax = vector.getCoordinateBorders().get(0).getMax().equals(BigDecimal.valueOf(Double.MAX_VALUE)) ? "INFINITY" : vector.getCoordinateBorders().get(0).getMax().toString();
				String ymin = vector.getCoordinateBorders().get(1).getMin().equals(BigDecimal.valueOf(Double.MIN_VALUE)) ? "-INFINITY" : vector.getCoordinateBorders().get(1).getMin().toString();
				String ymax = vector.getCoordinateBorders().get(1).getMax().equals(BigDecimal.valueOf(Double.MAX_VALUE)) ? "INFINITY" : vector.getCoordinateBorders().get(1).getMax().toString();

				System.out.println("\nV: x[" + xmin + ", " + xmax + "]  y[" + ymin + ", " + ymax + "]");
				vectors.add(vector);
			}
		}

		System.out.println("sup");
	}
}
