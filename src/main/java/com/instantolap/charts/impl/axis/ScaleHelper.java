package com.instantolap.charts.impl.axis;

public class ScaleHelper {

  private static final long[] TIME_SCALES = new long[]{
    1000, 60 * 1000,
    5 * 60 * 1000,
    10 * 60 * 1000,
    15 * 60 * 1000,
    30 * 60 * 1000,
    60 * 60 * 1000,
    3 * 60 * 60 * 1000,
    6 * 60 * 60 * 1000,
    12 * 60 * 60 * 1000,
    24 * 60 * 60 * 1000
  };

  public static String getFormat(double scale, Integer decimalCount) {
    if (scale == 0) {
      return null;
    }
    final StringBuilder pattern = new StringBuilder();
    pattern.append("###,###,###,##0");

    if ((decimalCount != null) && (decimalCount > 0)) {
      pattern.append(".");
      for (int n = 0; n < decimalCount; n++) {
        pattern.append("0");
      }
    } else {
      scale = scale - Math.floor(scale);
      if ((scale != 0) && (scale < 1)) {
        pattern.append(".");
        while (Math.round(scale) != scale) {
          pattern.append("0");
          scale *= 10;
        }
      }
    }

    return pattern.toString();
  }

  public static double findBestScale(double min, double max, double height, double minGridSize) {
    if (height <= 0
      || Double.isInfinite(max)
      || Double.isNaN(max)
      || Double.isInfinite(min)
      || Double.isNaN(min)) {
      return 1;
    }

    final double diff = max - min;
    double step = 1;

    if (diff > 0) {
      double diff2 = diff;
      while (diff2 < 1) {
        step /= 10.0;
        diff2 *= 10;
      }
    }

    for (int n = 0; true; n++) {
      final double gridSize = height / (diff / step);
      if (gridSize >= minGridSize) {
        break;
      }

      switch (n % 3) {
        case 0:
          step *= 2;
          break;
        case 1:
          step *= 2.5;
          break;
        case 2:
          step *= 2;
          break;
      }
      // if (n % 3 == 0) {
      // step *= 2.5;
      // } else {
      // step *= 2;
      // }

      // end of the world reached
      if (Double.isInfinite(step)) {
        break;
      }
    }

    return step;
  }

  public static double findBestTimeScale(long min, long max, double height, double minGridSize) {
    if (height <= 0) {
      return 1;
    }

    final long diff = max - min;
    if (diff == 0) {
      return TIME_SCALES[0];
    }

    for (long scale : TIME_SCALES) {
      final double size = height / (diff / scale);
      if (size >= minGridSize) {
        return scale;
      }
    }

    return TIME_SCALES[TIME_SCALES.length - 1];
  }
}
