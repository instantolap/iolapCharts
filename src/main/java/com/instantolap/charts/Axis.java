package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface Axis extends HasTitle, HasBackground, HasSamples {

  boolean isVisible();

  void setVisible(boolean visible);

  double getTitleRotation();

  void setTitleRotation(double rotation);

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

  double getLabelRotation();

  void setLabelRotation(double rotation);

  double getLabelSpacing();

  void setLabelSpacing(double spacing);

  boolean isAutoSpacingOn();

  void setAutoSpacingOn(boolean autoSpacing);

  double getTickWidth();

  void setTickWidth(double tickWidth);

  String getPrefix();

  void setPrefix(String prefix);

  String getPostfix();

  void setPostfix(String postfix);

  boolean isShowLabelsInside();

  void setShowLabelsInside(boolean inside);

  double[] getGrid();

  double[] getCenteredGrid();

  double[] getGridLines();

  boolean[] getVisibleGrid();

  void setVisibleGrid(boolean[] visible);

  String[] getTexts();

  double getNeededSize();

  double getSize();

  boolean isVertical();

  boolean isShowGrid();

  void setShowGrid(boolean show);
}
