package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.MeterContent;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;


public abstract class BasicMeterContentImpl extends BasicSampleContentImpl implements MeterContent {

  private String measure = Cube.MEASURE_VALUE;
  private double pinSize = 0.1;
  private ChartColor pinColor = null;

  public BasicMeterContentImpl(Theme theme) {
    super(theme);

    setOutline(ChartColor.BLACK);
  }

  @Override
  public String getMeasure() {
    return measure;
  }

  @Override
  public void setMeasure(String measure) {
    this.measure = measure;
  }

  public String[] getMeasures() {
    return new String[]{measure};
  }

  @Override
  public void setPinSize(double pinSize) {
    this.pinSize = pinSize;
  }

  @Override
  public double getPinSize() {
    return pinSize;
  }

  @Override
  public void setPinColor(ChartColor pinColor) {
    this.pinColor = pinColor;
  }

  @Override
  public ChartColor getPinColor() {
    return pinColor;
  }
}
