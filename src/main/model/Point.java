package main.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Point {
	private List<BigDecimal> vector;
	private int group;

	public Point() {
		this.vector = new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point point = (Point) o;

		if (group != point.group) return false;
		if (vector.size() != point.vector.size()) return false;
		for (int i = 0; i < vector.size(); i++) {
			if (!vector.get(i).equals(point.getVector().get(i))) {
				return false;
			}
		}
		return vector.equals(point.vector);
	}

	@Override
	public int hashCode() {
		int result = vector.hashCode();
		result = 31 * result + group;
		return result;
	}
}
