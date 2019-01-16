package com.instantolap.charts.impl.content;

import com.instantolap.charts.Data;
import com.instantolap.charts.PositionAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public interface SampleValueRenderer extends RendererWithSamples {
  void render(double progress, Renderer r, Data data, double x, double y,
              double width, double height, PositionAxis xAxis, ValueAxis yAxis,
    boolean isStacked, boolean isCentered, boolean isRotated,
    ChartFont font, ChartColor background) throws ChartException;

  void addMeasuresToAxes(ValueAxis axis);
}
