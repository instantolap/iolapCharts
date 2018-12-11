package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.CubeImpl;


public class SubcubeTransform extends BasicTransform {

  private final int[] dimensions;

  public SubcubeTransform(int[] dimensions) {
    this.dimensions = dimensions;
  }

  @Override
  public Cube transform(Cube cube) {
    final CubeImpl newCube = new CubeImpl();

    // transfer dimensions
    for (int n = 0; n < dimensions.length; n++) {
      final int d = dimensions[n];
      final int c = cube.getSampleCount(d);
      for (int i = 0; i < c; i++) {
        newCube.addSample(n, cube.getSample(d, i));
      }
    }

    // transfer values
    final int[] otherDimensions = getMissingDimensions(cube, dimensions);

    final int newSize = getSize(cube, dimensions);
    final int otherSize = getSize(cube, otherDimensions);

    final int[] pos = new int[cube.getDimensionCount()];
    final int[] targetPos = new int[dimensions.length];
    for (int n = 0; n < newSize; n++) {

      // build access key
      int v = n;
      for (int i = 0; i < dimensions.length; i++) {
        final int d = dimensions[i];
        final int c = cube.getSampleCount(d);
        pos[d] = v % c;
        targetPos[i] = pos[d];
        v /= c;
      }

      // get aggregated value
      final Double value = aggregate(cube, pos, otherDimensions, otherSize);
      if (value != null) {
        newCube.set(Cube.MEASURE_VALUE, value, targetPos);
      }
    }

    return newCube;
  }
}
