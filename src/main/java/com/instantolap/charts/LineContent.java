package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface LineContent
  extends SampleContent, HasValueLabels, HasRegression, HasValueAxis, HasMeasure
{

  boolean isAreaChart();

  void setAreaChart(boolean area);

  double getAreaOpacity();

  void setAreaOpacity(double opacity);

  boolean isConnected();

  void setConnected(boolean connected);

  boolean isInterpolated();

  void setInterpolated(boolean interpolated);

  boolean isStepLine();

  void setStepLine(boolean stairs);

  void setFillColor(int start, int end, ChartColor colors);

  ChartColor getFillColor(int series);

  Integer getFillStart(int series);

}