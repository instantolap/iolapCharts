package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Axis;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public interface AxisRenderer {

  void render(
    Renderer r,
    Axis axis,
    double x, double y,
    double width, double height,
    double radius,
    boolean isCentered,
    boolean flip,
    ChartFont font);

}