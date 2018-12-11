package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.ChartMouseListener;


public class RectAreaListener implements AreaListener {

  private final int x;
  private final int y;
  private final int w;
  private final int h;
  private final ChartMouseListener listener;

  public RectAreaListener(int x, int y, int w, int h, ChartMouseListener l) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.listener = l;
  }

  @Override
  public boolean isInside(int xx, int yy) {
    return (xx >= x && yy >= y && xx <= x + w && yy <= y + h);
  }

  @Override
  public ChartMouseListener getListener() {
    return listener;
  }
}
