package com.instantolap.charts.impl.content;

import com.instantolap.charts.Data;
import com.instantolap.charts.PositionAxis;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.ScaleAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class LabelContentImpl extends BasicLabelContentImpl
  implements SampleValueRenderer, SampleSampleRenderer, ValueValueRenderer
{

  public LabelContentImpl() {
    setColor(ChartColor.BLACK);
    setFont(ChartFont.DEFAULT_FONT);
  }

  @Override
  public void render(
    double progress,
    Renderer r,
    Data data,
    int x, int y,
    int width, int height,
    PositionAxis xAxis, ValueAxis yAxis,
    boolean isStacked, boolean isCentered, boolean isRotated,
    ChartFont font, ChartColor background) throws ChartException
  {
    render(r, x, y);
  }

  private void render(Renderer r, int x, int y) {
    if (getText() != null) {
      r.setFont(getFont());
      r.setColor(getColor());
      r.drawText(x + getX(), y + getY(), getText(), 0, Renderer.WEST);
    }
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
  }

  @Override
  public void render(
    double progress,
    Renderer r,
    Data data,
    int x, int y,
    int width, int height,
    SampleAxis xAxis, SampleAxis yAxis,
    ChartFont font, ChartColor background) throws ChartException
  {
    render(r, x, y);
  }

  @Override
  public void render(
    double progress,
    Renderer r,
    Data data,
    int x, int y,
    int width, int height,
    ScaleAxis xAxis, ValueAxis yAxis,
    ChartFont font, ChartColor background) throws ChartException
  {
    render(r, x, y);
  }

  @Override
  public void addMeasuresToAxes(ScaleAxis xAxis, ScaleAxis yAxis) {
  }

  @Override
  public boolean needsSampleLegend() {
    return false;
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return false;
  }

}
