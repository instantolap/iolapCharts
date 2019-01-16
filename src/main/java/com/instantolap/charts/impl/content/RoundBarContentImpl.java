package com.instantolap.charts.impl.content;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.FadeInContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.util.RoundLabelDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class RoundBarContentImpl extends BasicBarContentImpl
  implements RoundContent, SampleValueRenderer {
  private boolean isRound = true;

  public RoundBarContentImpl(Theme theme) {
    super(theme);

    setBarSpacing(2);
    setAnimation(new FadeInContentAnim());
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return true;
  }

  @Override
  public void setRound(boolean isRound) {
    this.isRound = isRound;
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
      throw new ChartException("Bar contents can only display 1. or 2-dimensional data");
    }

    final ContentAnimation anim = getAnimation();

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final double cx = x + (width / 2);
    final double cy = y + (height / 2);

    final double barWidth = getBarWidth();
    final boolean round = isRound();

    final String format = yAxis.getFormat();

    final int size0 = cube.getSampleCount(0);
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;
    final int visibleSize1 = (dimensions >= 2) ? cube.getVisibleSampleCount(1) : 1;

    final double xOffset = getShadowXOffset();
    final double yOffset = getShadowYOffset();

    final RoundLabelDrawer labelDrawer = new RoundLabelDrawer(r, cx, cy, 10);

    // draw bars
    for (int pass = 0; pass < 3; pass++) {
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }
        final double progress0 = (double) c0 / (double) (size0 - 1);

        double rad1 = getRad(startAngle, endAngle, c0, size0, progress);
        double rad2 = getRad(startAngle, endAngle, c0 + 1, size0, progress);

        final double step = rad2 - rad1;
        double barSize = (rad2 - rad1) * barWidth;
        if (!isStacked) {
          barSize /= visibleSize1;
        }
        final double padding = (step * (1 - barWidth)) / 2.0;
        rad1 += padding;

        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }

          // colors
          final ChartColor sampleColor =
            getSampleColor(progress, progress0, anim, data, c1, c0, isMultiColor() && (size1 == 1));
          final ChartColor outlineColor = getOutlineColor(progress, progress0, anim, data, c1, c0);
          final ChartColor shadowColor = getCurrentShadow(anim, progress, progress0, data, c1, c0);

          Double minValue = cube.get(Cube.MEASURE_LOWER, c0, c1);
          final Double maxValue = cube.get(Cube.MEASURE_VALUE, c0, c1);
          if (maxValue != null) {
            if (minValue == null) {
              minValue = 0.0;
            }

            final double len0 = Math.max(yAxis.getRadius(minValue), 0);
            final double len1 = yAxis.getRadius(maxValue);
            rad2 = rad1 + barSize;

            switch (pass) {
              case 0:
                // shadow
                if (shadowColor != null) {
                  r.setColor(shadowColor);
                  r.fillDonut(cx + xOffset, cy + yOffset, len0, len1, rad1, rad2, round);
                }
                break;

              case 1:
                // bar
                r.setColor(sampleColor);
                r.fillDonut(cx, cy, len0, len1, rad1, rad2, round);

                // outline
                if (outlineColor != null) {
                  r.setColor(outlineColor);
                  r.drawDonut(cx, cy, len0, len1, rad1, rad2, round);
                }
                break;

              case 2:
                // labels
                final String text = r.format(format, maxValue);

                // popup
                final String popupText = buildPopupText(cube, c0, c1, text);
                final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);
                r.addPopup(cx, cy, len0, len1, rad1, rad2, round,
                  popupText, getPopupFont(), linkCommands[0],
                  linkCommands[1], linkCommands[2]
                );

                // only display the top value for stacked
                // charts
                boolean isLastValue = true;
                if (isStacked) {
                  for (int n = c1 + 1; n < size1; n++) {
                    final Double o = cube.get(Cube.MEASURE_VALUE, c0, n);
                    if ((o != null) && (o * maxValue >= 0)) {
                      isLastValue = false;
                    }
                  }
                }

                final boolean isSelected = data.isSelected(1, c1);
                if (isLastValue || isSelected) {
                  // label
                  final String label = buildLabelText(cube, c0, c1, text, isSelected);
                  if (label != null) {

                    final ChartColor color1 =
                      anim.getLabelColor(progress, progress0, getLabelColor());
                    ChartColor color2 = color1;
                    if (sampleColor.getLuminance() <= 0.5) {
                      color2 = ChartColor.WHITE;
                    }

                    r.setFont(labelFont);
                    labelDrawer.add(
                      len0, len1, rad1, rad2, color1, color2, label, getValueLabelType()
                    );
                  }
                }
            }

            if (!isStacked) {
              rad1 += barSize;
            }
          }
        }
      }
    }

    labelDrawer.render();
  }

  @Override
  public boolean isRound() {
    return isRound;
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
    axis.addMeasures(getMeasure());
    axis.addMeasures(getLowerMeasure());
    axis.addMeasures(getMaxMeasure());
    axis.addMeasures(getMinMeasure());
  }


}
