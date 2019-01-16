package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Cube;
import com.instantolap.charts.TimeAxis;
import com.instantolap.charts.impl.data.SlicedCube;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.Renderer;

import java.util.Date;


public class TimeAxisImpl extends BasicScaleAxisImpl implements TimeAxis {

  private double minSampleWidth = 1;
  private double maxSampleWidth = Integer.MAX_VALUE;
  private long sampleMilliseconds = 60000;

  public TimeAxisImpl(Theme theme) {
    super(theme);
    addMeasures(Cube.MEASURE_TIME);
    setTitleRotation(270);
    setMinTickSize(100);
  }

  @Override
  public void setData(
    double height,
    Renderer r,
    int axisNum,
    boolean isStacked,
    boolean isCentered,
    boolean vertical,
    int index) {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    this.size = height;
    this.vertical = vertical;

    String prefix = getPrefix();
    if (prefix == null) {
      prefix = "";
    }

    String postfix = getPostfix();
    if (postfix == null) {
      postfix = "";
    }

    // find range
    if (userMin != null) {
      min = userMin;
    } else {
      min = cube.getMin(measures);
    }

    if (userMax != null) {
      max = userMax;
    } else {
      max = cube.getMax(measures);
    }

    // calc size
    neededWidth = 0;
    if ((min == null) || (max == null) || (min.equals(max)) || (height <= 0)) {
      grids = new double[0];
    } else {

      // find max ticksize
      adjustTicksAndBorders(height);

      // calc tick count
      double v = min;
      if (v % tick != 0) {
        v += tick - min % tick;
      }

      int tickCount = 0;
      double tickV = v;
      while (tick > 0) {
        final double pos = getPosition(tickV);
        if (pos < 0 || pos > size) {
          break;
        }

        tickCount++;
        tickV += tick;
      }

      // create grid
      grids = new double[tickCount];
      texts = new String[tickCount];
      for (int n = 0; n < tickCount; n++) {
        final double pos = getPosition(v);
        if (pos > size) {
          break;
        }
        grids[n] = pos;

        final String format;
        if (v % (24 * 60 * 60 * 1000) == 0) {
          format = "dd.MM";
        } else if (v % 60000 == 0) {
          format = "HH:mm";
        } else if (v % 1000 == 0) {
          format = "HH:mm:ss";
        } else {
          format = "HH:mm:ss SSS";
        }
        final Date date = new Date((long) v);

        // calc text width
        texts[n] = prefix + r.format(format, date) + postfix;

        if (isShowLabels()) {
          final double[] size = r.getTextSize(texts[n], getLabelRotation());
          neededWidth = Math.max(neededWidth, vertical ? size[0] : size[1]);
        }

        v += tick;
      }
    }

    // padding
    neededWidth += getTickWidth();
    if (isShowLabels()) {
      neededWidth += getLabelSpacing();
    }

    // title + padding
    neededWidth += getNeededTitleWidth(r, vertical);
  }

  @Override
  public Cube buildSubCube(Cube cube) {
    if ((userMin == null) && (userMax == null)) {
      return cube;
    }

    final SlicedCube subCube = new SlicedCube(cube);

    if (userMin != null) {
      final int pos = seek(cube, userMin);
      if (pos >= 0) {
        subCube.setDimensionStart(0, pos);
      }
    }

    if (userMax != null) {
      final int pos = seek(cube, userMax);
      if (pos >= 0) {
        subCube.setDimensionEnd(0, pos + 1);
      }
    }

    return subCube;
  }

  private int seek(Cube cube, double v) {
    int max = cube.getSampleCount(0) - 1;
    int min = 0;
    int end = -1;
    while (min <= max) {
      final int pos = (max + min) / 2;
      final Double f = cube.get(getMeasure(), pos);
      if ((f != null) && (v < f)) {
        end = pos;
        max = pos - 1;
      } else {
        min = pos + 1;
      }
    }

    return end;
  }

  @Override
  protected double findBestScale(Double min, Double max, double size, double minTickSize) {
    return ScaleHelper.findBestTimeScale(min.longValue(), max.longValue(),
      size, minTickSize
    );
  }

  @Override
  public double getSamplePosition(Cube cube, int sample) {
    final Double v = cube.get(getMeasure(), sample);
    if (v == null) {
      return 0;
    }
    return getPosition(v);
  }

  @Override
  public double getSampleWidth() {
    double candleWidth = Math.abs(getPosition(getSampleMilliseconds()) - getPosition(0));
    candleWidth = Math.min(candleWidth, getMaxSampleWidth());
    candleWidth = Math.max(candleWidth, getMinSampleWidth());
    return candleWidth;
  }

  @Override
  public void setMinSampleWidth(double minSampleWidth) {
    this.minSampleWidth = minSampleWidth;
  }

  @Override
  public double getMinSampleWidth() {
    return minSampleWidth;
  }

  @Override
  public void setMaxSampleWidth(double maxSampleWidth) {
    this.maxSampleWidth = maxSampleWidth;
  }

  @Override
  public double getMaxSampleWidth() {
    return maxSampleWidth;
  }

  @Override
  public void setSampleMilliseconds(long sampleMS) {
    this.sampleMilliseconds = sampleMS;
  }

  @Override
  public long getSampleMilliseconds() {
    return sampleMilliseconds;
  }
}
