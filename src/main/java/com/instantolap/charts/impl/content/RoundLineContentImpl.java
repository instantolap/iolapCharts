package com.instantolap.charts.impl.content;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.FadeInContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.util.RoundLabelDrawer;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.*;


public class RoundLineContentImpl extends BasicLineContentImpl implements SampleValueRenderer {

  public RoundLineContentImpl(Theme theme) {
    super(theme);

    setAnimation(new FadeInContentAnim());
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return false;
  }

  @Override
  public void render(double progress, Renderer r, Data data, double x, double y,
                     double width, double height, PositionAxis xAxis, ValueAxis yAxis,
                     boolean isStacked, boolean isCentered, boolean isRotated,
                     ChartFont font, ChartColor background) throws ChartException {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final RoundAxis roundAxis = (RoundAxis) xAxis;
    final double startAngle = roundAxis.getStartAngle();
    final double endAngle = roundAxis.getStopAngle();

    final int dimensions = cube.getDimensionCount();
    if (dimensions < 1 || dimensions > 2) {
      throw new ChartException("Line contents can only display 1. or 2-dimensional data");
    }

    final ContentAnimation anim = getAnimation();
    final String format = yAxis.getFormat();

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final double cx = x + width / 2;
    final double cy = y + height / 2;

    // draw lines
    final int size0 = cube.getSampleCount(0);
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    final double xOffset = getShadowXOffset();
    final double yOffset = getShadowYOffset();
    final boolean isAreaChart = isAreaChart();

    final RoundLabelDrawer labelDrawer = new RoundLabelDrawer(r, cx, cy, 10);

    for (int pass = 0; pass < 5; pass++) {
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }
        final double progress0 = (double) c0 / (size0 - 1);

        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }
          final int symbolSize = data.getSymbolSize(c1);

          // colors
          final ChartColor sampleColor =
            getSampleColor(progress, progress0, anim, data, c1, c0, false);
          final ChartColor areaColor =
            getAreaColor(progress, progress0, anim, data, getColorRange(), c1, c0);
          final ChartColor outlineColor =
            getOutlineColor(progress, progress0, anim, data, c1, c0);
          final ChartColor shadowColor =
            getCurrentShadow(anim, progress, progress0, data, c1, c0);

          Double minValue = cube.get(getLowerMeasure(), c0, c1);
          final Double maxValue = cube.get(getMeasure(), c0, c1);
          if (isValid(maxValue)) {
            if (minValue == null) {
              minValue = 0.0;
            }

            // coordinates
            final double len = yAxis.getRadius(anim.getValue(progress, progress0, maxValue));
            final double rad =
              getRad(startAngle, endAngle, c0 + (isCentered ? 0.5 : 0), size0, progress);
            final double xx = Math.sin(rad) * len;
            final double yy = -Math.cos(rad) * len;

            final double len0 = yAxis.getRadius(minValue);
            final double xx0 = Math.sin(rad) * len0;
            final double yy0 = -Math.cos(rad) * len0;

            // draw line?
            final int prevN = (c0 + size0 - 1) % size0;
            Double prevMinValue = cube.get(getLowerMeasure(), prevN, c1);
            final Double prevMaxValue = cube.get(getMeasure(), prevN, c1);
            if (isValid(prevMaxValue)) {
              if (prevMinValue == null) {
                prevMinValue = 0.0;
              }

              // previous coordinates
              final double plen = yAxis.getRadius(anim.getValue(progress, progress0, prevMaxValue));
              final double prad =
                getRad(startAngle, endAngle, prevN + (isCentered ? 0.5 : 0), size0, progress
                );
              final double xp = Math.sin(prad) * plen;
              final double yp = -Math.cos(prad) * plen;

              final double prevLen0 = yAxis.getRadius(prevMinValue);
              final double xp0 = Math.sin(prad) * prevLen0;
              final double yp0 = -Math.cos(prad) * prevLen0;

              final ChartStroke stroke = data.getStroke(c1);

              switch (pass) {
                case 0:
                  if (isAreaChart) {
                    if (maxValue * prevMaxValue >= 0.0) {

                      final double[] ax = new double[]{xp, xx, xx0, xp0};
                      final double[] ay = new double[]{yp, yy, yy0, yp0};

                      r.setColor(areaColor);
                      r.fillPolygon(shift(ax, cx), shift(ay, cy));
                    }
                  }
                  break;

                case 1:
                  if (stroke.getWidth() > 0) {
                    // shadow
                    if (shadowColor != null) {
                      r.setStroke(stroke);
                      r.setColor(shadowColor);
                      r.drawLine(cx + xp + xOffset, cy + yp + yOffset, cx + xx + xOffset,
                        cy + yy + yOffset
                      );
                    }

                    // outline
                    if (outlineColor != null) {
                      r.setColor(outlineColor);
                      r.setStroke(stroke.incStroke(2));
                      r.drawLine(cx + xp, cy + yp, cx + xx, cy + yy);
                    }
                  }
                  break;

                case 3:
                  if (stroke.getWidth() > 0) {
                    // line
                    r.setStroke(stroke);
                    r.setColor(sampleColor);
                    r.drawLine(cx + xp, cy + yp, cx + xx, cy + yy);
                    r.resetStroke();
                  }
                  break;
              }
            }

            switch (pass) {
              case 3:
                if (symbolSize > 0) {
                  // draw symbol
                  if (shadowColor != null) {
                    SymbolDrawer.draw(r, data.getSymbol(c1), cx
                        + xx + xOffset, cy + yOffset + yy,
                      symbolSize, shadowColor,
                      shadowColor, background
                    );
                  }

                  SymbolDrawer.draw(r, data.getSymbol(c1), cx
                      + xx, cy + yy, symbolSize, sampleColor,
                    outlineColor, background
                  );
                }
                break;

              case 4:
                if (progress >= 1) {
                  final String text = r.format(format, maxValue);

                  // popups
                  final String popupText = buildPopupText(cube, c0, c1, text);
                  final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);
                  r.addPopup(cx + xx - symbolSize / 2, cy + yy - symbolSize / 2, symbolSize,
                    symbolSize, getLabelAngle(), Renderer.CENTER, popupText,
                    getPopupFont(), linkCommands[0], linkCommands[1], linkCommands[2]
                  );

                  // only display the top value for stacked
                  // charts
                  boolean isLastValue = true;
                  if (isStacked) {
                    for (int n = c1 + 1; n < size1; n++) {
                      final Double o = cube.get(getMeasure(), c0, n);
                      if (isValid(o) && (o * maxValue >= 0)) {
                        isLastValue = false;
                      }
                    }
                  }

                  final boolean isSelected = data.isSelected(1, c1);
                  if (isLastValue || isSelected) {

                    // labels
                    final String label = buildLabelText(cube, c0, c1, text, isSelected);
                    if (label != null) {
                      final ChartColor color1 =
                        anim.getLabelColor(progress, progress0, getLabelColor());
                      ChartColor color2 = color1;
                      if (sampleColor.getLuminance() <= 0.5) {
                        color2 = ChartColor.WHITE;
                      }

                      r.setFont(labelFont);
                      labelDrawer.add(len, rad, color1, color2, label, getValueLabelType());
                    }
                  }
                }
                break;
            }
          }
        }
      }
    }

    labelDrawer.render();
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
    axis.addMeasures(getMeasure());
    axis.addMeasures(getLowerMeasure());
  }
}
