package com.instantolap.charts.impl.chart;

import com.instantolap.charts.Content;
import com.instantolap.charts.control.RoundChart;
import com.instantolap.charts.impl.content.SampleValueRenderer;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public abstract class BasicRoundChartImpl extends BasicChartImpl implements RoundChart {

  private boolean isStacked;
  private ChartColor innerBorder;
  private ChartStroke innerBorderStroke;
  private int innerPadding = 0;
  private boolean enableRotation = true;

  public BasicRoundChartImpl(Palette palette) {
    super(palette);
  }

  @Override
  public boolean isStacked() {
    return isStacked;
  }

  @Override
  public void setStacked(boolean isStacked) {
    this.isStacked = isStacked;
  }

  @Override
  public ChartColor getInnerBorder() {
    return innerBorder;
  }

  @Override
  public void setInnerBorder(ChartColor innerBorder) {
    this.innerBorder = innerBorder;
  }

  @Override
  public ChartStroke getInnerBorderStroke() {
    return innerBorderStroke;
  }

  @Override
  public void setInnerBorderStroke(ChartStroke innerBorderStroke) {
    this.innerBorderStroke = innerBorderStroke;
  }

  @Override
  public int getInnerPadding() {
    return innerPadding;
  }

  @Override
  public void setInnerPadding(int padding) {
    this.innerPadding = padding;
  }

  @Override
  public boolean getEnableRotation() {
    return enableRotation;
  }

  @Override
  public void setEnableRotation(boolean enableRotation) {
    this.enableRotation = enableRotation;
  }

  protected boolean needsCenteredSampleAxis() {
    for (Content content : getContents()) {
      if (content instanceof SampleValueRenderer) {
        final SampleValueRenderer sampleContent = (SampleValueRenderer) content;
        if (sampleContent.needsCenteredSampleAxis()) {
          return true;
        }
      }
    }
    return false;
  }
}
