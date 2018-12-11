package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class EmptyBarChartAnim extends ContentAnimationAdapter {

  @Override
  public ChartColor getShadowColor(double progress, double bar, ChartColor color) {
    return color.setOpacity(getOpacity(progress, bar));
  }

  @Override
  public ChartColor getSampleColor(double progress, double bar, ChartColor color) {
    return color.setOpacity(getOpacity(progress, bar));
  }

  @Override
  public ChartColor getOutlineColor(double progress, double bar, ChartColor color) {
    return color.setOpacity(getOpacity(progress, bar));
  }

  @Override
  public ChartColor getLabelColor(double progress, double bar, ChartColor color) {
    return color.setOpacity(getOpacity(progress, bar));
  }

  private double getOpacity(double progress, double bar) {
    return progress;
  }
}
