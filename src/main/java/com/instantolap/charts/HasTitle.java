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

  int getTitlePadding();

  void setTitlePadding(int padding);
}
