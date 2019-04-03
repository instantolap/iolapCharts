package com.instantolap.charts.impl.axis;

import com.instantolap.charts.ScaleAxis;
import com.instantolap.charts.ScaleAxisListener;
import com.instantolap.charts.TargetLine;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.util.ArrayHelper;
import com.instantolap.charts.renderer.*;

import java.util.ArrayList;
import java.util.List;


public abstract class BasicScaleAxisImpl extends BasicAxisImpl implements ScaleAxis {

  private final List<ScaleAxisListener> listeners = new ArrayList<>();
  protected Double userMin, userMax;
  protected String[] measures = new String[0];
  protected Double min, max;
  protected Double tick, userTick;
  protected double minTickSize = 25;
  protected int maxLineCount = Integer.MAX_VALUE;
  protected double size;
  protected boolean vertical;
  protected double[] grids;
  protected double[] gridPositions;
  protected double[] gridLines;
  protected String[] texts;
  protected double neededWidth;
  private boolean isZoomEnabled = true;
  private double zoomStep = 1.2;
  protected final List<TargetLine> targets = new ArrayList<>();
  protected boolean includeTargets = true;
  protected double maxLabelSize;

  public BasicScaleAxisImpl(Theme theme) {
    super(theme);
    setTitleRotation(270);
  }

  @Override
  public void clearMeasures() {
    measures = new String[0];
  }

  @Override
  public void addMeasures(String... newMeasures) {
    for (String m : newMeasures) {
      if (m != null) {
        measures = ArrayHelper.add(measures, m);
      }
    }
  }

  @Override
  public Double getMin() {
    return min;
  }

  @Override
  public void setMin(Double min) {
    this.userMin = min;
  }

  @Override
  public Double getMax() {
    return max;
  }

  @Override
  public void setMax(Double max) {
    this.userMax = max;
  }

  @Override
  public double getMinTickSize() {
    return minTickSize;
  }

  @Override
  public void setMinTickSize(double minTickSize) {
    this.minTickSize = minTickSize;
  }

  @Override
  public int getMaxLineCount() {
    return maxLineCount;
  }

  @Override
  public void setMaxLineCount(int maxLineCount) {
    this.maxLineCount = maxLineCount;
  }

  @Override
  public Double getTick() {
    return tick;
  }

  @Override
  public void setTick(Double stepSize) {
    this.tick = stepSize;
  }

  @Override
  public Double getUserTick() {
    return userTick;
  }

  @Override
  public void setUserTick(Double stepSize) {
    this.userTick = stepSize;
  }

  @Override
  public void clearTargets() {
    targets.clear();
  }

  @Override
  public void addTargetLine(
    double value, String text, ChartColor color, ChartColor background, ChartStroke stroke) {
    targets.add(new TargetLine(value, text, color, background, stroke));
  }

  @Override
  public TargetLine[] getTargetLines() {
    return targets.toArray(new TargetLine[0]);
  }

  @Override
  public void setIncludeTargets(boolean includeTargets) {
    this.includeTargets = includeTargets;
  }

  @Override
  public boolean isIncludeTargets() {
    return includeTargets;
  }

  @Override
  public double getPosition(double v) {
    double pos = getRadius(v);
    if (vertical) {
      pos = size - pos;
    }
    return pos;
  }

  @Override
  public double getRadius(double v) {
    if (min == null || max == null) {
      return 0;
    }

    return (v - min) / (max - min) * size;
  }

  @Override
  public void enableZoom(boolean isZoomEnabled) {
    this.isZoomEnabled = isZoomEnabled;
  }

  @Override
  public boolean isZoomEnabled() {
    return isZoomEnabled;
  }

  @Override
  public double getZoomStep() {
    return zoomStep;
  }

  @Override
  public void setZoomStep(double step) {
    this.zoomStep = step;
  }

  @Override
  public void zoom(double zoom, double center) {
    if (min == null || max == null) {
      return;
    }

    final double v = (min + (max - min) * center);
    userMin = v + (min - v) * zoom;
    userMax = v + (max - v) * zoom;

    fireMinMaxUpdate();
  }

  @Override
  public void translate(double shift) {
    if (min == null || max == null) {
      return;
    }

    userMin = min + shift;
    userMax = max + shift;

    fireMinMaxUpdate();
  }

  @Override
  public void addListener(ScaleAxisListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(ScaleAxisListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void setGridPositions(double[] gridPositions) {
    this.gridPositions = gridPositions;
  }

  protected String getMeasure() {
    return measures[0];
  }

  @Override
  public double[] getGrid() {
    return grids;
  }

  @Override
  public double[] getCenteredGrid() {
    return grids;
  }

  @Override
  public double[] getGridLines() {
    if (gridLines != null) {
      return gridLines;
    }
    return grids;
  }

  @Override
  public String[] getTexts() {
    return texts;
  }

  @Override
  public double getNeededSize() {
    return neededWidth;
  }

  @Override
  public double getSize() {
    return size;
  }

  protected void adjustTicksAndBorders(double size) {
    if (Double.isNaN(min) || Double.isNaN(max)) {
      return;
    }

    double minTickSize = getMinTickSize();
    final int maxLineCount = getMaxLineCount();
    minTickSize = Math.max(minTickSize, size / maxLineCount);

    if (userTick != null) {
      tick = userTick;
      adjustMinMax();
    } else {
      // find stepsize (max 10 times, avoid endless loop)
      boolean newTick = true;
      for (int n = 0; newTick && n < 10; n++) {
        tick = findBestScale(min, max, size, minTickSize);
        newTick = adjustMinMax();
      }
    }
  }

  private boolean adjustMinMax() {
    boolean newTick = false;

    // adjust bounds
    if (userMin == null) {
      if (min % tick != 0) {
        if (min >= 0) {
          min -= min % tick;
        } else {
          min -= tick + (min % tick);
        }
        newTick = true;
      }
    }

    if (userMax == null) {
      if (max % tick != 0) {
        if (max >= 0) {
          max += tick - (max % tick);
        } else {
          // TODO
        }
        newTick = true;
      }
    }
    return newTick;
  }

  protected abstract double findBestScale(Double min, Double max, double size, double minTickSize);

  @Override
  public void render(final Renderer r, final double x, final double y,
                     final double width, final double height, boolean isCentered,
                     boolean flip, ChartFont font) {
    super.render(r, x, y, width, height, isCentered, flip, font);

    // add mouse listener for zoom
    if (isZoomEnabled) {
      r.addMouseListener(x, y, width, height,
        (ChartMouseWheelListener) (mx, my, v) -> {
          if (isVertical()) {
            doZoom(r, v, my, y, height);
          } else {
            doZoom(r, v, mx, x, width);
          }
        }
      );

      r.addMouseListener(
        x, y, width, height, (ChartMouseDragListener) (dx, dy) -> doDrag(r, dx, dy));
    }

    // target lines
    ChartFont usedFont = getFont();
    if (usedFont == null) {
      usedFont = font;
    }
    r.setFont(usedFont);

    final double tick = getTickWidth();
    final double spacing = getLabelSpacing();
    final double rot = getLabelRotation();

    for (TargetLine target : getTargetLines()) {
      final double pos = getPosition(target.value);
      r.setColor(target.color);
      if (isVertical()) {
        if (flip) {
          r.setColor(target.color);
          r.drawLine(x + width - tick, y + pos, x + width, y + pos);

          // background
          if ((target.background != null) && (target.valueText != null)) {
            r.setColor(target.background);
            final double textHeight = ((r.getTextSize(target.valueText, rot)[1] + 4) / 2);
            final double[] xx = new double[]{
              x,
              x + width,
              x + width + textHeight,
              x + width,
              x};
            final double[] yy = new double[]{
              y + pos - textHeight,
              y + pos - textHeight,
              y + pos,
              y + pos + textHeight,
              y + pos + textHeight
            };
            r.fillPolygon(xx, yy);

            r.setColor(target.color);
            r.drawPolygon(xx, yy);
          }

          // text / value text
          r.drawText(
            x + width - tick - maxLabelSize - 2 * spacing, y + pos, target.text, rot, Renderer.EAST, false
          );
          if (target.valueText != null) {
            r.drawText(x + width - tick - spacing, y + pos, target.valueText, rot, Renderer.EAST, false);
          }
        } else {
          r.setColor(target.color);
          r.drawLine(x, y + pos, x + tick, y + pos);

          // background
          if ((target.background != null) && (target.valueText != null)) {
            r.setColor(target.background);
          }

          // text / value text
          r.drawText(
            x + tick + maxLabelSize + 2 * spacing, y + pos, target.text, rot, Renderer.WEST, false
          );

          if (target.valueText != null) {
            r.drawText(x + tick + spacing, y + pos, target.valueText, rot, Renderer.WEST, false);
          }
        }
      } else {
        if (flip) {
          r.setColor(target.color);
          r.drawLine(x + pos, y + height - tick, x + pos, y + height);

          // background
          if ((target.background != null) && (target.valueText != null)) {
            r.setColor(target.background);
          }

          // text / value text
          r.drawText(
            x + pos, y + height - tick - 2 * spacing - maxLabelSize, target.text, rot,
            Renderer.SOUTH, false
          );

          if (target.valueText != null) {
            r.drawText(
              x + pos, y + height - tick - spacing, target.valueText, spacing, Renderer.SOUTH, false
            );
          }
        } else {
          r.setColor(target.color);
          r.drawLine(x + pos, y, x + pos, y + tick);

          // background
          if ((target.background != null) && (target.valueText != null)) {
            r.setColor(target.background);
          }

          // text / value text
          r.drawText(
            x + pos, y + tick + 2 * spacing + maxLabelSize, target.text, rot, Renderer.NORTH, false
          );

          if (target.valueText != null) {
            r.drawText(
              x + pos, y + tick + spacing, target.valueText, rot, Renderer.NORTH, false
            );
          }
        }
      }
    }
  }

  @Override
  public boolean isVertical() {
    return vertical;
  }

  public void doZoom(final Renderer r, int wheelMotion, double x, double startX, double width) {
    try {
      final double f = (double) (x - startX) / (width);
      if (wheelMotion < 0) {
        zoom(1 / getZoomStep(), f);
      } else {
        zoom(getZoomStep(), f);
      }
      r.fireRepaint(true);
    } catch (ChartException ignored) {
    }
  }

  public void doDrag(Renderer r, int dx, int dy) {
    try {
      if (isVertical()) {
        final double f = (max - min) / size;
        translate(-dy * f);
      } else {
        final double f = (max - min) / size;
        translate(dx * f);
      }
      r.fireRepaint(true);
    } catch (Exception ignored) {
    }
  }

  private void fireMinMaxUpdate() {
    for (ScaleAxisListener l : listeners) {
      l.onTranslate(userMin, userMax);
    }
  }

}
