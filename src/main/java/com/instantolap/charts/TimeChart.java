package com.instantolap.charts;

public interface TimeChart extends Chart {

  ValueAxis getScaleAxis();

  ValueAxis getScaleAxis2();

  TimeAxis getTimeAxis();

  XYCanvas getCanvas();

  boolean isRotated();

  void setRotated(boolean rotated);

  boolean isStacked();

  void setStacked(boolean stacked);
}