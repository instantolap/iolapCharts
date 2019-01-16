package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public interface RoundChart extends Chart {

  ValueAxis getScaleAxis();

  SampleAxis getSampleAxis();

  boolean isStacked();

  void setStacked(boolean stacked);

  RoundCanvas getCanvas();

  ChartColor getInnerBorder();

  void setInnerBorder(ChartColor color);

  ChartStroke getInnerBorderStroke();

  void setInnerBorderStroke(ChartStroke stroke);

  int getInnerPadding();

  void setInnerPadding(int padding);

  boolean getEnableRotation();

  void setEnableRotation(boolean enableRotation);
}