package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface HeatMapContent extends SampleContent, HasValueLabels {

  HeatColor[] getHeatColors();

  void setHeatColors(HeatColor[] colors);

  ChartColor[][] getCellColors();

  void setCellColors(ChartColor[][] colors);

  boolean isShowSymbols();

  void setShowSymbols(boolean show);

  String getFormat();

  void setFormat(String format);

  boolean isSymbolAutoSize();

  void setSymbolAutoSize(boolean draw);

  int getMinTickSize();

  void setMinTickSize(int minSize);

  boolean isSymbolAutoColor();

  void setSymbolAutoColor(boolean auto);

  boolean isFill();

  void setFill(boolean fill);

  int getFillPadding();

  void setFillPadding(int padding);


  class HeatColor {
    public double start, end;
    public ChartColor startColor, endColor;
  }
}