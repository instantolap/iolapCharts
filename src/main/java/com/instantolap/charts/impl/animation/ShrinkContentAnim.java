package com.instantolap.charts.impl.animation;

public class ShrinkContentAnim extends ContentAnimationAdapter {

  @Override
  public double getValue(double progress, double bar, double value) {
    return value * 1 / progress;
  }
}
