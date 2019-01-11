package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.RoundCanvas;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public abstract class BasicRoundCanvasImpl extends BasicCanvasImpl implements RoundCanvas {

  private ChartColor grid = ChartColor.LIGHT_GRAY;
  private ChartColor scaleBackground;
  private ChartColor scaleBackground2;
  private ChartStroke gridStroke;
  private ChartColor baseLine = ChartColor.BLACK;
  private boolean round = true;

  public BasicRoundCanvasImpl(Theme theme) {
    super(theme);

    setBorder(null);
    setShadow(null);
  }

  @Override
  public ChartColor getScaleBackground() {
    return scaleBackground;
  }

  @Override
  public void setScaleBackground(ChartColor background) {
    this.scaleBackground = background;
  }

  @Override
  public ChartColor getScaleBackground2() {
    return scaleBackground2;
  }

  @Override
  public void setScaleBackground2(ChartColor background) {
    this.scaleBackground2 = background;
  }

  @Override
  public ChartColor getGrid() {
    return grid;
  }

  @Override
  public void setGrid(ChartColor color) {
    this.grid = color;
  }

  @Override
  public ChartColor getBaseLine() {
    return baseLine;
  }

  @Override
  public void setBaseLine(ChartColor color) {
    this.baseLine = color;
  }

  @Override
  public ChartStroke getGridStroke() {
    return gridStroke;
  }

  @Override
  public void setGridStroke(ChartStroke color) {
    this.gridStroke = color;
  }

  @Override
  public boolean isRound() {
    return round;
  }

  @Override
  public void setRound(boolean round) {
    this.round = round;
  }
}
