package main.service;

import main.model.AppData;
import main.model.Point;
import main.model.SpaceVector;

import java.util.Map;

public class ClassificationService {
	public Integer getPointGroup(Point pointToClassify) {
		Integer group = null;
		for (SpaceVector vector : AppData.getInstance().getVectors()) {
			for (Map.Entry<Integer, SpaceVector.Border> borderEntry : vector.getCoordinateBorders().entrySet()) {
				if (pointToClassify.getVector().get(borderEntry.getKey()).compareTo(borderEntry.getValue().getMin()) == -1 ||
						pointToClassify.getVector().get(borderEntry.getKey()).compareTo(borderEntry.getValue().getMax()) == 1) {
					group = null;
					break;
				} else {
					group = vector.getGroup();
				}
			}
			if (group != null) {
				break;
			}
		}
		return group;
	}
}
