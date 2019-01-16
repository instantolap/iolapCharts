package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.ChartMouseListener;


public class RectAreaListener implements AreaListener {

  private final double x;
  private final double y;
  private final double w;
  private final double h;
  private final ChartMouseListener listener;

  public RectAreaListener(double x, double y, double w, double h, ChartMouseListener l) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.listener = l;
  }

  @Override
  public boolean isInside(double xx, double yy) {
    return (xx >= x && yy >= y && xx <= x + w && yy <= y + h);
  }

  @Override
  public ChartMouseListener getListener() {
    return listener;
  }
}
