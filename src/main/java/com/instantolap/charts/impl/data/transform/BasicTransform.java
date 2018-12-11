package com.instantolap.charts.impl.data.transform;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Transformation;
import com.instantolap.charts.impl.data.CubeImpl;
import com.instantolap.charts.impl.util.ArrayHelper;


public abstract class BasicTransform implements Transformation {

  protected static void transferDimensions(Cube cube, CubeImpl newCube) {
    // transfer dimensions
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      final int count = cube.getSampleCount(d);
      for (int c0 = 0; c0 < count; c0++) {
        newCube.addSample(d, cube.getSample(d, c0));
      }
    }
  }

  protected static int getSize(Cube cube) {
    int newSize = 1;
    for (int d = 0; d < cube.getDimensionCount(); d++) {
      newSize *= cube.getSampleCount(d);
    }
    return newSize;
  }

  protected static int getSize(Cube cube, int[] dimensions) {
    int newSize = 1;
    for (int d : dimensions) {
      newSize *= cube.getSampleCount(d);
    }
    return newSize;
  }

  protected static int[] getMissingDimensions(Cube cube, int[] dimensions) {
    final int[] otherDimensions = new int[cube.getDimensionCount()
      - dimensions.length];
    int index = 0;
    for (int n = 0; n < cube.getDimensionCount(); n++) {
      if (!ArrayHelper.contains(dimensions, n)) {
        otherDimensions[index++] = n;
      }
    }
    return otherDimensions;
  }

  protected static Double aggregate(Cube cube, int[] pos, int[] otherDimensions, int otherSize) {
    Double result = null;
    for (int n = 0; n < otherSize; n++) {

      // fill pos with missing samples
      int p = n;
      for (final int d : otherDimensions) {
        final int c = cube.getSampleCount(d);
        pos[d] = p % c;
        p /= c;
      }

      // aggregate value
      final Double value = cube.get(Cube.MEASURE_VALUE, pos);
      if (result == null) {
        result = value;
      } else if (value != null) {
        result += value;
      }
    }
    return result;
  }
}
