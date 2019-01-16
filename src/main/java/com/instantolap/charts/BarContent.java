package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface BarContent
  extends SampleContent, HasValueLabels, HasRegression, HasValueAxis, HasMeasure
{

  double getBarWidth();

  void setBarWidth(double width);

  double getBarSpacing();

  void setBarSpacing(double spacing);

  boolean isMultiColor();

  void setMultiColor(boolean multiColor);

  String getLowerMeasure();

  void setLowerMeasure(String measure);

  ChartColor getColorUp();

  void setColorUp(ChartColor color);

  ChartColor getColorDown();

  void setColorDown(ChartColor color);

  String getMinMeasure();

  void setMinMeasure(String measure);

  String getMaxMeasure();

  void setMaxMeasure(String measure);
}