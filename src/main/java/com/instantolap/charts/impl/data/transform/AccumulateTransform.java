package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.CubeImpl;


public class AccumulateTransform extends BasicTransform {

  @Override
  public Cube transform(Cube cube) {
    final CubeImpl newCube = new CubeImpl();
    transferDimensions(cube, newCube);

    final int size0 = cube.getSampleCount(0);
    if (size0 > 0) {
      final int size1 = Math.max(cube.getSampleCount(1), 1);
      for (int c1 = 0; c1 < size1; c1++) {

        // first value
        double last = cube.get(Cube.MEASURE_VALUE, 0, c1);
        newCube.set(Cube.MEASURE_VALUE, last, 0, c1);

        // other values
        for (int c0 = 1; c0 < size0; c0++) {
          final Double value = cube.get(Cube.MEASURE_VALUE, c0, c1);
          if (value != null) {
            last += value;
            newCube.set(Cube.MEASURE_VALUE, last, c0, c1);
          }
        }
      }
    }

    return newCube;
  }
}
