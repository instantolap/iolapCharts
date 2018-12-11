package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.CubeImpl;


public class NormalizeTransform extends BasicTransform {

  @Override
  public Cube transform(Cube cube) {
    if (cube.getDimensionCount() != 2) {
      return cube;
    }

    final CubeImpl newCube = new CubeImpl();
    transferDimensions(cube, newCube);

    final int size0 = cube.getSampleCount(0);
    final int size1 = cube.getSampleCount(1);
    for (int c0 = 0; c0 < size0; c0++) {

      // calc total
      double total = 0;
      for (int c1 = 0; c1 < size1; c1++) {
        final Double value = cube.get(Cube.MEASURE_VALUE, c0, c1);
        if ((value != null) && (value > 0)) {
          total += value;
        }
      }

      // calc % values
      for (int c1 = 0; c1 < size1; c1++) {
        final Double value = cube.get(Cube.MEASURE_VALUE, c0, c1);
        if ((value != null) && (value > 0)) {
          final double newValue = value / total;
          newCube.set(Cube.MEASURE_VALUE, newValue, c0, c1);
        }
      }
    }
    return newCube;
  }
}
