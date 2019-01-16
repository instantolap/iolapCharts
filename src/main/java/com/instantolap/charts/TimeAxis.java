package com.instantolap.charts;

public interface TimeAxis extends ScaleAxis, PositionAxis {

  double getMinSampleWidth();

  void setMinSampleWidth(double min);

  double getMaxSampleWidth();

  void setMaxSampleWidth(double max);

  long getSampleMilliseconds();

  void setSampleMilliseconds(long ms);
}
