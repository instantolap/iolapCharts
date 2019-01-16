package com.instantolap.charts.impl.content;

import com.instantolap.charts.BarContent;
import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;


public abstract class BasicBarContentImpl extends BasicSampleContentImpl implements BarContent {

  private double barWidth = 0.5;
  private int barSpacing = 1;
  private boolean isMultiColor = false;
  private String measure = Cube.MEASURE_VALUE;
  private String lowerMeasure = Cube.MEASURE_LOWER;
  private String minMeasure;
  private String maxMeasure;
  private ChartColor colorUp, colorDown;

  public BasicBarContentImpl(Theme theme) {
    super(theme);
  }

  @Override
  public String getMeasure() {
    return measure;
  }

  @Override
  public void setBarWidth(double width) {
    this.barWidth = width;
  }

  @Override
  public void setMeasure(String measure) {
    this.measure = measure;
  }

  @Override
  public double getBarWidth() {
    return barWidth;
  }

  @Override
  public void setBarSpacing(int spacing) {
    this.barSpacing = spacing;
  }

  @Override
  public int getBarSpacing() {
    return barSpacing;
  }

  @Override
  public void setMultiColor(boolean isMultiColor) {
    this.isMultiColor = isMultiColor;
  }

  @Override
  public boolean isMultiColor() {
    return isMultiColor;
  }


  @Override
  public String getLowerMeasure() {
    return lowerMeasure;
  }

  @Override
  public void setLowerMeasure(String lowerMeasure) {
    this.lowerMeasure = lowerMeasure;
  }

  @Override
  public void setMinMeasure(String minMeasure) {
    this.minMeasure = minMeasure;
  }

  @Override
  public String getMinMeasure() {
    return minMeasure;
  }

  @Override
  public void setMaxMeasure(String maxMeasure) {
    this.maxMeasure = maxMeasure;
  }

  @Override
  public String getMaxMeasure() {
    return maxMeasure;
  }

  @Override
  public void setColorUp(ChartColor color) {
    this.colorUp = color;
  }

  @Override
  public ChartColor getColorUp() {
    return colorUp;
  }

  @Override
  public void setColorDown(ChartColor color) {
    this.colorDown = color;
  }

  @Override
  public ChartColor getColorDown() {
    return colorDown;
  }
}
