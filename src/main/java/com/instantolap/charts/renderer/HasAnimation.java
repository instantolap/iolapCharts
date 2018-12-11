package com.instantolap.charts.renderer;

public interface HasAnimation {

  boolean isAnimationEnabled();

  void setAnimationEnabled(boolean enabled);

  long getAnimationTime();

  void setAnimationTime(long time);

  void render(double progress) throws ChartException;
}
