package com.instantolap.charts.impl.data;

import com.instantolap.charts.Cube;


@SuppressWarnings("serial")
public class DelegatedCube extends BasicCube {

  protected Cube cube;

  public DelegatedCube() {
  }

  public DelegatedCube(Cube cube) {
    this.cube = cube;
  }

  public Cube getCube() {
    return cube;
  }

  @Override
  public int getDimensionCount() {
    return cube.getDimensionCount();
  }

  @Override
  public int getSampleCount(int dimension) {
    return cube.getSampleCount(dimension);
  }

  @Override
  public String getSample(int dimension, int pos) {
    return cube.getSample(dimension, pos);
  }

  @Override
  public String getSampleID(int dimension, int pos) {
    return cube.getSampleID(dimension, pos);
  }

  @Override
  public Double get(String measure, int pos1) {
    return cube.get(measure, pos1);
  }

  @Override
  public Double get(String measure, int pos1, int pos2) {
    return cube.get(measure, pos1, pos2);
  }

  @Override
  public Double get(String measure, int[] pos) {
    return cube.get(measure, pos);
  }

  @Override
  public String getString(String measure, int... pos) {
    return cube.getString(measure, pos);
  }

  @Override
  public Double getMin(String... measures) {
    return cube.getMin(measures);
  }

  @Override
  public Double getMax(String... measures) {
    return cube.getMax(measures);
  }

  @Override
  public void setVisible(int dimension, int sample, boolean visible) {
    cube.setVisible(dimension, sample, visible);
  }

  @Override
  public boolean isVisible(int dimension, int sample) {
    return cube.isVisible(dimension, sample);
  }
}
