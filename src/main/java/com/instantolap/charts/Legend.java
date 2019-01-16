package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface Legend extends HasBackground, HasShadow, HasBorder {

  ChartFont getFont();

  void setFont(ChartFont font);

  ChartColor getColor();

  void setColor(ChartColor color);

  ChartColor[] getColors();

  void setColors(ChartColor[] colors);

  double getPadding();

  void setPadding(double padding);

  double getSpacing();

  void setSpacing(double spacing);

  boolean isVertical();

  void setVertical(boolean vertical);

  int getDimension();

  void setDimension(int dimension);

  String[] getLabels();

  void setLabels(String[] labes);

  boolean isReverse();

  void setReverse(boolean reverse);
}
