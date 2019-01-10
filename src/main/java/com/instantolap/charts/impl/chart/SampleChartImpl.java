package com.instantolap.charts.impl.chart;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.axis.SampleAxisImpl;
import com.instantolap.charts.impl.axis.ValueAxisImpl;
import com.instantolap.charts.impl.content.SampleValueRenderer;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class SampleChartImpl extends BasicMultiAxisChartImpl implements SampleChart {

  private final ValueAxisImpl valueAxis1 = new ValueAxisImpl();
  private final ValueAxisImpl valueAxis2 = new ValueAxisImpl();
  private final SampleAxisImpl sampleAxis = new SampleAxisImpl(0);

  public SampleChartImpl(Palette palette) {
    super(palette);

    setAxes(sampleAxis, valueAxis1, valueAxis2);
  }

  @Override
  public ValueAxis getScaleAxis() {
    return valueAxis1;
  }

  @Override
  public ValueAxis getScaleAxis2() {
    return valueAxis2;
  }

  @Override
  public SampleAxis getSampleAxis() {
    return sampleAxis;
  }

  @Override
  protected void buildCubes() {
    super.buildCubes();

    // assign measures to axes
    valueAxis1.clearMeasures();
    valueAxis2.clearMeasures();
    for (Content content : getContents()) {
      if (content instanceof SampleValueRenderer) {
        final SampleValueRenderer sampleContent = (SampleValueRenderer) content;
        final Axis axis = getAxis(content);
        sampleContent.addMeasuresToAxes((ValueAxis) axis);
      }
    }
  }

  @Override
  protected void renderContent(
    double progress,
    Data data,
    Renderer r,
    int canvasWidth, int canvasHeight,
    ChartFont font,
    int xx, int yy)
    throws ChartException
  {
    final boolean isStacked = isStacked();
    final boolean isRotated = isRotated();
    final boolean isCentered = needsCenteredSampleAxis();

    for (Content content : getContents()) {

      // which axis to use?
      final ValueAxis valueAxis = (ValueAxis) getAxis(content);

      // render content
      if (content instanceof SampleValueRenderer) {
        final SampleValueRenderer sampleContent = (SampleValueRenderer) content;
        sampleContent.render(
          progress,
          r,
          data,
          xx, yy,
          canvasWidth, canvasHeight,
          sampleAxis, valueAxis,
          isStacked,
          isCentered, isRotated,
          font,
          getBackground()
        );
      }
    }
  }
}
