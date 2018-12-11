package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public class FadeInCanvasAnim implements CanvasAnimation {


  @Override
  public ChartColor getBackground(double progress, ChartColor color) {
    return color;
  }


  @Override
  public ChartColor getBorder(double progress, ChartColor color) {
    return color;
  }


  @Override
  public ChartColor getVerticalBackground(double progress, double grid, ChartColor color) {
    return color.setOpacity(getOpacity(progress, grid));
  }


  @Override
  public ChartColor getHorizontalBackground(double progress, double grid, ChartColor color) {
    return color.setOpacity(getOpacity(progress, grid));
  }

  @Override
  public ChartColor getVerticalGrid(double progress, double grid, ChartColor color) {
    return color.setOpacity(getOpacity(progress, grid));
  }

  @Override
  public ChartColor getHorizontalGrid(double progress, double grid, ChartColor color) {
    return color.setOpacity(getOpacity(progress, grid));
  }

  private double getOpacity(double progress, double bar) {
    return Math.min(1, Math.max(0, 2 * progress - bar));
  }
}
