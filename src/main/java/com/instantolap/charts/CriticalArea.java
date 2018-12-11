package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public class CriticalArea {
  public double min, max;
  public String text;
  public ChartColor color;

  public CriticalArea(double min, double max, String text, ChartColor color) {
    this.min = min;
    this.max = max;
    this.text = text;
    this.color = color;
  }
}
