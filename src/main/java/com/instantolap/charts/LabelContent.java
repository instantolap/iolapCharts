package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface LabelContent extends Content {

  String getText();

  void setText(String text);

  double getX();

  void setX(double x);

  double getY();

  void setY(double x);

  ChartFont getFont();

  void setFont(ChartFont font);

  ChartColor getColor();

  void setColor(ChartColor color);
}
