package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.CubeImpl;


public class ReverseTransform extends BasicTransform {

  private final int dimension;

  public ReverseTransform(int dimension) {
    this.dimension = dimension;
  }

  @Override
  public Cube transform(Cube cube) {
    final CubeImpl newCube = new CubeImpl();

    // transfer dimensions
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      final int count = cube.getSampleCount(d);
      for (int c0 = 0; c0 < count; c0++) {
        if (d == dimension) {
          newCube.addSample(d, cube.getSample(d, count - c0 - 1));
        } else {
          newCube.addSample(d, cube.getSample(d, c0));
        }
      }
    }

    // transfer values
    final int size = getSize(cube);
    final int[] pos = new int[cube.getDimensionCount()];
    final int[] targetPos = new int[cube.getDimensionCount()];
    for (int n = 0; n < size; n++) {
      int p = n;
      for (int d = 0; d < pos.length; d++) {
        final int count = cube.getSampleCount(d);
        pos[d] = p % count;
        p /= count;
        if (d == dimension) {
          targetPos[d] = count - pos[d] - 1;
        } else {
          targetPos[d] = pos[d];
        }
      }
      final Double value = cube.get(Cube.MEASURE_VALUE, pos);
      newCube.set(Cube.MEASURE_VALUE, value, targetPos);
    }
    return newCube;
  }
}
