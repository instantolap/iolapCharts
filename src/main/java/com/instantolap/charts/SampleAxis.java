package com.instantolap.charts;

public interface SampleAxis extends PositionAxis {

  String[] getLabels();

  void setLabels(String[] labels);

  void setGridPositions(double[] positions);
}
