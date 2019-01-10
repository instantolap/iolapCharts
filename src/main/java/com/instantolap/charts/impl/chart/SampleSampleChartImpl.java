package com.instantolap.charts.impl.chart;

import com.instantolap.charts.Content;
import com.instantolap.charts.Data;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.SampleSampleChart;
import com.instantolap.charts.impl.axis.SampleAxisImpl;
import com.instantolap.charts.impl.content.SampleSampleRenderer;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class SampleSampleChartImpl extends BasicMultiAxisChartImpl implements SampleSampleChart {

  private final SampleAxisImpl sampleAxis1 = new SampleAxisImpl(0);
  private final SampleAxisImpl sampleAxis2 = new SampleAxisImpl(1);

  public SampleSampleChartImpl(Palette palette) {
    super(palette);

    setAxes(sampleAxis1, sampleAxis2, null);
  }

  @Override
  public SampleAxis getXAxis() {
    return sampleAxis1;
  }

  @Override
  public SampleAxis getYAxis() {
    return sampleAxis2;
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
    for (Content content : getContents()) {
      if (content instanceof SampleSampleRenderer) {
        final SampleSampleRenderer sampleContent = (SampleSampleRenderer) content;
        sampleContent.render(
          progress,
          r,
          data,
          xx, yy,
          canvasWidth, canvasHeight,
          sampleAxis1, sampleAxis2,
          font,
          getBackground()
        );
      }
    }
  }
}
