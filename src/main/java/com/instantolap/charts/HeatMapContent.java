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

  double getMinTickSize();

  void setMinTickSize(double minSize);

  boolean isSymbolAutoColor();

  void setSymbolAutoColor(boolean auto);

  boolean isFill();

  void setFill(boolean fill);

  double getFillPadding();

  void setFillPadding(double padding);


  class HeatColor {
    public double start, end;
    public ChartColor startColor, endColor;
  }
}