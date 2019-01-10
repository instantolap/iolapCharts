package com.instantolap.charts.impl.content;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.PendulumContentAnim;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class MeterContentImpl extends BasicMeterContentImpl implements SampleValueRenderer {

  public MeterContentImpl(Palette palette) {
    super(palette);

    setAnimation(new PendulumContentAnim());
    setLabelFont(getPalette().getTitleFont());
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

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final int xOffset = getShadowXOffset();
    final int yOffset = getShadowYOffset();

    final int cx = x + width / 2;
    final int cy = y + height / 2;
    final int radius = Math.min(width / 2, height / 2);
    final int size = radius - Math.max(xOffset, yOffset);

    final int size0 = cube.getSampleCount(0);
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    final int pinSize = (int) (radius * getPinSize());
    int labelY = cy + radius / 2;

    // draw slices
    ChartColor latestColor = null;
    for (int pass = 0; pass < 2; pass++) {
      for (int c1 = 0; c1 < size1; c1++) {
        if (!cube.isVisible(1, c1)) {
          continue;
        }

        for (int c0 = 0; c0 < size0; c0++) {
          if (!cube.isVisible(0, c0)) {
            continue;
          }

          Double v = cube.get(getMeasure(), c0, c1);
          if (v != null) {
            final double bar = (double) c0 / (size0 - 1);
            v = anim.getValue(progress, bar, v);

            switch (pass) {
              case 0:
                final String text = r.format(yAxis.getFormat(), v);
                final String label = buildLabelText(cube, c0, c1, text, false);
                if (label != null) {
                  final ChartColor color1 = anim.getLabelColor(
                    progress, bar, getLabelColor());
                  r.setColor(color1);
                  r.setFont(labelFont);

                  r.drawText(cx, labelY, label, 0, Renderer.CENTER);
                  labelY += r.getTextHeight(text);
                }
                break;

              // draw pointers
              case 1:
                final ChartColor sampleColor =
                  getSampleColor(progress, bar, anim, data, c1, c0, true);
                final ChartColor outlineColor = getOutlineColor(progress, bar, anim, data, c1, c0);
                final ChartColor shadowColor = getCurrentShadow(anim, progress, bar, data, c1, c0);

                final double f = (double) yAxis.getPosition(v) / yAxis.getSize();
                final double arc = (endAngle - startAngle) * f + startAngle;
                if (arc < startAngle || arc > endAngle) {
                  continue;
                }

                final int tx = (int) (cx + Math.sin(arc) * size);
                final int ty = (int) (cy - Math.cos(arc) * size);

                final double arc0 = arc - Math.PI / 2;
                final double arc1 = arc + Math.PI / 2;
                final double oppSize = -size * 0.15;

                final int x0 = (int) (cx + Math.sin(arc0) * pinSize);
                final int y0 = (int) (cy - Math.cos(arc0) * pinSize);
                final int x1 = (int) (cx + Math.sin(arc1) * pinSize);
                final int y1 = (int) (cy - Math.cos(arc1) * pinSize);
                final int xOpp = (int) (cx + Math.sin(arc) * oppSize);
                final int yOpp = (int) (cy - Math.cos(arc) * oppSize);

                final int[] xx = new int[]{xOpp, x0, tx, x1};
                final int[] yy = new int[]{yOpp, y0, ty, y1};

                // shadow
                if (shadowColor != null) {
                  r.setColor(shadowColor);
                  r.fillPolygon(shift(xx, getShadowXOffset()), shift(yy, getShadowYOffset()));
                }

                r.setColor(sampleColor);
                r.fillPolygon(xx, yy);
                latestColor = sampleColor;

                if (outlineColor != null) {
                  r.setColor(outlineColor);
                  r.drawPolygon(xx, yy);
                }
                break;
            }
          }
        }
      }
    }

    final ChartColor pinColor = getPinColor();
    if (pinColor != null) {
      latestColor = pinColor;
    }

    if (latestColor != null) {
      r.setColor(latestColor);
      r.fillCircle(cx - pinSize, cy - pinSize, pinSize * 2);
    }

    final ChartColor outline = getOutline();
    if (outline != null) {
      r.setColor(outline);
      r.drawCircle(cx - pinSize, cy - pinSize, pinSize * 2);
    }
  }

  @Override
  public void addMeasuresToAxes(ValueAxis axis) {
    axis.addMeasures(getMeasure());
  }

}
