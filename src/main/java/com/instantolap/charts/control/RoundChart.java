package com.instantolap.charts.control;

import com.instantolap.charts.Chart;
import com.instantolap.charts.RoundCanvas;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.ValueAxis;
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