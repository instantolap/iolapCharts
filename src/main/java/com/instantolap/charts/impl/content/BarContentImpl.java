package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.PositionAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.FadeInContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.math.SimpleRegression;
import com.instantolap.charts.impl.util.LabelDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class BarContentImpl extends BasicBarContentImpl implements SampleValueRenderer {

  public BarContentImpl(Theme theme) {
    super(theme);

    setAnimation(new FadeInContentAnim());
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return true;
  }

  @Override
  public void render(
    double progress,
    Renderer r,
    Data data,
    double x, double y,
    double width, double height,
    PositionAxis xAxis, ValueAxis yAxis,
    boolean isStacked,
    boolean isCentered,
    boolean isRotated,
    ChartFont font,
    ChartColor background)
    throws ChartException
  {

    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final int dimensions = cube.getDimensionCount();
    if (dimensions < 1 || dimensions > 2) {
      throw new ChartException("Bar contents can only display 1. or 2-dimensional data");
    }

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final ChartColor colorUp = getColorUp();
    final ChartColor colorDown = getColorDown();

    final ContentAnimation anim = getAnimation();

    final double barWidthPercent = getBarWidth();
    final double barSpacing = getBarSpacing();
    double barPadding = ((xAxis.getSampleWidth() * (1 - barWidthPercent)) / 2);

    final int size0 = cube.getSampleCount(0);
    final int size1 = Math.max(cube.getSampleCount(1), 1);
    final int visibleSize1 = Math.max(cube.getVisibleSampleCount(1), 1);

    final double xShadowOffset = getShadowXOffset();
    final double yShadowOffset = getShadowYOffset();

    x = anim.getX(progress, x, width, height);
    y = anim.getY(progress, y, width, height);

    // calc bar width
    final double sampleWidth = xAxis.getSampleWidth();
    double barWidth = sampleWidth - (2 * barPadding);
    if (!isStacked) {
      barWidth -= (visibleSize1 - 1) * barSpacing;
      barWidth /= visibleSize1;
      barPadding =  (xAxis.getSampleWidth() - (visibleSize1 * barWidth) - (visibleSize1 - 1) * barSpacing) / 2;
    }

    double realBarWidth = barWidth;
    realBarWidth -= realBarWidth % 2;
    realBarWidth = Math.max(realBarWidth, 1);

    // init regression
    final SimpleRegression regression = initRegression();

    for (int pass = 0; pass < 3; pass++) {
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }
        double bar = (double) c0 / (double) (size0 - 1);
        if (size0 <= 1) {
          bar = 0;
        }

        double xx = xAxis.getSamplePosition(cube, c0) + barPadding;
        if (!isCentered) {
          xx -= sampleWidth / 2;
        }

        double avgY = 0, avgCount = 0;
        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }

          // colors
          ChartColor sampleColor =
            getSampleColor(progress, bar, anim, data, c1, c0, isMultiColor());
          final ChartColor outlineColor = getOutlineColor(progress, bar, anim, data, c1, c0);
          final ChartColor shadowColor = getCurrentShadow(anim, progress, bar, data, c1, c0);

          final String format = yAxis.getFormat();
          Double lowerValue = cube.get(getLowerMeasure(), c0, c1);
          final Double upperValue = cube.get(getMeasure(), c0, c1);
          if (upperValue != null) {
            if (lowerValue == null) {
              lowerValue = 0.0;
            }

            // find lower and higher y
            final double y0 = yAxis.getPosition(anim.getValue(progress, bar, lowerValue));
            final double y1 = yAxis.getPosition(anim.getValue(progress, bar, upperValue));

            final double by = Math.min(y0, y1);
            final double bh = Math.abs(y0 - y1);

            // use specific color?
            boolean newColor = false;
            if (y1 < y0) {
              if (colorUp != null) {
                sampleColor = colorUp;
                newColor = true;
              }
            } else {
              if (colorDown != null) {
                sampleColor = colorDown;
                newColor = true;
              }
            }

            if (newColor) {
              sampleColor = anim.getSampleColor(progress, bar, sampleColor);
              sampleColor = changeSelectedColor(sampleColor, data, 1, c1);
            }

            // get min / max
            Double yMin = null;
            if (getMinMeasure() != null) {
              final Double minValue = cube.get(getMinMeasure(), c0, c1);
              if (minValue != null) {
                yMin = yAxis.getPosition(anim.getValue(progress, bar, minValue));
              }
            }

            Double yMax = null;
            if (getMaxMeasure() != null) {
              final Double maxValue = cube.get(getMaxMeasure(), c0, c1);
              if (maxValue != null) {
                yMax = yAxis.getPosition(anim.getValue(progress, bar, maxValue));
              }
            }

            // labels
            boolean isLastValue = true;
            if (isStacked) {
              for (int n = c1 + 1; n < size1; n++) {
                final Double o = cube.get(getMeasure(), c0, n);
                if ((o != null) && (o * upperValue >= 0)) {
                  isLastValue = false;
                }
              }
            }

            // add to regression
            if (isLastValue) {
              avgY += by;
              avgCount++;
            }

            // bar position
            final double rectX, rectY, rectWidth, rectHeight;
            if (isRotated) {
              rectX = x + by;
              rectY = y + xx;
              rectWidth = bh;
              rectHeight = realBarWidth;
            } else {
              rectX = x + xx;
              rectY = y + by;
              rectWidth = realBarWidth;
              rectHeight = bh;
            }

            switch (pass) {
              case 0:
                // shadow
                if (shadowColor != null) {
                  r.setColor(shadowColor);
                  r.fillRect(rectX + xShadowOffset, rectY + yShadowOffset, rectWidth, rectHeight);
                }
                break;

              case 1:
                // used color (outline for very small bars)
/*                if ((realBarWidth <= 2) && (outlineColor != null)) {
                  r.setColor(outlineColor);
                } else {
                  r.setColor(sampleColor);
                }
*/
                // draw body
                r.fillRect(rectX, rectY, rectWidth, rectHeight);

                // shine effect
                double shine = getShine();
                double shineSize = shine;
                if (shine <= 1) {
                  shineSize = ((realBarWidth / 2.0) * shine);
                }
                shine = Math.min(shine, realBarWidth / 2.0);

                final ChartColor cA =
                  anim.getShadowColor(progress, bar, ChartColor.WHITE.setOpacity(0.2));
                final ChartColor cB =
                  anim.getShadowColor(progress, bar, ChartColor.BLACK.setOpacity(0.05));
                for (int n = 0; n <= shineSize; n++) {
                  if (isRotated) {
                    r.setColor(cA);
                    r.fillRect(x + by, y + xx, bh, n);
                    r.setColor(cB);
                    r.fillRect(x + by, y + xx + realBarWidth - n, bh, n);
                  } else {
                    r.setColor(cA);
                    r.fillRect(x + xx, y + by, n, bh);
                    r.setColor(cB);
                    r.fillRect(x + xx + realBarWidth - n, y + by, n, bh);
                  }
                }

                // HI / LO
                if (outlineColor != null) {
                  r.setColor(outlineColor);
                } else {
                  r.setColor(sampleColor);
                }

                final double xc = xx + realBarWidth / 2;

                if (yMax != null) {
                  if (isRotated) {
                    r.drawLine(x + by + bh, y + xc, x + yMax, y + xc);
                  } else {
                    r.drawLine(x + xc, y + yMax, x + xc, y + by);
                  }
                }

                if (yMin != null) {
                  if (isRotated) {
                    r.drawLine(x + yMin, y + xc, x + by, y + xc);
                  } else {
                    r.drawLine(x + xc, y + by + bh, x + xc, y + yMin);
                  }
                }

                // outline
                if ((outlineColor) != null && (realBarWidth > 2)) {
                  r.setColor(outlineColor);
                  r.drawRect(rectX, rectY, rectWidth, rectHeight);
                }

                break;

              case 2:
                if (progress >= 1) {

                  // value label
                  final String text;
                  if (isStacked) {
                    text = r.format(format, upperValue - lowerValue);
                  } else {
                    text = r.format(format, upperValue);
                  }

                  // add floating label
                  final int anchor;
                  if (isRotated) {
                    if (upperValue >= 0) {
                      anchor = Renderer.EAST;
                    } else {
                      anchor = Renderer.WEST;
                    }
                  } else {
                    if (upperValue >= 0) {
                      anchor = Renderer.NORTH;
                    } else {
                      anchor = Renderer.SOUTH;
                    }
                  }

                  // add popup
                  if (isShowPopup()) {
                    final String popupText = buildPopupText(cube, c0, c1, text);
                    final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);
                    r.addPopup(
                      rectX, rectY,
                      rectWidth, rectHeight,
                      0,
                      anchor,
                      popupText,
                      getPopupFont(),
                      linkCommands[0], linkCommands[1], linkCommands[2]
                    );
                  }

                  final boolean isSelected = data.isSelected(1, c1);
                  if (isLastValue || isSelected) {
                    final String label = buildLabelText(cube, c0, c1, text, isSelected);
                    if (label != null) {
                      final ChartColor color1 = anim.getLabelColor(progress, bar, getLabelColor());
                      ChartColor color2 = getOutline();
                      if (color2 == null) {
                        color2 = color1;
                      }
                      r.setFont(labelFont);

                      LabelDrawer.drawInsideOutside(
                        r,
                        rectX, rectY,
                        rectHeight, rectWidth,
                        getLabelSpacing(),
                        anchor,
                        getLabelAngle(),
                        color1, color2,
                        text, getValueLabelType()
                      );
                    }
                  }
                }
            }
          }

          if (!isStacked) {
            xx += barWidth + barSpacing;
          }
        }

        if (regression != null) {
          regression.addData(xx, avgY / avgCount);
        }

        if (isStacked) {
          xx += barWidth;
        }

      }
    }

    drawRegression(r, x, y, width, height, isRotated, regression);
    drawAnnotations(r, xAxis, yAxis, x, y, width, height, isCentered);
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
    axis.addMeasures(getMeasure());
    axis.addMeasures(getLowerMeasure());
    axis.addMeasures(getMaxMeasure());
    axis.addMeasures(getMinMeasure());
  }
}
