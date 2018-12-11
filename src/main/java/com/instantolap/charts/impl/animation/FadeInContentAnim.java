package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class FadeInContentAnim extends ColorContentAnimation {

  @Override
  protected ChartColor calc(double progress, double bar, ChartColor color) {
    if (color == null) {
      return null;
    }

    final double opacity = Math.min(1, Math.max(0, 2 * progress - (1 - bar)));
    return color.setOpacity(opacity);
  }
}
