package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface HasBackground {

  ChartColor getBackground();

  void setBackground(ChartColor background);
}
