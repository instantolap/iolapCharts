package com.instantolap.charts.impl.content;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.FlyInContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.util.RoundLabelDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;

import java.util.List;


public class PieContentImpl extends BasicPieContentImpl implements SampleValueRenderer {

  public PieContentImpl(Theme theme) {
    super(theme);

    setOutline(new ChartColor("666"));
    setShine(5);
    setAnimation(new FlyInContentAnim());
    setPercentLabelFormat("0%");
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return true;
  }

  @Override
  public boolean needsSampleLegend() {
    return true;
  }

  @Override
  public void render(double progress, Renderer r, Data data, int x, int y,
    int width, int height, PositionAxis xAxis, ValueAxis yAxis,
    boolean isStacked, boolean isCentered, boolean isRotated,
    ChartFont font, ChartColor background) throws ChartException
  {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final RoundAxis roundAxis = (RoundAxis) yAxis;
    final double startAngle = roundAxis.getStartAngle();
    final double endAngle = roundAxis.getStopAngle();

    final int dimensions = cube.getDimensionCount();
    if (dimensions < 1 || dimensions > 2) {
      throw new ChartException("Pie contents can only display 1. or 2-dimensional data");
    }

    final ContentAnimation anim = getAnimation();
    final double seriesSpacing = getSeriesSpace();
    final double detachedDistance = getDetachedDistance();
    final boolean round = isRound();
    final List<Integer> detachedSamples = getDetachedSamples();

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final int xOffset = getShadowXOffset();
    final int yOffset = getShadowYOffset();

    final int cx = x + width / 2;
    final int cy = y + height / 2;
    final int radius = xAxis.getSize();
    int size = radius - Math.max(xOffset, yOffset);

    if (isShowSampleLabels()
      || isShowSeriesLabels()
      || isShowValueLabels()
      || !detachedSamples.isEmpty())
    {
      size -= 20;
    }

    final RoundLabelDrawer labelDrawer = new RoundLabelDrawer(r, cx, cy, 10);
    final String format = yAxis.getFormat();

    final int size0 = cube.getSampleCount(0);
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    // draw slices
    for (int pass = 0; pass < 3; pass++) {
      for (int c1 = 0; c1 < size1; c1++) {
        if (!cube.isVisible(1, c1)) {
          continue;
        }

        // inner/outer bounds
        final double seriesSize = (double) size / size1;
        final double base = seriesSize * c1;
        final int len0 = (int) (base + seriesSpacing * seriesSize);
        final int len1 = (int) (base + seriesSize);

        // calc total
        double total = 0;
        for (int c0 = 0; c0 < size0; c0++) {
          if (!cube.isVisible(0, c0)) {
            continue;
          }

          final Double v = cube.get(getMeasure(), c0, c1);
          if ((v != null) && (v >= 0)) {
            total += v;
          }
        }

        double rad = startAngle;
        for (int c0 = 0; c0 < size0; c0++) {
          if (!cube.isVisible(0, c0)) {
            continue;
          }

          final double bar = (double) c0 / (size0 - 1);

          // colors
          final ChartColor sampleColor = getSampleColor(progress, bar, anim, data, c1, c0, true);
          final ChartColor outlineColor = getOutlineColor(progress, bar, anim, data, c1, c0);
          final ChartColor shadowColor = getCurrentShadow(anim, progress, bar, data, c1, c0);

          final Double v = cube.get(getMeasure(), c0, c1);
          if ((v != null) && (v >= 0)) {
            final double f = v / total;
            final double arc = f * (endAngle - startAngle);
            final double rad2 = rad + arc;

            // calc (animated) distance
            double animDistance = anim.getDistance(progress, bar, 0);
            if (detachedSamples.contains(c0)) {
              animDistance = detachedDistance;
            }

            final double c = (rad + rad2) / 2;
            double distance = seriesSize * animDistance;
            if (Double.isNaN(distance)) {
              distance = 0;
            }
            final int xx = (int) (cx + distance * Math.sin(c));
            final int yy = (int) (cy - distance * Math.cos(c));

            // render
            switch (pass) {
              case 0:
                // shadow
                if (shadowColor != null) {
                  r.setColor(shadowColor);
                  r.fillDonut(xx + xOffset, yy + yOffset, len0, len1, rad, rad2, round);
                }
                break;

              case 1:
                // slice
                r.setColor(sampleColor);
                r.fillDonut(xx, yy, len0, len1, rad, rad2, round);

                // shine effect
                final double shine = getShine();
                int shineSize = (int) shine;
                if (shine <= 1) {
                  shineSize = (int) (((len1 - len0) / 2.0) * shine);
                }

                ChartColor color1 = ChartColor.WHITE.setOpacity(0.2);
                ChartColor color2 = ChartColor.BLACK.setOpacity(0.05);
                for (int n = 0; n <= shineSize; n++) {
                  r.setColor(color1);
                  r.fillDonut(xx, yy, len1 - n, len1, rad, rad2, round);
                  if (len0 > 0) {
                    r.setColor(color2);
                    r.fillDonut(xx, yy, len0, len0 + n, rad, rad2, round);
                  }
                }

                // outline
                if (outlineColor != null) {
                  r.setColor(outlineColor);
                  r.drawDonut(xx, yy, len0, len1, rad, rad2, round);
                }
                break;

              case 2:
                if (progress >= 1) {
                  final String text = r.format(format, v);

                  // popup
                  final String popupText = buildPopupText(cube, c0, c1, text, v / total, r);
                  final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);
                  r.addPopup(xx, yy, len0, len1, rad, rad2,
                    round, popupText, getPopupFont(),
                    linkCommands[0], linkCommands[1],
                    linkCommands[2]
                  );

                  // value label
                  final boolean isSelected = data.isSelected(1, c1);
                  final String label = buildLabelText(cube, c0, c1, text, isSelected, v / total, r);
                  if (label != null) {
                    color1 = anim.getLabelColor(progress, bar, getLabelColor());
                    color2 = color1;
                    if (sampleColor.getLuminance() <= 0.5) {
                      color2 = ChartColor.WHITE;
                    }

                    r.setFont(labelFont);
                    labelDrawer.add((int) (len0 + distance),
                      (int) (len1 + distance), rad, rad2,
                      color1, color2, label,
                      getValueLabelType()
                    );
                  }
                }

                break;
            }

            rad += arc;
          }
        }
      }
    }

    labelDrawer.render();
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
    axis.addMeasures(getMeasure());
  }

}
