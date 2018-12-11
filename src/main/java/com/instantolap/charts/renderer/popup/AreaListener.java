package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.ChartMouseListener;


public interface AreaListener {

  boolean isInside(int x, int y);

  ChartMouseListener getListener();
}
