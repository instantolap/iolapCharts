package com.instantolap.charts.impl.content;

import com.instantolap.charts.Data;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public interface SampleSampleRenderer extends RendererWithSamples {
  void render(double progress, Renderer r, Data data, int x, int y,
    int width, int height, SampleAxis xAxis, SampleAxis yAxis,
    ChartFont font, ChartColor background) throws ChartException;
}
