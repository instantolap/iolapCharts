package com.instantolap.charts;

public interface ScatterContent extends SampleContent, HasValueLabels, HasRegression, HasMeasure {

  boolean isBubble();

  void setBubble(boolean bubble);

  String getXMeasure();

  void setXMeasure(String measure);

  String getYMeasure();

  void setYMeasure(String measure);

  long getTimeWindow();

  void setTimeWindow(long timeWindow);

  double getMinFade();

  void setMinFade(double minFade);
}