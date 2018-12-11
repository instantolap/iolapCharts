package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.ScatterContent;
import com.instantolap.charts.renderer.ChartColor;


public abstract class BasicScatterContentImpl extends BasicSampleContentImpl
  implements ScatterContent
{
  private boolean isBubble;
  private String measure = Cube.MEASURE_VALUE;
  private String xMeasure = Cube.MEASURE_X;
  private String yMeasure = Cube.MEASURE_Y;

  public BasicScatterContentImpl() {
    setOutline(ChartColor.BLACK);
  }

  @Override
  public boolean isBubble() {
    return isBubble;
  }

  @Override
  public void setBubble(boolean isBubble) {
    this.isBubble = isBubble;
  }

  @Override
  public String getXMeasure() {
    return xMeasure;
  }

  @Override
  public String getMeasure() {
    return measure;
  }

  @Override
  public void setXMeasure(String measure) {
    this.xMeasure = measure;
  }

  @Override
  public void setMeasure(String measure) {
    this.measure = measure;
  }

  @Override
  public String getYMeasure() {
    return yMeasure;
  }

  @Override
  public void setYMeasure(String measure) {
    this.yMeasure = measure;
  }




}
