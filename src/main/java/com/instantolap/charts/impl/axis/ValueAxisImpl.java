package com.instantolap.charts.impl.axis;

import com.instantolap.charts.CriticalArea;
import com.instantolap.charts.Cube;
import com.instantolap.charts.TargetLine;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class ValueAxisImpl extends BasicScaleAxisImpl implements ValueAxis {

  private String format;
  private Integer decimalCount;
  private boolean useZeroAsBase = true;

  public ValueAxisImpl(Theme theme) {
    super(theme);
    setTitleRotation(270);
  }

  @Override
  public String getFormat() {
    return format;
  }

  @Override
  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public void setDecimalCount(Integer count) {
    this.decimalCount = count;
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
      if (min == null) {
        min = 0.0;
      } else if (useZeroAsBase && min > 0) {
        min = 0.0;
      }
    }

    if (userMax != null) {
      max = userMax;
    } else {
      max = cube.getMax(measures);
      if (max == null) {
        max = 0.0;
      } else if (max < 0) {
        max = 0.0;
      }

      if (includeTargets) {
        for (TargetLine target : getTargetLines()) {
          max = Math.max(max, target.value);
        }
      }

      if (includeCriticalAreas) {
        for (CriticalArea area : getCriticalAreas()) {
          max = Math.max(max, area.max);
          max = Math.max(max, area.min);
        }
      }
    }

    // calc size
    neededWidth = 0;
    if ((min.equals(max)) || height <= 0) {
      grids = new double[0];
    } else {

      // find stepsize
      adjustTicksAndBorders(height);

      // find format
      if (format == null) {
        format = ScaleHelper.getFormat(tick, getDecimalCount());
      }

      // calc tick count
      double v = min;
      if (v % tick != 0) {
        final double d = v % tick;
        if (d < 0) {
          v -= d;
        } else {
          v += tick - d;
        }
      }

      int tickCount = 0;
      double tickV = v;
      while (tick > 0) {
        final double pos = getPosition(tickV);
        if (Double.isNaN(pos) ||pos < 0 || pos > size) {
          break;
        }

        tickCount++;
        tickV += tick;
      }

      // create custom grid
      gridLines = null;
      if (gridPositions != null) {
        gridLines = new double[gridPositions.length];
        texts = new String[gridPositions.length];
        for (int n = 0; n < gridPositions.length; n++) {
          gridLines[n] = getPosition(gridPositions[n]);

          // calc text width
          texts[n] = prefix + r.format(format, v) + postfix;
        }
      }

      // create grid
      grids = new double[tickCount];
      texts = new String[tickCount];
      for (int n = 0; n < tickCount; n++) {
        grids[n] = getPosition(v);

        // calc text width
        texts[n] = prefix + r.format(format, v) + postfix;

        if (isVisible() && isShowLabels()) {
          r.setFont(getFont());
          final double[] size = r.getTextSize(texts[n], getLabelRotation());
          neededWidth = Math.max(neededWidth, vertical ? size[0] : size[1]);
        }

        v += tick;
      }
    }

    // padding
    maxLabelSize = neededWidth;
    neededWidth += getTickWidth();
    if (isShowLabels()) {
      neededWidth += getLabelSpacing();
    }

    // title + padding
    neededWidth += getNeededTitleWidth(r, vertical);

    // targets
    double maxTargetSize = 0;
    for (TargetLine target : getTargetLines()) {
      final double[] size = r.getTextSize(target.text, getLabelRotation());
      maxTargetSize = Math.max(maxTargetSize, vertical ? size[0] : size[1]);
      target.valueText = prefix + r.format(format, target.value) + postfix;
    }
    if (maxTargetSize > 0) {
      neededWidth += getLabelSpacing();
    }
    neededWidth += maxTargetSize;
  }

  @Override
  protected double findBestScale(Double min, Double max, double size, double minTickSize) {
    return ScaleHelper.findBestScale(min, max, size, minTickSize);
  }

  @Override
  public Integer getDecimalCount() {
    return decimalCount;
  }

  @Override
  public void render(
    Renderer r,
    double x, double y,
    double width, double height,
    boolean isCentered,
    boolean flip,
    ChartFont font) {
    if ((width <= 0) || (height <= 0)) {
      return;
    }

    super.render(r, x, y, width, height, isCentered, flip, font);
  }

  @Override
  public void setUseZeroAsBase(boolean use) {
    this.useZeroAsBase = use;
  }

  @Override
  public boolean isUseZeroAsBase() {
    return useZeroAsBase;
  }
}
