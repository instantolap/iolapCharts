package com.instantolap.charts.impl.content;

import com.instantolap.charts.Data;
import com.instantolap.charts.ScaleAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public interface ValueValueRenderer {
  void render(double progress, Renderer r, Data data, int x, int y,
    int width, int height, ScaleAxis xAxis, ValueAxis yAxis,
    ChartFont font, ChartColor background) throws ChartException;

  void addMeasuresToAxes(ScaleAxis xAxis, ScaleAxis yAxis);
}
