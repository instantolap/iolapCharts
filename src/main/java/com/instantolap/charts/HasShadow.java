package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface HasShadow {

  ChartColor getShadow();

  void setShadow(ChartColor shadow);

  int getShadowXOffset();

  void setShadowXOffset(int offset);

  int getShadowYOffset();

  void setShadowYOffset(int offset);
}
