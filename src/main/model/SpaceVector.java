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
		StringBuilder builder  = new StringBuilder();
		builder.append("[ ");
		vector.forEach(v -> builder.append((v) ? "1 " : "0 "));
		return builder.append("]").toString();
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Border {
		private BigDecimal min;
		private BigDecimal max;
	}
}


