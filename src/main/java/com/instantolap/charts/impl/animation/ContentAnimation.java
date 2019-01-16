package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;


public interface ContentAnimation extends Animation {

  double getValue(double progress, double bar, double value);

  ChartColor getShadowColor(double progress, double bar, ChartColor color);

  ChartColor getSampleColor(double progress, double bar, ChartColor color);

  ChartColor getOutlineColor(double progress, double bar, ChartColor barOutline);

  ChartColor getLabelColor(double progress, double bar, ChartColor labelColor);

  double getDistance(double progress, double bar, double distance);

  double getX(double progress, double x, double width, double height);

  double getY(double progress, double y, double width, double height);

}
