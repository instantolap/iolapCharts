package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public interface XYCanvas extends Canvas {

  ChartColor getHorizontalBackground2();

  void setHorizontalBackground2(ChartColor background);

  ChartColor getVerticalBackground2();

  void setVerticalBackground2(ChartColor background);

  ChartColor getBaseLine();

  void setBaseLine(ChartColor color);

  ChartColor[] getVerticalGrid();

  void setVerticalGrid(ChartColor... colors);

  ChartStroke getVerticalGridStroke();

  void setVerticalGridStroke(ChartStroke color);

  ChartColor[] getHorizontalGrid();

  void setHorizontalGrid(ChartColor... colors);

  ChartStroke getHorizontalGridStroke();

  void setHorizontalGridStroke(ChartStroke color);
}