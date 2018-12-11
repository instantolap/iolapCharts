package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public interface CanvasAnimation extends Animation {

  ChartColor getBackground(double progress, ChartColor color);

  ChartColor getBorder(double progress, ChartColor border);

  ChartColor getVerticalBackground(double progress, double grid, ChartColor color);

  ChartColor getHorizontalBackground(double progress, double grid, ChartColor color);

  ChartColor getVerticalGrid(double progress, double grid, ChartColor color);

  ChartColor getHorizontalGrid(double progress, double grid, ChartColor color);
}
