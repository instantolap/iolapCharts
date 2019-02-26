package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class ControllableFadeContentAnimation extends ContentAnimationAdapter {

  double fade = 1;

  public void setFade(double fade) {
    this.fade = fade;
  }

  @Override
  public ChartColor getShadowColor(double progress, double bar, ChartColor color) {
    return fadeColor(color);
  }

  @Override
  public ChartColor getSampleColor(double progress, double bar, ChartColor color) {
    return fadeColor(color);
  }

  @Override
  public ChartColor getOutlineColor(double progress, double bar, ChartColor barOutline) {
    return fadeColor(barOutline);
  }

  @Override
  public ChartColor getLabelColor(double progress, double bar, ChartColor labelColor) {
    return fadeColor(labelColor);
  }

  private ChartColor fadeColor(ChartColor color) {
    if(color==null){
      return null;
    }
    return color.setOpacity(fade);
  }
}
