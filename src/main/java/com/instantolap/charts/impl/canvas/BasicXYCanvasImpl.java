package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.XYCanvas;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public abstract class BasicXYCanvasImpl extends BasicCanvasImpl implements XYCanvas {

  private ChartColor horizontalBackground2;
  private ChartColor verticalBackground2;
  private ChartColor[] horizontalGrid = {ChartColor.LIGHT_GRAY};
  private ChartStroke horizontalGridStroke;
  private ChartColor[] verticalGrid = null;
  private ChartStroke verticalGridStroke;
  private ChartColor baseLine = null;

  @Override
  public void setHorizontalBackground2(ChartColor color) {
    this.horizontalBackground2 = color;
  }

  @Override
  public ChartColor getHorizontalBackground2() {
    return horizontalBackground2;
  }

  @Override
  public void setVerticalBackground2(ChartColor color) {
    this.verticalBackground2 = color;
  }

  @Override
  public ChartColor getVerticalBackground2() {
    return verticalBackground2;
  }

  @Override
  public void setBaseLine(ChartColor color) {
    this.baseLine = color;
  }

  @Override
  public ChartColor getBaseLine() {
    return baseLine;
  }

  @Override
  public void setVerticalGrid(ChartColor... color) {
    this.verticalGrid = color;
  }

  @Override
  public ChartColor[] getVerticalGrid() {
    return verticalGrid;
  }

  @Override
  public void setVerticalGridStroke(ChartStroke color) {
    this.verticalGridStroke = color;
  }

  @Override
  public ChartStroke getVerticalGridStroke() {
    return verticalGridStroke;
  }

  @Override
  public ChartColor[] getHorizontalGrid() {
    return horizontalGrid;
  }

  @Override
  public void setHorizontalGrid(ChartColor... color) {
    this.horizontalGrid = color;
  }

  @Override
  public ChartStroke getHorizontalGridStroke() {
    return horizontalGridStroke;
  }

  @Override
  public void setHorizontalGridStroke(ChartStroke color) {
    this.horizontalGridStroke = color;
  }
}
