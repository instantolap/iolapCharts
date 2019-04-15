package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.CubeImpl;


public class StackedTransform extends BasicTransform {

  @Override
  public Cube transform(Cube cube) {
    if (cube.getDimensionCount() < 2) {
      return cube;
    }

    final CubeImpl newCube = new CubeImpl();
    transferDimensions(cube, newCube);

    // aggregate
    final int size0 = cube.getSampleCount(0);
    final int size1 = cube.getSampleCount(1);
    for (int c0 = 0; c0 < size0; c0++) {
      double pos = 0, neg = 0;
      for (int c1 = 0; c1 < size1; c1++) {
        final Double value = cube.get(Cube.MEASURE_VALUE, c0, c1);
        if (value != null) {
          if (value < 0) {
            newCube.set(Cube.MEASURE_LOWER, pos, c0, c1);
            pos += value;
            newCube.set(Cube.MEASURE_VALUE, pos, c0, c1);
          } else {
            newCube.set(Cube.MEASURE_LOWER, neg, c0, c1);
            neg += value;
            newCube.set(Cube.MEASURE_VALUE, neg, c0, c1);
          }
        }
        final String link = cube.getString(Cube.MEASURE_LINK, c0, c1);
        if (link != null) {
          newCube.set(Cube.MEASURE_LINK, link, c0, c1);
        }
      }
    }

    return newCube;
  }
}
