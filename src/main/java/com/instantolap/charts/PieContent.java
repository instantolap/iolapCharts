package com.instantolap.charts;

import java.util.List;


public interface PieContent extends SampleContent, HasValueLabels, HasMeasure {

  double getSeriesSpace();

  void setSeriesSpace(double space);

  void addDetachedSample(int sample);

  List<Integer> getDetachedSamples();

  double getDetachedDistance();

  void setDetachedDistance(double distance);

  double getStartAngle();

  void setStartAngle(double angle);

  boolean isRound();

  void setRound(boolean round);
}