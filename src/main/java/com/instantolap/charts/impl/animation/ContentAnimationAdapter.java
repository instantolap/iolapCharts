package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class ContentAnimationAdapter implements ContentAnimation {

  @Override
  public double getValue(double progress, double bar, double value) {
    return value;
  }

  @Override
  public ChartColor getShadowColor(double progress, double bar, ChartColor color) {
    return color;
  }

  @Override
  public ChartColor getSampleColor(double progress, double bar, ChartColor color) {
    return color;
  }

  @Override
  public ChartColor getOutlineColor(double progress, double bar, ChartColor barOutline) {
    return barOutline;
  }

  @Override
  public ChartColor getLabelColor(double progress, double bar, ChartColor labelColor) {
    return labelColor;
  }

  @Override
  public double getDistance(double progress, double bar, double distance) {
    return distance;
  }

  @Override
  public double getX(double progress, double x, double width, double height) {
    return x;
  }

  @Override
  public double getY(double progress, double y, double width, double height) {
    return y;
  }
}
