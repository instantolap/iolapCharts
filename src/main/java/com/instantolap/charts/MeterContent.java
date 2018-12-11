package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface MeterContent extends HasValueLabels, SampleContent, HasMeasure {

  double getPinSize();

  void setPinSize(double size);

  ChartColor getPinColor();

  void setPinColor(ChartColor color);
}