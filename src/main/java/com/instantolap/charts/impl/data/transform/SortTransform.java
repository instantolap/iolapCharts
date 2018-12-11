package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.CubeImpl;

import java.util.TreeSet;


public class SortTransform extends BasicTransform {

  private final int dimension;
  private final int descending;
  private int limit = Integer.MAX_VALUE;


  public SortTransform(int dimension, boolean descending, int limit) {
    this.dimension = dimension;
    this.descending = descending ? -1 : 1;
    this.limit = limit;
  }

  @Override
  public Cube transform(Cube cube) {
    final CubeImpl newCube = new CubeImpl();

    // build access key for rest
    final int dimensionCount = cube.getDimensionCount();
    final int[] otherDimensions = new int[dimensionCount - 1];
    for (int n = 0; n < otherDimensions.length; n++) {
      otherDimensions[n] = n + 1;
    }
    final int otherSize = getSize(cube, otherDimensions);

    final int[] dimensionCounts = new int[dimensionCount];
    for (int d = 0; d < dimensionCount; d++) {
      dimensionCounts[d] = cube.getSampleCount(d);
    }

    // find order
    final int[] pos = new int[dimensionCount];
    final TreeSet<IndexComparator> set = new TreeSet<>();
    for (int n = 0; n < dimensionCounts[dimension]; n++) {
      pos[dimension] = n;
      final Double value = aggregate(cube, pos, otherDimensions, otherSize);
      set.add(new IndexComparator(n, value));
    }

    dimensionCounts[dimension] = Math.min(dimensionCounts[dimension],
      limit
    );
    final int[] order = buildList(set);

    // transfer dimensions
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      final int count = dimensionCounts[d];
      for (int c0 = 0; c0 < count; c0++) {
        if (d == dimension) {
          newCube.addSample(d, cube.getSample(d, order[c0]));
        } else {
          newCube.addSample(d, cube.getSample(d, c0));
        }
      }
    }

    // transfer values
    final int size = getSize(cube);
    final int[] targetPos = new int[cube.getDimensionCount()];
    for (int n = 0; n < size; n++) {
      int p = n;
      for (int d = 0; d < pos.length; d++) {
        final int count = cube.getSampleCount(d);
        targetPos[d] = p % count;
        p /= count;
        if (d == dimension) {
          pos[d] = order[targetPos[d]];
        } else {
          pos[d] = targetPos[d];
        }
      }
      final Double value = cube.get(Cube.MEASURE_VALUE, pos);
      newCube.set(Cube.MEASURE_VALUE, value, targetPos);
    }
    return newCube;
  }

  private int[] buildList(TreeSet<IndexComparator> set) {
    final int[] order = new int[set.size()];
    int next = 0;
    for (IndexComparator c : set) {
      order[next++] = c.index;
    }
    return order;
  }


  private class IndexComparator implements Comparable<IndexComparator> {
    private final int index;
    private final Double value;

    public IndexComparator(int index, Double value) {
      this.index = index;
      this.value = value;
    }

    @Override
    public int compareTo(IndexComparator o) {
      if ((value != null) && (o.value != null)) {
        final int c = Double.compare(value, o.value);
        if (c == 0) {
          return (index - o.index) * descending;
        }
        return c * descending;
      } else if ((value == null) && (o.value == null)) {
        return (index - o.index) * descending;
      } else if (value == null) {
        return descending;
      } else {
        return -1 * descending;
      }
    }
  }
}
