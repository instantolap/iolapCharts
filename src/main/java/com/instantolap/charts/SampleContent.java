package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;


public interface SampleContent extends Content, HasShadow, HasAnnotations, HasSampleColors {

  ChartColor getOutline();

  void setOutline(ChartColor color);

  double getShine();

  void setShine(double shine);
}
