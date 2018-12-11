package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Axis;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public interface AxisRenderer {

  void render(
    Renderer r,
    Axis axis,
    int x, int y,
    int width, int height,
    int radius,
    boolean isCentered,
    boolean flip,
    ChartFont font);

}