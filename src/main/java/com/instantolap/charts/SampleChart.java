package com.instantolap.charts;

public interface SampleChart extends Chart {

  ValueAxis getScaleAxis();

  ValueAxis getScaleAxis2();

  SampleAxis getSampleAxis();

  boolean isStacked();

  void setStacked(boolean stacked);

  boolean isRotated();

  void setRotated(boolean rotated);

  XYCanvas getCanvas();
}