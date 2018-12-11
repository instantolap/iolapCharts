package com.instantolap.charts;

public interface PositionAxis extends Axis {

  int getSamplePosition(Cube cube, int sample);

  int getSampleWidth();

}
