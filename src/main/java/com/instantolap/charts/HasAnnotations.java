package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface HasAnnotations {

  void addAnnotation(
    Double x,
    Double y,
    Integer pos,
    Integer series,
    ChartColor foreground,
    ChartColor background,
    ChartFont font,
    String text);

}
