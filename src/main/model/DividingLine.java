package main.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DividingLine {
	private BigDecimal value;
	private int coordinate;
	private boolean isAsc; // accepted and deleted points side
	private int groupAccepted;
}
