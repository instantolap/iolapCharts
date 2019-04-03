package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;

public interface ScaleAxis extends Axis {

  void clearMeasures();

  void addMeasures(String... measure);

  Double getMin();

  void setMin(Double min);

  Double getMax();

  void setMax(Double max);

  double getMinTickSize();

  void setMinTickSize(double minTickSize);

  int getMaxLineCount();

  void setMaxLineCount(int count);

  Double getTick();

  void setTick(Double stepSize);

  Double getUserTick();

  void setUserTick(Double stepSize);

  double getPosition(double v);

  double getRadius(double v);

  void enableZoom(boolean enable);

  boolean isZoomEnabled();

  double getZoomStep();

  void setZoomStep(double step);

  void zoom(double zoom, double center);

  void translate(double shift);

  void addListener(ScaleAxisListener listener);

  void removeListener(ScaleAxisListener listener);

  void setGridPositions(double[] positions);

  void clearTargets();

  void addTargetLine(
    double value, String text, ChartColor color, ChartColor background, ChartStroke stroke);

  TargetLine[] getTargetLines();

  boolean isIncludeTargets();

  void setIncludeTargets(boolean include);
}
