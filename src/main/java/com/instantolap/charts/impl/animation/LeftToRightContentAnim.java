package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class LeftToRightContentAnim extends ColorContentAnimation {

  @Override
  protected ChartColor calc(double progress, double bar, ChartColor color) {
    if (color == null) {
      return null;
    }

    if (bar > progress) {
      return color.setOpacity(0);
    } else {
      return color;
    }
  }
}
