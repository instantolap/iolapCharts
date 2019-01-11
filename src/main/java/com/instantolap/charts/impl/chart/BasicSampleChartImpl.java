package com.instantolap.charts.impl.chart;

import com.instantolap.charts.Content;
import com.instantolap.charts.SampleContent;
import com.instantolap.charts.impl.data.Theme;


public abstract class BasicSampleChartImpl extends BasicChartImpl {

  private boolean isStacked;
  private boolean isRotated;

  public BasicSampleChartImpl(Theme theme) {
    super(theme);
  }

  public boolean isStacked() {
    return isStacked;
  }

  public void setStacked(boolean isStacked) {
    this.isStacked = isStacked;
  }

  public boolean isRotated() {
    return isRotated;
  }

  public void setRotated(boolean isRotated) {
    this.isRotated = isRotated;
  }

  @Override
  protected boolean needsSampleLegend() {
    for (Content content : getContents()) {
      if (content instanceof SampleContent) {
        final SampleContent sampleContent = (SampleContent) content;
        if (sampleContent.needsSampleLegend()) {
          return true;
        }
      }
    }
    return false;
  }
}
