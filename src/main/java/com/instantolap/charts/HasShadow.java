package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface HasShadow {

  ChartColor getShadow();

  void setShadow(ChartColor shadow);

  double getShadowXOffset();

  void setShadowXOffset(double offset);

  double getShadowYOffset();

  void setShadowYOffset(double offset);
}
