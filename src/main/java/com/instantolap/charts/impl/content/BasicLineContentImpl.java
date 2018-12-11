package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.LineContent;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.util.ArrayHelper;
import com.instantolap.charts.renderer.ChartColor;


public abstract class BasicLineContentImpl extends BasicSampleContentImpl implements LineContent {
  private final String lowerMeasure = Cube.MEASURE_LOWER;
  private boolean isAreaChart = false;
  private double areaOpacity = 0.5;
  private boolean isConnected = false;
  private boolean isInterpolated = false;
  private boolean isStepLine = false;
  private ChartColor[] fillColors = new ChartColor[0];
  private Integer[] fillStarts = new Integer[0];
  private String measure = Cube.MEASURE_VALUE;

  public BasicLineContentImpl() {
    setOutline(ChartColor.WHITE);
  }

  @Override
  public String getMeasure() {
    return measure;
  }

  @Override
  public void setAreaChart(boolean area) {
    this.isAreaChart = area;
  }

  @Override
  public void setMeasure(String measure) {
    this.measure = measure;
  }

  @Override
  public boolean isAreaChart() {
    return isAreaChart;
  }

  public String getLowerMeasure() {
    return lowerMeasure;
  }

  @Override
  public void setAreaOpacity(double areaOpacity) {
    this.areaOpacity = areaOpacity;
  }

  protected ChartColor getAreaColor(
    double progress, double bar,
    ContentAnimation anim,
    Data data,
    int colorRange,
    int series,
    int sample)
  {
    ChartColor areaColor = getFillColor(series);
    if (areaColor == null) {
      if (!isAreaChart) {
        return null;
      }
      areaColor = data.getColor(colorRange, series);
    }
    areaColor = anim.getSampleColor(progress, bar, areaColor);
    areaColor = changeSelectedColor(areaColor, data, 1, series);
    return areaColor;
  }

  @Override
  public double getAreaOpacity() {
    return areaOpacity;
  }

  @Override
  public void setConnected(boolean connected) {
    this.isConnected = connected;
  }

  @Override
  public boolean isConnected() {
    return isConnected;
  }

  @Override
  public void setInterpolated(boolean interpolated) {
    this.isInterpolated = interpolated;
  }

  @Override
  public boolean isInterpolated() {
    return isInterpolated;
  }

  @Override
  public void setStepLine(boolean stepLine) {
    this.isStepLine = stepLine;
  }

  @Override
  public boolean isStepLine() {
    return isStepLine;
  }

  @Override
  public void setFillColor(int start, int end, ChartColor colors) {
    fillColors = ArrayHelper.add(fillColors, end, colors);
    fillStarts = ArrayHelper.add(fillStarts, end, start);
  }

  @Override
  public ChartColor getFillColor(int series) {
    if (series >= fillColors.length) {
      return null;
    } else {
      return fillColors[series];
    }
  }

  @Override
  public Integer getFillStart(int series) {
    if (series >= fillStarts.length) {
      return null;
    } else {
      return fillStarts[series];
    }
  }


}
