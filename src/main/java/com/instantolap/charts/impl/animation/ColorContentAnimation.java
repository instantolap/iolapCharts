package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public abstract class ColorContentAnimation extends ContentAnimationAdapter {

  @Override
  public ChartColor getLabelColor(double progress, double bar, ChartColor color) {
    return calc(progress, bar, color);
  }

  @Override
  public ChartColor getOutlineColor(double progress, double bar, ChartColor color) {
    return calc(progress, bar, color);
  }

  @Override
  public ChartColor getSampleColor(double progress, double bar, ChartColor color) {
    return calc(progress, bar, color);
  }

  @Override
  public ChartColor getShadowColor(double progress, double bar, ChartColor color) {
    return calc(progress, bar, color);
  }

  protected abstract ChartColor calc(double progress, double bar, ChartColor color);

}
