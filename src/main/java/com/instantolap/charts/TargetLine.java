package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public class TargetLine {
  public double value;
  public String text;
  public String valueText;
  public ChartColor color;
  public ChartColor background;
  public ChartStroke stroke;

  public TargetLine(
    double value, String text, ChartColor color, ChartColor background, ChartStroke stroke)
  {
    this.value = value;
    this.text = text;
    this.color = color;
    this.background = background;
    this.stroke = stroke;
  }
}
