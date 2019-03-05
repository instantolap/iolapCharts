package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.ScaleAxis;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.ControllableFadeContentAnimation;
import com.instantolap.charts.impl.animation.FadeInContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.math.SimpleRegression;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class ScatterContentImpl extends BasicScatterContentImpl implements ValueValueRenderer {

  public ScatterContentImpl(Theme theme) {
    super(theme);

    setAnimation(new FadeInContentAnim());
    setShadow(null);
  }

  @Override
  public void render(double progress, Renderer r, Data data, double x, double y,
                     double width, double height, ScaleAxis xAxis, ValueAxis yAxis,
                     ChartFont font, ChartColor background) throws ChartException {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final int dimensions = cube.getDimensionCount();
    if (dimensions > 2) {
      throw new ChartException("Scatter contents can only display 0, 1- or 2-dimensional data");
    }

    ContentAnimation anim = getAnimation();

    ChartFont labelFont = getLabelFont();
    if (labelFont == null) {
      labelFont = font;
    }

    final String format = yAxis.getFormat();

    final int size0 = (dimensions >= 1) ? cube.getSampleCount(0) : 1;
    final int size1 = (dimensions >= 2) ? cube.getSampleCount(1) : 1;

    final double xOffset = getShadowXOffset();
    final double yOffset = getShadowYOffset();

    final double maxVZ = getMaxVZ(cube);

    // init regression
    SimpleRegression[] regressions = null;
    final ChartColor regressionColor = getRegressionColor();
    if (regressionColor != null) {
      regressions = new SimpleRegression[size1];
      for (int n = 0; n < size1; n++) {
        regressions[n] = new SimpleRegression();
      }
    }

    // time animation?
    final ControllableFadeContentAnimation fadeAnimation = new ControllableFadeContentAnimation();
    final Double visibleTime = (getTimeWindow() > 0) ? (double) getTimeWindow() * 1000L : null;
    final Double minTime = cube.getMin(Cube.MEASURE_TIME);
    final Double maxTime = cube.getMax(Cube.MEASURE_TIME);
    Double currentTime = null;
    if (minTime != null && maxTime != null && visibleTime != null) {
      anim = fadeAnimation;
      final double timeDifference = maxTime - minTime;
      currentTime = minTime + timeDifference * progress;
    }

    final double minFade = getMinFade();

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

          Double timestamp = cube.get(Cube.MEASURE_TIME, c0, c1);
          boolean showOutline = true;
          boolean addToRegression = true;
          if (timestamp != null && currentTime != null) {
            if (timestamp > currentTime) {
              continue;
            } else if (timestamp < currentTime - visibleTime) {
              addToRegression = false;
            }

            // fade older values
            double fade = Math.pow(Math.min(1, Math.max(0, 1 - (currentTime - timestamp) / visibleTime)), 2);
            fade = Math.max(minFade, fade);

            // invisible? then skip
            if (fade <= 0) {
              continue;
            }
            fadeAnimation.setFade(fade);

            // if the symbol is faded too much, the outline is no longer necessary
            if (fade <= minFade) {
              showOutline = false;
            }
          }

          // colors
          final ChartColor sampleColor = getSampleColor(progress, progress0, anim, data, c1, c0, false);
          final ChartColor outlineColor = showOutline ? getOutlineColor(progress, progress0, anim, data, c1, c0) : null;
          final ChartColor shadowColor = showOutline ? getCurrentShadow(anim, progress, progress0, data, c1, c0) : null;

          final Double vx = cube.get(getXMeasure(), c0, c1);
          final Double vy = cube.get(getYMeasure(), c0, c1);

          double symbolWidth = data.getSymbolSize(c1);
          if (isBubble()) {
            final Double vz = cube.get(getMeasure(), c0, c1);
            if ((vz == null) || (vz <= 0)) {
              continue;
            }
            symbolWidth *= (vz / maxVZ);
            symbolWidth = Math.max(symbolWidth, 1d);
          }

          if (vx != null && vy != null) {

            final double xx = xAxis.getPosition(vx);
            final double yy = yAxis.getPosition(vy);

            if (regressions != null && addToRegression) {
              regressions[c1].addData(xx, yy);
            }

            switch (pass) {
              // shadow
              case 0:
                final int symbol = data.getSymbol(c1);

                if (shadowColor != null) {
                  SymbolDrawer.draw(
                    r, symbol,
                    x + xx + xOffset, y + yy + yOffset,
                    symbolWidth, shadowColor, shadowColor, background
                  );
                }

                SymbolDrawer.draw(
                  r, symbol,
                  x + xx, y + yy,
                  symbolWidth, sampleColor, outlineColor, background
                );
                break;

              case 1:
                /*
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
                    r.drawText(x + xx + symbolWidth / 2 + 2, y + yy, label, 0, Renderer.WEST, false);
                  }
                }
                break;
                */
            }
          }
        }
      }
    }

    // draw regression line?
    if (regressions != null) {
      for (int n = 0; n < regressions.length; n++) {
        final double y0 = regressions[n].predict(0);
        final double y1 = regressions[n].predict(width);
        r.setColor(regressionColor);
        r.setStroke(getRegressionStroke());
        r.drawLine(x, y + y0, x + width, y + y1);
      }
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
