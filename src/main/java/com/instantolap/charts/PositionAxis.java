package com.instantolap.charts;

public interface PositionAxis extends Axis {

  double getSamplePosition(Cube cube, int sample, int series);

  double getSampleWidth();

}
