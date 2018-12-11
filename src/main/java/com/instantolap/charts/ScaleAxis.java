package com.instantolap.charts;

public interface ScaleAxis extends Axis {

  void clearMeasures();

  void addMeasures(String... measure);

  Double getMin();

  void setMin(Double min);

  Double getMax();

  void setMax(Double max);

  int getMinTickSize();

  void setMinTickSize(int minTickSize);

  int getMaxLineCount();

  void setMaxLineCount(int count);

  Double getTick();

  void setTick(Double stepSize);

  Double getUserTick();

  void setUserTick(Double stepSize);

  int getPosition(double v);

  int getRadius(double v);

  void enableZoom(boolean enable);

  boolean isZoomEnabled();

  double getZoomStep();

  void setZoomStep(double step);

  void zoom(double zoom, double center);

  void translate(double shift);

  void addListener(ScaleAxisListener listener);

  void removeListener(ScaleAxisListener listener);

  void setGridPositions(double[] positions);
}
