package com.instantolap.charts;

public interface TimeAxis extends ScaleAxis, PositionAxis {

  int getMinSampleWidth();

  void setMinSampleWidth(int min);

  int getMaxSampleWidth();

  void setMaxSampleWidth(int max);

  long getSampleMilliseconds();

  void setSampleMilliseconds(long ms);
}
