package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.XYCanvas;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public abstract class BasicXYCanvasImpl extends BasicCanvasImpl implements XYCanvas {

  private ChartColor horizontalBackground2;
  private ChartColor verticalBackground2;
  private ChartColor[] horizontalGrid;
  private ChartStroke horizontalGridStroke;
  private ChartColor[] verticalGrid;
  private ChartStroke verticalGridStroke;
  private ChartColor baseLine;

  public BasicXYCanvasImpl(Theme theme) {
    super(theme);
  }

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
    return baseLine != null ? baseLine : getTheme().getBaseLine();
  }

  @Override
  public void setVerticalGrid(ChartColor... color) {
    this.verticalGrid = color;
  }

  @Override
  public ChartColor[] getVerticalGrid() {
    return verticalGrid != null ? verticalGrid : getTheme().getVerticalGrid();
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
    return horizontalGrid != null ? horizontalGrid : getTheme().getHorizontalGrid();
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
