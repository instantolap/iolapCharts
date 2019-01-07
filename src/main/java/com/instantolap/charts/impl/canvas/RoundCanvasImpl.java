package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.CanvasAnimation;
import com.instantolap.charts.impl.animation.FadeInCanvasAnim;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.Renderer;


public class RoundCanvasImpl extends BasicRoundCanvasImpl {

  private final CanvasAnimation anim;

  public RoundCanvasImpl() {
    anim = new FadeInCanvasAnim();
  }

  public void render(
    double progress,
    Renderer r,
    int x, int y,
    int width, int height,
    int cx, int cy,
    int radius,
    Data data,
    Axis innerAxis,
    RoundAxis outerAxis, boolean isCentered)
  {
    super.render(anim, progress, r, x, y, width, height);

    final int[] innerGrid = innerAxis.getGrid();
    final int innerLength = innerGrid.length;
    final int[] outerGrid = outerAxis.getGrid();
    final int outerLength = outerGrid.length;

    final ChartColor bgColor = getScaleBackground();
    final ChartColor bgColor2 = getScaleBackground2();
    final ChartColor gridColor = getGrid();
    final ChartColor baseLineColor = getBaseLine();
    final ChartStroke gridStroke = getGridStroke();
    final boolean round = isRound();

    final double startAngle = outerAxis.getStartAngle();
    final double stopAngle = outerAxis.getStopAngle();

    final ChartColor border = getBorder();
    if (border != null) {
      final ChartStroke borderStroke = getBorderStroke();
      final int bw = borderStroke.getWidth();
      final int br = radius - bw / 2;
      r.setStroke(borderStroke);
      r.setColor(border);
      r.drawCircle(cx - br, cy - br, br * 2);
      r.resetStroke();
      radius -= bw;
    }

    // draw background
    if ((bgColor != null) || (bgColor2 != null)) {
      for (int n = 0; n < outerLength; n++) {
        final int n1;
        final int n2;
        if (n == 0) {
          n2 = outerLength - 1;
          n1 = n;
        } else {
          n1 = n - 1;
          n2 = n;
        }
        final double rad0 =
          getRad(startAngle, stopAngle, outerGrid[n1], outerAxis.getSize(), progress);
        final double rad1 =
          getRad(startAngle, stopAngle, outerGrid[n2], outerAxis.getSize(), progress);

        for (int i = 1; i < innerLength; i++) {

          ChartColor bg = null;
          if (i % 2 == 0) {
            bg = bgColor2;
          }
          if (bg == null) {
            bg = bgColor;
          }

          if (bg == null) {
            continue;
          }

          final int v0 = innerGrid[i - 1];
          final int v1 = innerGrid[i];

          r.setColor(bg);
          r.fillDonut(cx, cy, v0, v1, rad0, rad1, round);
        }
      }
    }

    // draw grid
    if (gridColor != null) {
      r.setColor(gridColor);
      r.setStroke(gridStroke);

      for (final int anOuterGrid : outerGrid) {
        final double rad =
          getRad(startAngle, stopAngle, anOuterGrid, outerAxis.getSize(), progress);
        final int xx = (int) (Math.sin(rad) * radius);
        final int yy = (int) (-Math.cos(rad) * radius);
        r.drawLine(cx, cy, cx + xx, cy + yy);
      }

      if (round) {
        for (final int v : innerGrid) {
          r.drawDonut(cx, cy, v, v, startAngle, stopAngle, true);
        }
      } else {
        for (int n = 0; n < outerLength; n++) {
          int index = n - 1;
          if (index < 0) {
            index += outerLength;
          }
          final double rad0 =
            getRad(startAngle, stopAngle, outerGrid[index], outerAxis.getSize(), progress);
          final double rad1 =
            getRad(startAngle, stopAngle, outerGrid[n], outerAxis.getSize(), progress);

          for (final int v : innerGrid) {
            r.drawDonut(cx, cy, v, v, rad0, rad1, false);
          }
        }
      }
      r.resetStroke();
    }

    // draw areas
    if (outerAxis instanceof ValueAxis) {
      final ValueAxis valueAxis = (ValueAxis) outerAxis;
      for (CriticalArea area : valueAxis.getCriticalAreas()) {

        double arcStart = getRad(
          startAngle, stopAngle, valueAxis.getPosition(area.min), valueAxis.getSize(), progress
        );
        arcStart = Math.max(arcStart, startAngle);
        double arcEnd = getRad(
          startAngle, stopAngle, valueAxis.getPosition(area.max), valueAxis.getSize(), progress
        );
        arcEnd = Math.min(arcEnd, stopAngle);
        if (arcEnd > arcStart) {
          r.setColor(area.color);
          r.fillDonut(cx, cy, radius - 10, radius, arcStart, arcEnd,
            round
          );
        }
      }
    }

    // draw baseline
    if (baseLineColor != null) {
      if (innerAxis instanceof ValueAxis) {
        final ValueAxis scale = (ValueAxis) innerAxis;
        int v = scale.getRadius(0);
        r.setColor(baseLineColor);
        if (round) {
          r.drawDonut(cx, cy, v, v, startAngle, stopAngle, true);
        } else {
          for (int n = 1; n < outerLength; n++) {
            final double rad0 =
              getRad(startAngle, stopAngle, outerGrid[n - 1], outerAxis.getSize(), progress);
            final double rad1 =
              getRad(startAngle, stopAngle, outerGrid[n], outerAxis.getSize(), progress);

            for (final int anInnerGrid : innerGrid) {
              r.drawDonut(cx, cy, v, v, rad0, rad1, false);
            }
          }
        }

        for (TargetLine target : scale.getTargetLines()) {
          v = scale.getRadius(target.value);
          r.setColor(target.color);
          if (target.stroke != null) {
            r.setStroke(target.stroke);
          }
          if (round) {
            r.drawDonut(cx, cy, v, v, startAngle, stopAngle, round);
          } else {
            for (int n = 0; n < outerLength; n++) {
              int index = n - 1;
              if (index < 0) {
                index += outerLength;
              }
              final double rad0 =
                getRad(startAngle, stopAngle, outerGrid[index], outerAxis.getSize(), progress);
              final double rad1 =
                getRad(startAngle, stopAngle, outerGrid[n], outerAxis.getSize(), progress);

              for (final int anInnerGrid : innerGrid) {
                r.drawDonut(cx, cy, v, v, rad0, rad1, false);
              }
            }
          }
          r.setStroke(ChartStroke.DEFAULT);
        }
      }
    }
  }

  private double getRad(double start, double end, double n, int count, double progress) {
    double rad = (n / count) * (end - start) + start;
    rad -= (1 - progress) * 0.1 * Math.PI;
    return rad;
  }

}
