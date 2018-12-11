package com.instantolap.charts.impl.animation;

public class GrowContentAnim extends ContentAnimationAdapter {

  @Override
  public double getValue(double progress, double bar, double value) {
    return value * progress;
  }
}
