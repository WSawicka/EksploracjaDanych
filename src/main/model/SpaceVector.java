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
	private int group;

	public SpaceVector() {
		this.coordinateBorders = new HashMap<>();
		this.vector = new ArrayList<>();
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Border {
		private BigDecimal min;
		private BigDecimal max;
	}
}


