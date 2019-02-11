package com.instantolap.charts.impl.content;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.PendulumContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class MeterContentImpl extends BasicMeterContentImpl implements SampleValueRenderer {

  public MeterContentImpl(Theme theme) {
    super(theme);

    setAnimation(new PendulumContentAnim());
    setLabelFont(getTheme().getTitleFont());
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
  public void render(double progress, Renderer r, Data data, double x, double y,
                     double width, double height, PositionAxis xAxis, ValueAxis yAxis,
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

    final double xOffset = getShadowXOffset();
    final double yOffset = getShadowYOffset();

    final double cx = x + width / 2;
    final double cy = y + height / 2;
    final double radius = Math.min(width / 2, height / 2);
    final double size = radius - Math.max(xOffset, yOffset);

    final int size0 = cube.getSampleCount(0);
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    final double pinSize = (radius * getPinSize());
    double labelY = cy + radius / 2;

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

                  r.drawText(cx, labelY, label, 0, Renderer.CENTER, false);
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

                final double tx = (cx + Math.sin(arc) * size);
                final double ty = (cy - Math.cos(arc) * size);

                final double arc0 = arc - Math.PI / 2;
                final double arc1 = arc + Math.PI / 2;
                final double oppSize = -size * 0.15;

                final double x0 =  (cx + Math.sin(arc0) * pinSize);
                final double y0 = (cy - Math.cos(arc0) * pinSize);
                final double x1 = (cx + Math.sin(arc1) * pinSize);
                final double y1 = (cy - Math.cos(arc1) * pinSize);
                final double xOpp = (cx + Math.sin(arc) * oppSize);
                final double yOpp = (cy - Math.cos(arc) * oppSize);

                final double[] xx = new double[]{xOpp, x0, tx, x1};
                final double[] yy = new double[]{yOpp, y0, ty, y1};

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
