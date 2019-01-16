package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface HasTitle {

  String getTitle();

  void setTitle(String title);

  ChartColor getTitleColor();

  void setTitleColor(ChartColor color);

  ChartFont getTitleFont();

  void setTitleFont(ChartFont font);

  double getTitlePadding();

  void setTitlePadding(double padding);
}
