package com.instantolap.charts.impl.chart;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.axis.TimeAxisImpl;
import com.instantolap.charts.impl.axis.ValueAxisImpl;
import com.instantolap.charts.impl.content.SampleValueRenderer;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.*;


public class TimeChartImpl extends BasicMultiAxisChartImpl implements TimeChart {

  private final ValueAxisImpl valueAxis1;
  private final ValueAxisImpl valueAxis2;
  private final TimeAxisImpl timeAxis;

  public TimeChartImpl(Theme theme) {
    super(theme);

    timeAxis = new TimeAxisImpl(theme);
    timeAxis.setLabelRotation(270);

    valueAxis1 = new ValueAxisImpl(theme);
    valueAxis1.setUseZeroAsBase(false);
    valueAxis1.enableZoom(false);

    valueAxis2 = new ValueAxisImpl(theme);
    valueAxis2.setUseZeroAsBase(false);
    valueAxis2.enableZoom(false);

    setAxes(timeAxis, valueAxis1, valueAxis2);
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
  public TimeAxis getTimeAxis() {
    return timeAxis;
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
  protected void renderContent(double progress, Data data, final Renderer r,
                               final double canvasWidth, final double canvasHeight, ChartFont font,
                               final double xx, final double yy) throws ChartException {

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
          timeAxis, valueAxis,
          isStacked(),
          false,
          isRotated(),
          font,
          getBackground()
        );
      }
    }

    // add mouse listener for zoom
    if (timeAxis.isZoomEnabled()) {
      r.addMouseListener(
        xx, yy,
        canvasWidth, canvasHeight,
        (ChartMouseWheelListener) (x, y, v) -> timeAxis.doZoom(r, v, x, xx, canvasWidth)
      );
    }
    if (timeAxis.isZoomEnabled() || valueAxis1.isZoomEnabled()) {
      r.addMouseListener(
        xx, yy,
        canvasWidth, canvasHeight,
        (ChartMouseDragListener) (dx, dy) -> {
          if (valueAxis1.isZoomEnabled()) {
            valueAxis1.doDrag(r, dx, dy);
          }
          if (timeAxis.isZoomEnabled()) {
            timeAxis.doDrag(r, dx, dy);
          }
        }
      );
    }
  }
}
