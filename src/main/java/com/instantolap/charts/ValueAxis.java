package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface ValueAxis extends ScaleAxis {

  String getFormat();

  void setFormat(String format);

  Integer getDecimalCount();

  void setDecimalCount(Integer count);

  void addCriticalArea(double min, double max, String text, ChartColor color);

  CriticalArea[] getCriticalAreas();

  void setIncludeCritialAreas(boolean include);

  boolean isIncludeCriticalAreas();

  boolean isUseZeroAsBase();

  void setUseZeroAsBase(boolean use);

  @Override
  void setGridPositions(double[] gridPositions);
}
