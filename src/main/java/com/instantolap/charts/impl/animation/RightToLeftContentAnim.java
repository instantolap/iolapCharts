package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class RightToLeftContentAnim extends ColorContentAnimation {

  @Override
  protected ChartColor calc(double progress, double bar, ChartColor color) {
    if (color == null) {
      return null;
    }

    if ((1 - bar) > progress) {
      return color.setOpacity(0);
    } else {
      return color;
    }
  }
}
