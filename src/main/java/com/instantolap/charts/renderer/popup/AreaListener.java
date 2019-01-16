package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.ChartMouseListener;


public interface AreaListener {

  boolean isInside(double x, double y);

  ChartMouseListener getListener();
}
