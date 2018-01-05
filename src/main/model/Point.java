package main.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class Point {
	private List<BigDecimal> vector;
	private int group;
}
