package com.instantolap.charts.impl.data;

import com.instantolap.charts.Cube;


@SuppressWarnings("serial")
public class PartialCube extends DeligatedCube {

  private boolean[][] visibilities;

  public PartialCube(Cube cube) {
    super(cube);

    visibilities = new boolean[cube.getDimensionCount()][];
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      final int sampleCount = cube.getSampleCount(d);
      visibilities[d] = new boolean[sampleCount];
      for (int s = 0; s < sampleCount; s++) {
        visibilities[d][s] = cube.isVisible(d, s);
      }
    }
  }

  public PartialCube() {}

  public void setVisible(int dimension, int[] samples, boolean visible) {
    if (dimension >= visibilities.length) {
      return;
    }
    for (int sample : samples) {
      if (sample < visibilities[dimension].length) {
        visibilities[dimension][sample] = visible;
      }
    }
  }

  public void keepVisible(int dimension, int[] samples, boolean visible) {
    for (int n = 0; n < getSampleCount(dimension); n++) {
      if (!contains(samples, n)) {
        visibilities[dimension][n] = visible;
      }
    }
  }

  private boolean contains(int[] samples, int n) {
    for (int s : samples) {
      if (s == n) {
        return true;
      }
    }
    return false;
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
  public void setVisible(int dimension, int sample, boolean visible) {
    visibilities[dimension][sample] = visible;
  }

  @Override
  public boolean isVisible(int dimension, int sample) {
    if (!getCube().isVisible(dimension, sample)) {
      return false;
    }

    if (dimension >= visibilities.length) {
      return true;
    } else if (sample >= visibilities[dimension].length) {
      return true;
    } else {
      return visibilities[dimension][sample];
    }
  }

  private Double getMinMax(boolean isMin, String... measures) {
    int maxIndex = 1;
    for (int n = 0; n < getDimensionCount(); n++) {
      maxIndex *= getSampleCount(n);
    }

    Double max = null;
    final int[] pos = new int[getDimensionCount()];
    for (int n = 0; n < maxIndex; n++) {
      int index = n;
      boolean visible = true;
      for (int i = getDimensionCount() - 1; i >= 0; i--) {
        final int c = getSampleCount(i);
        pos[i] = index % c;
        if (!visibilities[i][pos[i]]) {
          visible = false;
          break;
        }
        index /= c;
      }
      if (visible) {
        for (String measure : measures) {
          final Double value = get(measure, pos);
          if (value != null) {
            if (max == null) {
              max = value;
            } else if (isMin) {
              max = Math.min(max, value);
            } else {
              max = Math.max(max, value);
            }
          }
        }
      }
    }
    return max;
  }
}
