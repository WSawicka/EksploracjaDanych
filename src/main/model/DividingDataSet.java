package main.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DividingDataSet {
	private int maxPointsAmount = 0;
	private int maxPointsCoord = 0;
	private int lastInPointIndex = 0;
	private int firstOutPointIndex = 0;
	private boolean isAsc = false;
	private int inGroup = 0;
}
