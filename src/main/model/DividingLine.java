package main.model;

import lombok.Getter;
import lombok.Setter;
import main.model.enums.DimensionEnum;

import java.math.BigDecimal;

@Getter
@Setter
public class DividingLine {
    private BigDecimal value;
    private DimensionEnum coordinate;
}
