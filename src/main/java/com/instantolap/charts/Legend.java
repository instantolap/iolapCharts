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

  int getPadding();

  void setPadding(int padding);

  int getSpacing();

  void setSpacing(int spacing);

  boolean isVertical();

  void setVertical(boolean vertical);

  int getDimension();

  void setDimension(int dimension);

  String[] getLabels();

  void setLabels(String[] labes);

  boolean isReverse();

  void setReverse(boolean reverse);
}
