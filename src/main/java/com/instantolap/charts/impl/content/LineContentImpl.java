package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.PositionAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.LeftToRightContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.math.PolynomialSplineFunction;
import com.instantolap.charts.impl.math.SimpleRegression;
import com.instantolap.charts.impl.math.SplineInterpolator;
import com.instantolap.charts.impl.util.LabelDrawer;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.*;

import java.util.ArrayList;
import java.util.List;


public class LineContentImpl extends BasicLineContentImpl implements SampleValueRenderer {

  public LineContentImpl(Theme theme) {
    super(theme);

    setAnimation(new LeftToRightContentAnim());
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return false;
  }

  @Override
  public void render(
    double progress,
    Renderer r,
    Data data,
    double x, double y,
    double width, double height,
    PositionAxis xAxis, ValueAxis yAxis,
    boolean isStacked, boolean isCentered, boolean isRotated,
    ChartFont font, ChartColor background) throws ChartException {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final int dimensions = cube.getDimensionCount();
    if (dimensions < 1 || dimensions > 2) {
      throw new ChartException("Line contents can only display 1. or 2-dimensional data");
    }

    final boolean isConnected = isConnected();
    final boolean spline = isInterpolated();
    final boolean stepLine = isStepLine();

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final ContentAnimation anim = getAnimation();

    final int size0 = cube.getSampleCount(0);
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    final double xOffset = getShadowXOffset();
    final double yOffset = getShadowYOffset();

    final PolynomialSplineFunction[][] cubics =
      buildInterpolations(spline, cube, anim, yAxis, progress, isStacked);

    if (isCentered) {
      x += xAxis.getSampleWidth() / 2;
    }

    final String yMeasure = getMeasure();
    final String format = yAxis.getFormat();

    // init regression
    final SimpleRegression regression = initRegression();

    // draw lines
    for (int pass = 0; pass < 5; pass++) {
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }

        // progress
        final double bar = (double) c0 / (double) (size0 - 1);

        // centered?
        double avgY = 0, avgCount = 0;
        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }

          final double xx = xAxis.getSamplePosition(cube, c0, c1);
          final int symbolWidth = data.getSymbolSize(c1);
          final ChartStroke stroke = data.getStroke(c1);

          // colors c1
          final ChartColor sampleColor = getSampleColor(progress, bar, anim, data, c1, c0, false);
          final ChartColor areaColor = getAreaColor(progress, bar, anim, data, getColorRange(), c1, c0);
          final ChartColor outlineColor = getOutlineColor(progress, bar, anim, data, c1, c0);
          final ChartColor shadowColor = getCurrentShadow(anim, progress, bar, data, c1, c0);

          final Double maxValue = cube.get(yMeasure, c0, c1);
          if (maxValue != null) {

            // find lower and higher y
            final double yMax = yAxis.getPosition(anim.getValue(progress, bar, maxValue));

            boolean isLastValue = true;
            if (isStacked) {
              for (int n = c1 + 1; n < size1; n++) {
                final Double o = cube.get(yMeasure, c0, n);
                if ((o != null) && (o * maxValue >= 0)) {
                  isLastValue = false;
                }
              }
            }

            // average for regression
            if (isLastValue) {
              avgY += yMax;
              avgCount++;
            }

            if (c0 > 0) {

              // find last sample (don't change code, otherwise GWT compiler hangs)
              int prevSample = c0 - 1;
              if (isConnected && !spline) {
                prevSample = findLastIndexWithValue(cube, yMeasure, c0, c1);
              }
              final Double prevMaxValue = cube.get(yMeasure, prevSample, c1);
              if (prevMaxValue != null) {

                final double px = xAxis.getSamplePosition(cube, prevSample, c1);
                final double pyMax = yAxis.getPosition(anim.getValue(progress, bar, prevMaxValue));

                switch (pass) {
                  case 0:
                    // draw area?
                    if (areaColor != null) {
                      final Integer start = getFillStart(c1);

                      // draw area
                      r.setColor(areaColor);
                      if (spline) {
                        splineFill(
                          r,
                          progress, bar,
                          x, y,
                          anim, cube, cubics, c1,
                          start, prevSample, c0,
                          yAxis, xAxis
                        );
                      } else {
                        // find previous sample
                        Double minValue = null;
                        Double prevMinValue = null;
                        if (isStacked && (start == null)) {
                          prevMinValue = cube.get(Cube.MEASURE_LOWER, prevSample, c1);
                          minValue = cube.get(Cube.MEASURE_LOWER, c0, c1);
                        } else if (start != null) {
                          minValue = cube.get(getMeasure(), c0, start);
                          prevMinValue = cube.get(getMeasure(), prevSample, start);
                        }
                        if (minValue == null) {
                          minValue = 0.0;
                        }
                        if (prevMinValue == null) {
                          prevMinValue = 0.0;
                        }

                        final double yMin = yAxis.getPosition(anim.getValue(progress, bar, minValue));
                        final double pyMin = yAxis.getPosition(anim.getValue(progress, bar, prevMinValue));

                        final double[] xp;
                        final double[] yp;
                        if (stepLine) {
                          xp = new double[]{px, xx, xx, px};
                          yp = new double[]{yMax, yMax, yMin, yMin};
                        } else {
                          xp = new double[]{px, xx, xx, px};
                          yp = new double[]{pyMax, yMax, yMin, pyMin};
                        }

                        if (isRotated) {
                          r.fillPolygon(shift(yp, x), shift(xp, y));
                        } else {
                          r.fillPolygon(shift(xp, x), shift(yp, y));
                        }
                      }
                    }
                    break;

                  case 1:
                    // draw shadow
                    if (stroke.getWidth() > 0) {
                      r.setStroke(stroke);
                      if (shadowColor != null) {
                        r.setColor(shadowColor);
                        drawLine(r,
                          xAxis, yAxis, x + xOffset, y + yOffset,
                          isRotated, spline,
                          stepLine, cube, cubics, xx,
                          yMax, prevSample, px,
                          pyMax, c0, c1, progress,
                          bar
                        );
                      }

                      // draw outline
                      if (outlineColor != null) {
                        r.setColor(outlineColor);
                        r.setStroke(stroke.incStroke(2));
                        drawLine(r,
                          xAxis, yAxis, x, y,
                          isRotated, spline,
                          stepLine, cube, cubics, xx,
                          yMax, prevSample, px,
                          pyMax, c0, c1, progress,
                          bar
                        );
                      }
                    }
                    break;

                  case 2:
                    // draw line
                    if (stroke.getWidth() > 0) {
                      r.setColor(sampleColor);
                      r.setStroke(stroke);
                      drawLine(r,
                        xAxis, yAxis, x, y,
                        isRotated, spline, stepLine,
                        cube, cubics, xx, yMax,
                        prevSample, px, pyMax, c0, c1,
                        progress, bar
                      );
                      r.resetStroke();
                    }
                    break;
                }
              }
            }

            switch (pass) {
              case 3:
                // draw symbol
                if (symbolWidth > 0) {
                  double sx = x + xx;
                  double sy = y + yMax;
                  if (isRotated) {
                    sx = x + yMax;
                    sy = y + xx;
                  }

                  final int symbol = data.getSymbol(c1);

                  if (shadowColor != null) {
                    SymbolDrawer.draw(
                      r,
                      symbol,
                      sx + xOffset, sy + yOffset,
                      symbolWidth,
                      shadowColor, shadowColor, shadowColor
                    );
                  }

                  SymbolDrawer.draw(
                    r,
                    symbol,
                    sx, sy,
                    symbolWidth,
                    sampleColor, outlineColor, background
                  );
                }
                break;

              case 4:
                if (progress >= 1) {
                  final String text = r.format(format, maxValue);

                  final double cx;
                  final double cy;
                  final double rectHeight = symbolWidth;
                  final double rectWidth = symbolWidth;
                  if (isRotated) {
                    cx = x + yMax;
                    cy = y + xx;
                  } else {
                    cx = x + xx;
                    cy = y + yMax;
                  }
                  final double rectX = cx - symbolWidth / 2.0;
                  final double rectY = cy - symbolWidth / 2.0;

                  final int anchor;
                  if (isRotated) {
                    anchor = maxValue >= 0 ? Renderer.EAST : Renderer.WEST;
                  } else {
                    anchor = maxValue >= 0 ? Renderer.NORTH : Renderer.SOUTH;
                  }

                  // popup / links
                  final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);
                  if (isShowPopup() || (linkCommands[0] != null)) {
                    final String popupText = buildPopupText(cube, c0, c1, text);

                    final double wAdd = Math.max(rectWidth, 6);
                    final double hAdd = Math.max(rectHeight, 6);
                    r.addPopup(cx - wAdd / 2, cy - hAdd / 2,
                      wAdd, hAdd, 0, anchor, popupText,
                      getPopupFont(), linkCommands[0],
                      linkCommands[1], linkCommands[2]
                    );
                  }

                  // labels
                  final boolean isSelected = data.isSelected(1, c1);
                  if (isSelected || isLastValue) {
                    final String label = buildLabelText(cube, c0, c1, text, isSelected);
                    if (label != null) {
                      final ChartColor c = anim.getLabelColor(progress, bar, getLabelColor());

                      r.setFont(labelFont);
                      LabelDrawer.drawInsideOutside(r, rectX,
                        rectY, rectHeight, rectWidth,
                        getLabelSpacing(), anchor,
                        getLabelAngle(), c, c, label, getValueLabelType()
                      );
                    }
                  }
                }
                break;
            }
          }
          if (c1 == 0 && regression != null) {
            regression.addData(xx, avgY / avgCount);
          }
        }
      }
    }

    // draw regression line?
    drawRegression(r, x, y, width, height, isRotated, regression);
    drawAnnotations(r, xAxis, yAxis, x, y, width, height, isCentered);
  }

  private PolynomialSplineFunction[][] buildInterpolations(boolean spline,
                                                           Cube cube, ContentAnimation anim, ValueAxis yAxis, double progress,
                                                           boolean isStacked) {
    PolynomialSplineFunction[][] cubics = null;
    if (spline) {
      final int size0 = cube.getSampleCount(0);
      final int size1 = Math.max(cube.getSampleCount(1), 1);
      cubics = new PolynomialSplineFunction[size1][2];
      for (int c1 = 0; c1 < size1; c1++) {
        final List<Number> minX = new ArrayList<>();
        final List<Number> minV = new ArrayList<>();
        final List<Number> maxX = new ArrayList<>();
        final List<Number> maxV = new ArrayList<>();
        for (int c0 = 0; c0 < size0; c0++) {
          final double progress0 = (double) c0 / (size0 - 1);
          final Double max = cube.get(getMeasure(), c0, c1);
          if (max != null) {
            maxX.add(c0);
            double dy = yAxis.getPosition(anim.getValue(progress, progress0, max));
            maxV.add(dy);

            Double min = null;
            if (isStacked) {
              min = cube.get(Cube.MEASURE_LOWER, c0, c1);
            } else if (c1 > 0) {
              min = cube.get(getMeasure(), c0, c1 - 1);
            }
            if (min == null) {
              min = 0.0;
            }

            minX.add(c0);
            dy = yAxis.getPosition(anim.getValue(progress, progress0, min));
            minV.add(dy);
          }
        }

        final SplineInterpolator i = new SplineInterpolator();
        if (minX.size() >= 3) {
          cubics[c1][0] = i.interpolate(toArray(minX), toArray(minV));
          cubics[c1][1] = i.interpolate(toArray(maxX), toArray(maxV));
        }
      }
    }
    return cubics;
  }

  private int findLastIndexWithValue(Cube cube, String yMeasure, int c0, int c1) {
    int prevSample;
    for (prevSample = c0 - 1; prevSample >= 0; prevSample--) {
      if (cube.get(yMeasure, prevSample, c1) != null) {
        break;
      }
    }
    return prevSample;
  }

  private void splineFill(Renderer r, double progress, double bar, double x,
                          double y, ContentAnimation anim, Cube cube,
                          PolynomialSplineFunction[][] cubics, int series,
                          Integer startSeries, int prevSample, int sample, ValueAxis yAxis,
                          PositionAxis sampleAxis) {
    final double startX = sampleAxis.getSamplePosition(cube, prevSample, series);
    final double endX = sampleAxis.getSamplePosition(cube, sample, series);
    final double[] xx = new double[(int) ((endX - startX + 1) * 2)];
    final double[] yy = new double[xx.length];
    int pos = 0;
    for (double dx = startX; dx <= endX; dx++) {
      final double p = (double) (dx - startX) / (double) (endX - startX);
      final double vx = prevSample + p * (sample - prevSample);
      final double yMin;
      if (startSeries != null) {
        yMin = cubics[startSeries][1].value(vx);
      } else {
        yMin = cubics[series][0].value(vx);
      }
      final double yMax = cubics[series][1].value(vx);
      xx[pos] = x + dx;
      xx[xx.length - pos - 1] = x + dx;
      yy[pos] = y + yMax;
      yy[yy.length - pos - 1] = y + yMin;
      pos++;
    }
    r.fillPolygon(xx, yy);
  }

  private void drawLine(Renderer r, PositionAxis xAxis, ValueAxis yAxis,
                        double x, double y, boolean isRotated, boolean spline, boolean stepLine,
                        Cube cube, PolynomialSplineFunction[][] cubics, double xx, double yMax,
                        int prevSample, double px, double pyMax, int c0, int c1, double progress,
                        double bar) {
    final ContentAnimation anim = getAnimation();

    if (isRotated) {
      if (spline) {
        // TODO ?
      } else if (stepLine) {
        r.drawLine(x + pyMax, y + px, x + yMax, y + px);
        r.drawLine(x + yMax, y + px, x + yMax, y + xx);
      } else {
        r.drawLine(x + pyMax, y + px, x + yMax, y + xx);
      }
    } else {
      if (spline) {
        spline(r, progress, bar, x, y, anim, cube, cubics, c1, prevSample, c0, xAxis);
      } else if (stepLine) {
        r.drawLine(x + px, y + pyMax, x + px, y + yMax);
        r.drawLine(x + px, y + yMax, x + xx, y + yMax);
      } else {
        r.drawLine(x + px, y + pyMax, x + xx, y + yMax);
      }
    }
  }

  private double[] toArray(List<Number> a) {
    final double[] result = new double[a.size()];
    for (int n = 0; n < a.size(); n++) {
      final Number number = a.get(n);
      result[n] = number.doubleValue();
    }
    return result;
  }

  private void spline(Renderer r, double progress, double bar, double x, double y,
                      ContentAnimation anim, Cube cube,
                      PolynomialSplineFunction[][] cubics, int series, int prevSample,
                      int sample, PositionAxis sampleAxis) {
    final double startX = sampleAxis.getSamplePosition(cube, prevSample, series);
    final double endX = sampleAxis.getSamplePosition(cube, sample, series);
    final double[] xx = new double[(int) (endX - startX + 1)];
    final double[] yy = new double[xx.length];
    int pos = 0;
    for (double dx = startX; dx <= endX; dx++) {
      final double p = (double) (dx - startX) / (double) (endX - startX);
      final double vx = prevSample + p * (sample - prevSample);
      final double dy = cubics[series][1].value(vx);
      xx[pos] = x + dx;
      yy[pos] = y + dy;
      pos++;
    }
    r.drawPolyLine(xx, yy);
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
    axis.addMeasures(getMeasure());
    axis.addMeasures(getLowerMeasure());
  }
}
