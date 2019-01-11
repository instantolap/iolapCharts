package com.instantolap.charts.impl.data;

import com.instantolap.charts.Cube;


@SuppressWarnings("serial")
public class ReducedCube extends DelegatedCube {

  private Cube cube;
  private boolean[][] indices;
  private int[] sampleCounts;

  public ReducedCube() {
  }

  public ReducedCube(Cube cube) {
    super(cube);
    this.cube = cube;

    indices = new boolean[cube.getDimensionCount()][];
    sampleCounts = new int[cube.getDimensionCount()];
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      final int count = cube.getSampleCount(d);
      sampleCounts[d] = count;
      indices[d] = new boolean[count];
      for (int s = 0; s < count; s++) {
        indices[d][s] = true;
      }
    }
  }

  public void removeSamples(int d, int[] samples) {
    if (d >= sampleCounts.length) {
      return;
    }
    for (int s : samples) {
      indices[d][s] = false;
      sampleCounts[d]--;
    }
  }

  public void removeAllSamples(int d) {
    if (d >= sampleCounts.length) {
      return;
    }
    for (int n = 0; n < indices[d].length; n++) {
      indices[d][n] = false;
    }
    sampleCounts[d] = 0;
  }

  public void keepSamples(int d, int[] samples) {
    if (d >= sampleCounts.length) {
      return;
    }
    int last = getSampleCount(d);
    for (int n = samples.length - 1; n >= 0; n--) {
      final int s = samples[n];
      for (int l = last - 1; l > s; l--) {
        removeSample(d, l);
      }
      last = s;
    }
    for (int l = last - 1; l >= 0; l--) {
      removeSample(d, l);
    }
  }

  @Override
  public int getSampleCount(int dimension) {
    return sampleCounts[dimension];
  }

  public void removeSample(int d, int sample) {
    if (d >= sampleCounts.length) {
      return;
    }
    indices[d][sample] = false;
    sampleCounts[d]--;
  }

  @Override
  public String getSample(int dimension, int pos) {
    return cube.getSample(dimension, transform(dimension, pos));
  }

  @Override
  public Double get(String measure, int pos) {
    pos = transform(0, pos);
    return cube.get(measure, pos);
  }

  @Override
  public Double get(String measure, int pos1, int pos2) {
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
  public Double getMin(String... measures) {
    return getMinMax(true, measures);
  }

  @Override
  public Double getMax(String... measures) {
    return getMinMax(false, measures);
  }

  private Double getMinMax(boolean isMin, String... measures) {
    int maxIndex = 1;
    for (int c : sampleCounts) {
      maxIndex *= c;
    }

    Double result = null;
    final int[] pos = new int[getDimensionCount()];
    for (int n = 0; n < maxIndex; n++) {
      int index = n;
      for (int i = 0; i < getDimensionCount(); i++) {
        final int c = getSampleCount(i);
        pos[i] = index % c;
        index /= c;
      }
      for (String measure : measures) {
        final Double value = get(measure, pos);
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

  private int[] transform(int[] pos) {
    final int[] newPos = new int[pos.length];
    for (int n = 0; n < pos.length; n++) {
      newPos[n] = transform(n, pos[n]);
    }
    return newPos;
  }

  private int transform(int dimension, int pos) {
    int dim = 0;
    for (int n = 0; n < indices[dimension].length; n++) {
      if (indices[dimension][n]) {
        if (pos == dim++) {
          return n;
        }
      }
    }
    return 0;
  }

  @Override
  public boolean isVisible(int dimension, int sample) {
    return cube.isVisible(dimension, transform(dimension, sample));
  }
}
