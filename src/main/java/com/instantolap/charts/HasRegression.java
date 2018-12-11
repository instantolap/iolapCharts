package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public interface HasRegression extends Content {

  ChartColor getRegressionColor();

  void setRegressionColor(ChartColor color);

  ChartStroke getRegressionStroke();

  void setRegressionStroke(ChartStroke stroke);
}