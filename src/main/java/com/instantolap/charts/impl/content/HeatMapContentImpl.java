package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.LeftToRightContentAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.util.LabelDrawer;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class HeatMapContentImpl extends BasicHeatMapContentImpl implements SampleSampleRenderer {

  public HeatMapContentImpl(Theme theme) {
    super(theme);

    setAnimation(new LeftToRightContentAnim());
  }

  @Override
  public boolean needsCenteredSampleAxis() {
    return true;
  }

  @Override
  public void render(
    double progress,
    Renderer r,
    Data data, double x, double y,
    double width, double height,
    SampleAxis xAxis, SampleAxis yAxis,
    ChartFont font, ChartColor background) throws ChartException
  {
    final Cube cube = getCube();
    if (cube == null) {
      return;
    }

    final ContentAnimation anim = getAnimation();
    final ChartFont labelFont = getLabelFont();

    final double xOffset = getShadowXOffset();
    final double yOffset = getShadowYOffset();

    final int size0 = cube.getSampleCount(0);
    final int size1 = cube.getSampleCount(1);

    Double max = cube.getMax(Cube.MEASURE_VALUE);
    if (max == null) {
      max = 0.0;
    }

    final ChartColor[][] cellColors = getCellColors();

    for (int pass = 0; pass < 4; pass++) {
      for (int c0 = 0; c0 < size0; c0++) {
        if (!cube.isVisible(0, c0)) {
          continue;
        }
        final double xx = xAxis.getSamplePosition(cube, c0);
        final double cellWidth = xAxis.getSamplePosition(cube, c0 + 1) - xx;

        final double progress0 = (double) c0 / (double) (size0 - 1);
        for (int c1 = 0; c1 < size1; c1++) {
          if (!cube.isVisible(1, c1)) {
            continue;
          }
          final double yy = yAxis.getSamplePosition(cube, c1);
          final double cellHeight = yAxis.getSamplePosition(cube, c1 + 1) - yy;
          final double contentWidth = Math.min(cellWidth, cellHeight);
          final double cx = x + xx + cellWidth / 2;
          final double cy = y + yy + cellHeight / 2;

          final Double value = cube.get(Cube.MEASURE_VALUE, c0, c1);
          if (value != null) {
            final double p = value / max;

            final ChartColor sampleColor =
              getSampleColor(progress, progress0, anim, data, c1, c0, false);
            final ChartColor outlineColor =
              getOutlineColor(progress, progress0, anim, data, c1, c0);
            final ChartColor shadowColor =
              getCurrentShadow(anim, progress, progress0, data, c1, c0);
            final ChartColor autoColor =
              anim.getSampleColor(progress, progress0, getTargetColor(data, c1, p));

            switch (pass) {
              case 0:
                if (isFill() && (autoColor != null)) {
                  r.setColor(autoColor);
                  r.fillRect(x + xx, y + yy, cellWidth, cellHeight);
                } else if (cellColors != null) {
                  if (cellColors.length > c1 && cellColors[c1].length > c0) {
                    r.setColor(cellColors[c1][c0]);
                    r.fillRect(x + xx, y + yy, cellWidth, cellHeight);
                  }
                }
                break;

              case 1:
                if (isFill() && (autoColor != null)) {
                  r.setColor(ChartColor.BLACK);
                  r.drawRect(x + xx, y + yy, cellWidth, cellHeight);
                } else if (cellColors != null) {
                  if (cellColors.length > c1 && cellColors[c1].length > c0) {
                    r.setColor(ChartColor.BLACK);
                    r.drawRect(x + xx, y + yy, cellWidth, cellHeight);
                  }
                }
                break;

              case 2:
                if (isShowSymbols()) {
                  // use symbolsize or calc symbol size?
                  double symbolSize = data.getSymbolSize(c1);
                  if (isSymbolAutoSize()) {
                    final double minSize = getMinTickSize();
                    final double diff = contentWidth - minSize;
                    symbolSize = (minSize + (p * diff));
                  }

                  if (symbolSize > 0) {
                    final int symbol = data.getSymbol(c1);

                    ChartColor symbolColor = sampleColor;
                    if (isSymbolAutoColor()) {
                      symbolColor = autoColor;
                    }

                    if (shadowColor != null) {
                      SymbolDrawer.draw(
                        r,
                        symbol,
                        cx + xOffset, cy + yOffset,
                        symbolSize,
                        shadowColor, shadowColor, shadowColor
                      );
                    }

                    SymbolDrawer.draw(
                      r,
                      symbol,
                      cx, cy,
                      symbolSize,
                      symbolColor, outlineColor, background
                    );
                  }
                }
                break;

              case 3:
                final String format = getFormat();
                final String text = r.format(format, value);

                // add popup
                final String popupText = buildPopupText(cube, c0, c1, text);
                final Runnable[] linkCommands = getLinkCommands(r, cube, c0, c1);
                r.addPopup(
                  x + xx, y + yy,
                  cellWidth, cellHeight,
                  0,
                  Renderer.CENTER,
                  popupText,
                  getPopupFont(),
                  linkCommands[0], linkCommands[1], linkCommands[2]
                );

                // labels
                final boolean isSelected = data.isSelected(1, c1);
                final String label = buildLabelText(cube, c0, c1, text, isSelected);
                if (label != null) {
                  final ChartColor labelColor =
                    anim.getLabelColor(progress, progress0, getLabelColor());
                  r.setColor(labelColor);
                  r.setFont(labelFont);
                  LabelDrawer.drawInside(
                    r,
                    x + xx, y + yy,
                    cellHeight, cellWidth,
                    0,
                    Renderer.CENTER,
                    getLabelAngle(),
                    text
                  );
                }
            }
          }
        }
      }
    }
  }

  private ChartColor getTargetColor(Data data, int c1, double p) {
    final HeatColor[] colors = getHeatColors();
    if (colors == null) {
      return null;
    }

    for (HeatColor h : colors) {
      if (p >= h.start && p < h.end) {
        final double diff = h.end - h.start;
        if (false) {
          return h.startColor;
        } else {
          p = (p - h.start) / diff;
          final double ip = (1.0 - p);
          final int r = (int) (h.startColor.getR() * ip + h.endColor.getR() * p);
          final int g = (int) (h.startColor.getG() * ip + h.endColor.getG() * p);
          final int b = (int) (h.startColor.getB() * ip + h.endColor.getB() * p);
          ChartColor pColor = new ChartColor(r, g, b);
          pColor = changeSelectedColor(pColor, data, 1, c1);
          return pColor;
        }
      }
    }

    return null;
  }
}
