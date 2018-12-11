package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public interface ValueAxis extends ScaleAxis {

  String getFormat();

  void setFormat(String format);

  Integer getDecimalCount();

  void setDecimalCount(Integer count);

  void clearTargets();

  void addTargetLine(
    double value, String text, ChartColor color, ChartColor background, ChartStroke stroke);

  TargetLine[] getTargetLines();

  boolean isIncludeTargets();

  void setIncludeTargets(boolean include);

  void addCriticalArea(double min, double max, String text, ChartColor color);

  CriticalArea[] getCriticalAreas();

  void setIncludeCritialAreas(boolean include);

  boolean isIncludeCriticalAreas();

  boolean isUseZeroAsBase();

  void setUseZeroAsBase(boolean use);

  @Override
  void setGridPositions(double[] gridPositions);
}
