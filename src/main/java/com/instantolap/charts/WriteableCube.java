package com.instantolap.charts;

public interface WriteableCube extends Cube {

  void setDimensionSize(int axis, int size);

  void addSample(int axis, String sample);

  void addSample(int axis, String sample, String id);

  void set(String measure, Double v, int pos1);

  void set(String measure, Double v, int pos1, int pos2);

  void set(String measure, Double v, int[] pos);

  void set(String measure, String v, int... pos);
}