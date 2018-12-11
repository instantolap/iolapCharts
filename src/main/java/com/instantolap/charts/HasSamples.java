package com.instantolap.charts;

public interface HasSamples {

  void addDisplaySample(int dimension, int sample);

  int[] getDisplaySamples(int dimension);

}
