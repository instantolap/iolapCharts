package com.instantolap.charts.impl.animation;

public class FlyInContentAnim extends ContentAnimationAdapter {

  @Override
  public double getDistance(double progress, double bar, double distance) {
    double add = (bar - progress) * 10;
    add = Math.max(add, 0);
    return add + distance;
  }
}
