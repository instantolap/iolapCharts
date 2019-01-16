package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public interface HasBorder extends HasShadow {

  ChartColor getBorder();

  void setBorder(ChartColor border);

  ChartStroke getBorderStroke();

  void setBorderStroke(ChartStroke border);

  double getRoundedCorner();

  void setRoundedCorner(double arc);
}