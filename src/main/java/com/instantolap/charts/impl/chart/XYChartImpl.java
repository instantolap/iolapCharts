package com.instantolap.charts.impl.chart;

import com.instantolap.charts.Content;
import com.instantolap.charts.Data;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.XYChart;
import com.instantolap.charts.impl.axis.ValueAxisImpl;
import com.instantolap.charts.impl.content.ValueValueRenderer;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class XYChartImpl extends BasicMultiAxisChartImpl implements XYChart {

  private final ValueAxisImpl xAxis = new ValueAxisImpl();
  private final ValueAxisImpl yAxis = new ValueAxisImpl();

  public XYChartImpl(Palette palette) {
    super(palette);

    setAxes(xAxis, yAxis, null);
    xAxis.setTitleRotation(0);
    yAxis.setTitleRotation(270);
  }

  @Override
  public ValueAxis getXAxis() {
    return xAxis;
  }

  @Override
  public ValueAxis getYAxis() {
    return yAxis;
  }

  @Override
  protected void buildCubes() {
    super.buildCubes();

    // assign measures to axes
    xAxis.clearMeasures();
    yAxis.clearMeasures();
    for (Content content : getContents()) {
      if (content instanceof ValueValueRenderer) {
        final ValueValueRenderer valueContent = (ValueValueRenderer) content;
        valueContent.addMeasuresToAxes(xAxis, yAxis);
      }
    }
  }

  @Override
  protected void renderContent(double progress, Data data, Renderer r,
    int canvasWidth, int canvasHeight, ChartFont font, int xx, int yy)
    throws ChartException
  {
    for (Content content : getContents()) {
      if (content instanceof ValueValueRenderer) {
        final ValueValueRenderer sampleContent = (ValueValueRenderer) content;
        sampleContent.render(
          progress,
          r,
          data,
          xx, yy,
          canvasWidth, canvasHeight,
          xAxis, yAxis,
          font,
          getBackground()
        );
      }
    }
  }
}
