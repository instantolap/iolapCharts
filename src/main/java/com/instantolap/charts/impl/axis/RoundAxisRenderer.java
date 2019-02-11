package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Axis;
import com.instantolap.charts.RoundAxis;
import com.instantolap.charts.impl.util.LabelDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class RoundAxisRenderer implements AxisRenderer {

  @Override
  public void render(
    Renderer r,
    Axis _axis,
    double x, double y,
    double width, double height,
    double radius,
    boolean isCentered,
    boolean flip,
    ChartFont font)
  {
    final RoundAxis axis = (RoundAxis) _axis;

    x += width / 2;
    y += height / 2;

    ChartFont tickFont = axis.getFont();
    if (tickFont == null) {
      tickFont = font;
    }

    final double tickWidth = axis.getTickWidth();

    final double r1 = radius;
    final double a1 = axis.getStartAngle();
    final double a2 = axis.getStopAngle();

    // draw baseline
    final ChartColor lineColor = axis.getLineColor();
    if (axis.isShowBaseLine() && (lineColor != null)) {
      r.setColor(axis.getLineColor());
      r.drawDonut(x, y, r1, r1, a1, a2, true);
    }

    final double[] grid = isCentered ? axis.getCenteredGrid() : axis.getGrid();
    if (grid == null) {
      return;
    }

    // draw ticks
    //		boolean visible[] = new boolean[grid.length];
    final String[] texts = axis.getTexts();
    double lastStart = Double.MIN_VALUE;
    double lastEnd = Double.MIN_VALUE;
    final ChartColor[] colors = axis.getColors();
    final boolean autoSpacing = axis.isAutoSpacingOn() || true;

    r.setFont(tickFont);
    for (int n = 0; n < grid.length; n++) {
      //			visible[n] = true;

      final double a = ((double) grid[n] / axis.getSize()) * (a2 - a1) + a1;
      final double x1 = x + Math.sin(a) * r1;
      final double y1 = y - Math.cos(a) * r1;

      // draw label
      if (axis.isShowLabels()) {
        final String text = texts[n];
        final double pos = grid[n];
        r.setColor(colors[n % colors.length]);
        r.setFont(axis.getFont());

        // enough space?
        if (autoSpacing) {
          final double[] size = r.getTextSize(text, 0);
          final double minSpace = 5;
          final double space = (size[0] + minSpace) / 2.0;
          if ((pos + space) > lastStart && (pos - space) < lastEnd) {
            continue;
          }

          lastStart = pos - space;
          lastEnd = pos + space;
        }

        if (axis.isRotateLabels()) {
          final double textHeight = r.getTextHeight(text);
          final double tr = r1 + textHeight / 2;
          final double tx = x + Math.sin(a) * tr;
          final double ty = y - Math.cos(a) * tr;

          double ta = a;
          if (ta < 0) {
            ta += Math.PI;
          }

          if (ta <= Math.PI * 0.5 || ta > Math.PI * 1.5) {
            ta -= Math.PI;
          }
          r.drawText(tx, ty, text, 180 + Math.toDegrees(ta), Renderer.CENTER, false);
        } else {
          if (axis.isShowLabelsInside()) {
            LabelDrawer.roundInsideLabel(r, x, y, r1 - tickWidth - axis.getLabelSpacing(), a, text);
          } else {
            LabelDrawer.roundOutsideLabel(
              r, x, y, r1 + tickWidth + axis.getLabelSpacing(), a, text
            );
          }
        }
      }

      // draw ticks
      if ((lineColor != null) && (tickWidth > 0)) {
        final double usedTickWidth = axis.isShowLabelsInside() ? -tickWidth : tickWidth;
        final double x2 = x + Math.sin(a) * (r1 + usedTickWidth);
        final double y2 = y - Math.cos(a) * (r1 + usedTickWidth);

        r.setColor(axis.getLineColor());
        r.drawLine(x1, y1, x2, y2);
      }
    }
  }

}
