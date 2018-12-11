package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface LabelContent extends Content {

  String getText();

  void setText(String text);

  int getX();

  void setX(int x);

  int getY();

  void setY(int x);

  ChartFont getFont();

  void setFont(ChartFont font);

  ChartColor getColor();

  void setColor(ChartColor color);
}
