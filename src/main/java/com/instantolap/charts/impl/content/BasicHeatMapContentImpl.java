package com.instantolap.charts.impl.content;

import com.instantolap.charts.HeatMapContent;
import com.instantolap.charts.renderer.ChartColor;


public abstract class BasicHeatMapContentImpl extends BasicSampleContentImpl
  implements HeatMapContent
{
  private HeatColor[] heatColors;
  private ChartColor[][] cellColors;
  private boolean symbolAutoColor = false;
  private boolean showSymbols = false;
  private boolean symbolAutoSize = true;
  private int minTickSize;
  private boolean fill;
  private int fillPadding = 0;
  private String format;

  public BasicHeatMapContentImpl() {
  }

  @Override
  public void setHeatColors(HeatColor[] heatColors) {
    this.heatColors = heatColors;
  }

  @Override
  public HeatColor[] getHeatColors() {
    return heatColors;
  }

  @Override
  public void setCellColors(ChartColor[][] cellColors) {
    this.cellColors = cellColors;
  }

  @Override
  public ChartColor[][] getCellColors() {
    return cellColors;
  }

  @Override
  public void setSymbolAutoColor(boolean autoColor) {
    this.symbolAutoColor = autoColor;
  }

  @Override
  public boolean isSymbolAutoColor() {
    return symbolAutoColor;
  }

  @Override
  public void setShowSymbols(boolean showSymbols) {
    this.showSymbols = showSymbols;
  }

  @Override
  public boolean isShowSymbols() {
    return showSymbols;
  }

  @Override
  public void setSymbolAutoSize(boolean symbolAutoSize) {
    this.symbolAutoSize = symbolAutoSize;
  }

  @Override
  public boolean isSymbolAutoSize() {
    return symbolAutoSize;
  }

  @Override
  public void setMinTickSize(int minTickSize) {
    this.minTickSize = minTickSize;
  }

  @Override
  public int getMinTickSize() {
    return minTickSize;
  }

  @Override
  public void setFill(boolean fill) {
    this.fill = fill;
  }

  @Override
  public boolean isFill() {
    return fill;
  }

  @Override
  public int getFillPadding() {
    return fillPadding;
  }

  @Override
  public void setFillPadding(int fillPadding) {
    this.fillPadding = fillPadding;
  }

  @Override
  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public String getFormat() {
    return format;
  }
}
