package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface Axis extends HasTitle, HasBackground, HasSamples {

  boolean isVisible();

  void setVisible(boolean visible);

  int getTitleRotation();

  void setTitleRotation(int rotation);

  ChartColor getLineColor();

  void setLineColor(ChartColor color);

  boolean isShowBaseLine();

  void setShowBaseLine(boolean show);

  boolean isShowLabels();

  void setShowLabels(boolean show);

  ChartColor getColor();

  void setColor(ChartColor color);

  ChartColor[] getColors();

  void setColors(ChartColor[] colors);

  ChartFont getFont();

  void setFont(ChartFont font);

  int getLabelRotation();

  void setLabelRotation(int rotation);

  int getLabelSpacing();

  void setLabelSpacing(int spacing);

  boolean isAutoSpacingOn();

  void setAutoSpacingOn(boolean autoSpacing);

  int getTickWidth();

  void setTickWidth(int tickWidth);

  String getPrefix();

  void setPrefix(String prefix);

  String getPostfix();

  void setPostfix(String postfix);

  boolean isShowLabelsInside();

  void setShowLabelsInside(boolean inside);

  int[] getGrid();

  int[] getCenteredGrid();

  int[] getGridLines();

  boolean[] getVisibleGrid();

  void setVisibleGrid(boolean[] visible);

  String[] getTexts();

  int getNeededSize();

  int getSize();

  boolean isVertical();

  boolean isShowGrid();

  void setShowGrid(boolean show);
}
