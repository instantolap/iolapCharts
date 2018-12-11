package com.instantolap.charts;

import java.io.Serializable;


public interface Cube extends Serializable {

  String MEASURE_VALUE = "value";
  String MEASURE_LOWER = "lower";
  String MEASURE_LINK = "link";
  String MEASURE_TARGET = "target";
  String MEASURE_X = "x";
  String MEASURE_Y = "y";
  String MEASURE_TIME = "time";

  // dimensions

  int getDimensionCount();

  int getSampleCount(int dimension);

  int getVisibleSampleCount(int dimension);

  String getSample(int dimension, int pos);

  String getSampleID(int dimension, int pos);

  // values

  Double get(String measure, int pos);

  Double get(String measure, int pos1, int pos2);

  Double get(String measure, int[] pos);

  String getString(String measure, int... pos);

  Double getMin(String... measures);

  Double getMax(String... measures);

  // visibility

  void setVisible(int dimension, int sample, boolean visible);

  boolean isVisible(int dimension, int sample);

  // listeners

  void addListener(CubeListener listener);

  void removeListener(CubeListener listener);

}