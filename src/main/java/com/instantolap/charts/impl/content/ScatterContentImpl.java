package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.ScaleAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.FadeInContentAnim;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.impl.math.SimpleRegression;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class ScatterContentImpl extends BasicScatterContentImpl implements ValueValueRenderer {

  public ScatterContentImpl(Palette palette) {
    super(palette);

    setAnimation(new FadeInContentAnim());
    setShadow(null);
  }

  @Override
  public void render(double progress, Renderer r, Data data, int x, int y,
    int width, int height, ScaleAxis xAxis, ValueAxis yAxis,
    ChartFont font, ChartColor background) throws ChartException
  {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final int dimensions = cube.getDimensionCount();
    if (dimensions > 2) {
      throw new ChartException("Scatter contents can only display 0, 1- or 2-dimensional data");
    }

    final ContentAnimation anim = getAnimation();

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final String format = yAxis.getFormat();

    final int size0 = (dimensions >= 1) ? cube.getSampleCount(0) : 1;
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    final int xOffset = getShadowXOffset();
    final int yOffset = getShadowYOffset();

    final double maxVZ = getMaxVZ(cube);

    // init regression
    SimpleRegression regression = null;
    final ChartColor regressionColor = getRegressionColor();
    if (regressionColor != null) {
      regression = new SimpleRegression();
    }

    // draw symbols
    for (int pass = 0; pass < 2; pass++) {
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }
        final double progress0 = (double) c0 / (double) (size0 - 1);

        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }

          // colors
          final ChartColor sampleColor =
            getSampleColor(progress, progress0, anim, data, c1, c0, false);
          final ChartColor outlineColor = getOutlineColor(progress, progress0, anim, data, c1, c0);
          final ChartColor shadowColor = getCurrentShadow(anim, progress, progress0, data, c1, c0);

          final Double vx = cube.get(getXMeasure(), c0, c1);
          final Double vy = cube.get(getYMeasure(), c0, c1);

          int symbolWidth = data.getSymbolSize(c1);
          if (isBubble()) {
            final Double vz = cube.get(getMeasure(), c0, c1);
            if ((vz == null) || (vz <= 0)) {
              continue;
            }
            symbolWidth *= (vz / maxVZ);
          }

          if (vx != null && vy != null) {

            final int xx = xAxis.getPosition(vx);
            final int yy = yAxis.getPosition(vy);

            if (regression != null) {
              regression.addData(xx, yy);
            }

            switch (pass) {
              // shadow
              case 0:
                final int symbol = data.getSymbol(c1);

                if (shadowColor != null) {
                  SymbolDrawer.draw(r, symbol, x + xx + xOffset, y + yy + yOffset, symbolWidth,
                    shadowColor, shadowColor, background
                  );
                }

                SymbolDrawer.draw(r, symbol, x + xx, y + yy, symbolWidth, sampleColor, outlineColor,
                  background
                );
                break;

              case 1:
                if (progress >= 1) {
                  final String text = r.format(format, vx) + " / " + r.format(format, vy);
                  final String popup = buildPopupText(cube, c0, c1, text);
                  final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);

                  r.addPopup(x + xx, y + yy, symbolWidth, symbolWidth, 0, Renderer.WEST, popup,
                    getPopupFont(), linkCommands[0], linkCommands[1], linkCommands[2]
                  );

                  // labels
                  final String label = buildLabelText(cube, c0, c1, text, data.isSelected(1, c1));
                  if (label != null) {
                    r.setColor(anim.getLabelColor(progress, progress0, getLabelColor()));

                    r.setFont(labelFont);
                    r.drawText(x + xx + symbolWidth / 2 + 2, y + yy, label, 0, Renderer.WEST);
                  }
                }
                break;
            }
          }
        }
      }
    }

    // draw regression line?
    if (regression != null) {
      final int y0 = (int) regression.predict(x);
      final int y1 = (int) regression.predict(x + width);
      r.setColor(regressionColor);
      r.setStroke(getRegressionStroke());
      r.drawLine(x, y + y0, x + width, y + y1);
    }
  }

  private double getMaxVZ(Cube cube) {
    double maxVZ = 0;
    if (isBubble()) {
      final int size0 = cube.getSampleCount(0);
      final int size1 = Math.max(cube.getSampleCount(1), 1);
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }

        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }

          final Double vz = cube.get(getMeasure(), c0, c1);
          if (vz == null) {
            continue;
          }

          maxVZ = Math.max(maxVZ, vz);
        }
      }
    }
    return maxVZ;
  }

  @Override
  public void addMeasuresToAxes(ScaleAxis xAxis, ScaleAxis yAxis) {
    xAxis.addMeasures(getXMeasure());
    yAxis.addMeasures(getYMeasure());
  }
}
