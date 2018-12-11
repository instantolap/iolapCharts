package com.instantolap.charts.impl.data;

import com.instantolap.charts.Cube;


@SuppressWarnings("serial")
public class SlicedCube extends DeligatedCube {

  private int[] starts;
  private int[] ends;

  public SlicedCube(Cube cube) {
    super(cube);

    starts = new int[cube.getDimensionCount()];
    ends = new int[cube.getDimensionCount()];
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      starts[d] = 0;
      ends[d] = cube.getSampleCount(d);
    }
  }

  public SlicedCube() {}

  public void setDimensionStart(int d, int start) {
    starts[d] = start;
  }

  public void setDimensionEnd(int d, int end) {
    ends[d] = end;
  }

  @Override
  public Double getMax(String... measures) {
    return getMinMax(false, measures);
  }

  @Override
  public Double getMin(String... measures) {
    return getMinMax(true, measures);
  }

  @Override
  public Double get(String measure, int pos) {
    pos = transform(0, pos);
    return cube.get(measure, pos);
  }

  @Override
  public Double get(String measure, int pos1, int pos2) {
    if (starts.length < 2) {
      return get(measure, pos1);
    }

    pos1 = transform(0, pos1);
    pos2 = transform(1, pos2);
    return cube.get(measure, pos1, pos2);
  }

  @Override
  public Double get(String measure, int[] pos) {
    pos = transform(pos);
    return cube.get(measure, pos);
  }

  @Override
  public String getSample(int dimension, int pos) {
    return cube.getSample(dimension, transform(dimension, pos));
  }

  @Override
  public int getSampleCount(int dimension) {
    if (dimension >= ends.length) {
      return 0;
    }
    return ends[dimension] - starts[dimension];
  }

  @Override
  public boolean isVisible(int dimension, int sample) {
    if (dimension >= ends.length) {
      return true;
    }
    return cube.isVisible(dimension, transform(dimension, sample));
  }

  private int[] transform(int[] pos) {
    final int[] newPos = new int[pos.length];
    for (int n = 0; n < pos.length; n++) {
      newPos[n] = pos[n] + starts[n];
    }
    return newPos;
  }

  private int transform(int dimension, int pos) {
    return pos + starts[dimension];
  }

  private Double getMinMax(boolean isMin, String... measures) {
    int maxIndex = 1;
    for (int d = 0; d < getDimensionCount(); d++) {
      maxIndex *= getSampleCount(d);
    }

    Double result = null;
    final int[] pos = new int[getDimensionCount()];
    for (int n = 0; n < maxIndex; n++) {
      int index = n;
      for (int i = 0; i < getDimensionCount(); i++) {
        final int c = getSampleCount(i);
        pos[i] = index % c + starts[i];
        index /= c;
      }
      for (String measure : measures) {
        final Double value = cube.get(measure, pos);
        if (value != null) {
          if (result == null) {
            result = value;
          } else if (isMin) {
            result = Math.min(result, value);
          } else {
            result = Math.max(result, value);
          }
        }
      }
    }
    return result;
  }
}
