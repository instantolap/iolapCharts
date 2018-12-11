package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;

import java.util.ArrayList;
import java.util.List;


public class CombinedCanvasAnim implements CanvasAnimation {

  private final List<CanvasAnimation> animations = new ArrayList<>();

  public void addAnimation(CanvasAnimation anim) {
    animations.add(anim);
  }

  @Override
  public ChartColor getBackground(double progress, ChartColor color) {
    for (CanvasAnimation a : animations) {
      color = a.getBackground(progress, color);
    }
    return color;
  }

  @Override
  public ChartColor getBorder(double progress, ChartColor color) {
    for (CanvasAnimation a : animations) {
      color = a.getBorder(progress, color);
    }
    return color;
  }

  @Override
  public ChartColor getVerticalBackground(double progress, double grid, ChartColor color) {
    for (CanvasAnimation a : animations) {
      color = a.getVerticalBackground(progress, grid, color);
    }
    return color;
  }

  @Override
  public ChartColor getHorizontalBackground(double progress, double grid, ChartColor color) {
    for (CanvasAnimation a : animations) {
      color = a.getHorizontalBackground(progress, grid, color);
    }
    return color;
  }

  @Override
  public ChartColor getVerticalGrid(double progress, double grid, ChartColor color) {
    for (CanvasAnimation a : animations) {
      color = a.getVerticalGrid(progress, grid, color);
    }
    return color;
  }

  @Override
  public ChartColor getHorizontalGrid(double progress, double grid, ChartColor color) {
    for (CanvasAnimation a : animations) {
      color = a.getHorizontalGrid(progress, grid, color);
    }
    return color;
  }
}
