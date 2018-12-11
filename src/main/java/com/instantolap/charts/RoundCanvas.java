package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public interface RoundCanvas extends Canvas {

  ChartColor getScaleBackground();

  void setScaleBackground(ChartColor background);

  ChartColor getScaleBackground2();

  void setScaleBackground2(ChartColor background);

  ChartColor getGrid();

  void setGrid(ChartColor color);

  ChartColor getBaseLine();

  void setBaseLine(ChartColor color);

  ChartStroke getGridStroke();

  void setGridStroke(ChartStroke color);

  boolean isRound();

  void setRound(boolean round);
}