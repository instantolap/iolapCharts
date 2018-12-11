package com.instantolap.charts;

public interface XYChart extends Chart {

  ValueAxis getXAxis();

  ValueAxis getYAxis();

  XYCanvas getCanvas();

  @Override
  void addContent(Content content);
}