package main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SpaceVector {
	private Map<Integer, Border> coordinateBorders;
	private List<Boolean> vector;
	private Integer group;

	public SpaceVector() {
		this.coordinateBorders = new HashMap<>();
		this.vector = new ArrayList<>();
	}

	public String getVectortoString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\t[ ");
		vector.forEach(v -> builder.append((v) ? "1 " : "0 "));
		return builder.append("]").toString();
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Border {
		private BigDecimal min;
		private BigDecimal max;

		public String toString() {
			String xmin = min.equals(BigDecimal.valueOf(Double.MIN_VALUE)) ? "-INFIN" : min.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
			String xmax = max.equals(BigDecimal.valueOf(Double.MAX_VALUE)) ? "INFIN" : max.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
			String ymin = min.equals(BigDecimal.valueOf(Double.MIN_VALUE)) ? "-INFIN" : min.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
			String ymax = max.equals(BigDecimal.valueOf(Double.MAX_VALUE)) ? "INFIN" : max.setScale(3, BigDecimal.ROUND_HALF_UP).toString();

			return "x[" + xmin + ", " + xmax + "]  y[" + ymin + ", " + ymax + "]";
		}
	}
}


