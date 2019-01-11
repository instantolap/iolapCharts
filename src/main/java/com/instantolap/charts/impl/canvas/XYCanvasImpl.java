package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.Axis;
import com.instantolap.charts.CriticalArea;
import com.instantolap.charts.TargetLine;
import com.instantolap.charts.ValueAxis;
import com.instantolap.charts.impl.animation.CanvasAnimation;
import com.instantolap.charts.impl.animation.FadeInCanvasAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.Renderer;


public class XYCanvasImpl extends BasicXYCanvasImpl {

  private final CanvasAnimation anim;

  public XYCanvasImpl(Theme theme) {
    super(theme);
    
    anim = new FadeInCanvasAnim();
  }

  public void render(
    double progress,
    Renderer r,
    int x, int y,
    int width, int height,
    Axis yAxis1, Axis yAxis2,
    Axis xAxis, Axis xAxis2)
  {

    super.render(anim, progress, r, x, y, width, height);

    clip(r, x, y, width, height);
    drawVerticalBackground(xAxis, progress, r, x, y, height);
    drawVerticalBackground(xAxis2, progress, r, x, y, height);
    drawHorizontalBackground(yAxis1, progress, r, x, y, width);
    drawHorizontalBackground(yAxis2, progress, r, x, y, width);

    // areas
    drawAreas(r, x, y, width, height, yAxis1);
    drawAreas(r, x, y, width, height, yAxis2);
    drawAreas(r, x, y, width, height, xAxis);
    drawAreas(r, x, y, width, height, xAxis2);

    // grids
    drawVerticalGrid(xAxis, progress, r, x, y, height);
    drawVerticalGrid(xAxis2, progress, r, x, y, height);
    drawHorizontalGrid(yAxis1, progress, r, x, y, width);
    drawHorizontalGrid(yAxis2, progress, r, x, y, width);

    // base lines
    final ChartColor baseLineColor = getBaseLine();
    if (baseLineColor != null) {
      r.setColor(baseLineColor);
      drawHorizontalBaseLine(r, x, y, width, yAxis1);
      drawHorizontalBaseLine(r, x, y, width, yAxis2);
      drawVerticalBaseLine(r, x, y, height, xAxis);
      drawVerticalBaseLine(r, x, y, height, xAxis2);
    }

    // target lines
    drawTargetLines(r, x, y, width, height, yAxis1);
    drawTargetLines(r, x, y, width, height, yAxis2);
    drawTargetLines(r, x, y, width, height, xAxis);
    drawTargetLines(r, x, y, width, height, xAxis2);

    // reset clip and render border
    r.resetClip();
    super.postRender(anim, progress, r, x, y, width, height);

    clip(r, x, y, width, height);
  }

  private void clip(Renderer r, int x, int y, int width, int height) {
    if (getBorder() == null) {
      x--;
      y--;
      width += 2;
      height += 2;
    }

    r.clipRoundedRect(x, y, width, height, getRoundedCorner());
  }

  private void drawVerticalBackground(
    Axis xAxis, double progress, Renderer r, int x, int y, int height)
  {
    if (xAxis == null) {
      return;
    }
    final int[] xGrid = xAxis.getGrid();
    if (xGrid != null) {
      final ChartColor verticalBackground2 = getVerticalBackground2();
      if (verticalBackground2 != null) {
        for (int n = 1; n < xGrid.length; n += 2) {
          final double grid = (double) n / (xGrid.length - 1);
          r.setColor(anim.getVerticalBackground(progress, grid, verticalBackground2));
          final int bgWidth = xGrid[n] - xGrid[n - 1];
          r.fillRect(x + xGrid[n - 1], y, bgWidth, height);
        }
      }
    }
  }

  private void drawHorizontalBackground(
    Axis axis, double progress, Renderer r, int x, int y, int width)
  {
    if (axis == null || !axis.isShowGrid()) {
      return;
    }

    final ChartColor horizontalBackground2 = getHorizontalBackground2();
    if (horizontalBackground2 == null) {
      return;
    }

    final int[] yGrid = axis.getGrid();
    if (yGrid != null) {
      for (int n = 1; n < yGrid.length; n += 2) {
        final double grid = (double) n / (yGrid.length - 1);
        r.setColor(anim.getHorizontalBackground(progress, grid, horizontalBackground2));
        final int bgHeight = yGrid[n - 1] - yGrid[n];
        r.fillRect(x, y + yGrid[n], width, bgHeight);
      }
    }
  }

  private void drawAreas(Renderer r, int x, int y, int width, int height, Axis axis) {
    final int padding = 10;
    if (axis != null) {
      if (axis instanceof ValueAxis) {
        final ValueAxis valueAxis = (ValueAxis) axis;
        for (CriticalArea area : valueAxis.getCriticalAreas()) {
          final double min = Math.min(area.min, area.max);
          final double max = Math.max(area.min, area.max);
          int x0 = valueAxis.getPosition(min);
          int x1 = valueAxis.getPosition(max);
          r.setColor(area.color);
          if (valueAxis.isVertical()) {
            x0 = Math.max(0, Math.min(x0, height));
            x1 = Math.max(0, Math.min(x1, height));
            r.fillRect(x, y + x1, width, Math.abs(x0 - x1));

            if (area.text != null) {
              r.setColor(ChartColor.BLACK);
              r.drawText(x + width - padding, y + x0 - padding, area.text, 0, Renderer.EAST);
            }
          } else {
            r.fillRect(x + x0, y, (x1 - x0), height);

            if (area.text != null) {
              r.setColor(ChartColor.BLACK);
              r.drawText(x + x1 - padding, y + height - padding, area.text, 0, Renderer.EAST);
            }
          }
        }
      }
    }
  }

  private void drawVerticalGrid(Axis axis, double progress, Renderer r, int x, int y, int height) {
    if (axis == null || !axis.isShowGrid()) {
      return;
    }

    final ChartColor[] verticalGrid = getVerticalGrid();
    if (verticalGrid == null) {
      return;
    }

    final int[] xGrid = axis.getGridLines();
    if (xGrid != null) {
      r.setStroke(getVerticalGridStroke());
      final boolean[] visible = axis.getVisibleGrid();
      for (int n = 0; n < xGrid.length; n++) {
        //        if ((visible == null) || (n >= visible.length) || (visible[n])) {
        final double gridProgress = (double) n / (xGrid.length - 1);
        final ChartColor currentColor = verticalGrid[n % verticalGrid.length];
        r.setColor(anim.getVerticalGrid(progress, gridProgress, currentColor));
        r.drawLine(x + xGrid[n], y, x + xGrid[n], y + height - 1);
      }
      //      }

      r.resetStroke();
    }
  }

  private void drawHorizontalGrid(Axis axis, double progress, Renderer r, int x, int y, int width) {
    if (axis == null || !axis.isShowGrid()) {
      return;
    }

    final ChartColor[] horizontalGrid = getHorizontalGrid();
    if (horizontalGrid == null) {
      return;
    }

    final int[] yGrid = axis.getGridLines();
    if (yGrid != null) {
      r.setStroke(getHorizontalGridStroke());
      final boolean[] visible = axis.getVisibleGrid();
      for (int n = 0; n < yGrid.length; n++) {
        if ((visible == null) || (n >= visible.length) || visible[n]) {
          final double gridProgress = (double) n / (yGrid.length - 1);
          final ChartColor currentColor = horizontalGrid[n % horizontalGrid.length];
          r.setColor(anim.getHorizontalGrid(progress, gridProgress, currentColor));
          r.drawLine(x, y + yGrid[n], x + width - 1, y + yGrid[n]);
        }
      }

      r.resetStroke();
    }
  }

  private void drawHorizontalBaseLine(Renderer r, int x, int y, int width, Axis axis) {
    if ((axis != null) && (axis instanceof ValueAxis) && (axis.isVisible())) {
      final ValueAxis valueAxis = (ValueAxis) axis;
      final int y0 = y + valueAxis.getPosition(0);
      r.drawLine(x, y0, x + width, y0);
    }
  }

  private void drawVerticalBaseLine(Renderer r, int x, int y, int height, Axis axis) {
    if ((axis != null) && (axis instanceof ValueAxis)) {
      final ValueAxis valueAxis = (ValueAxis) axis;
      final int x0 = x + valueAxis.getPosition(0);
      r.drawLine(x0, y, x0, y + height);
    }
  }

  private void drawTargetLines(Renderer r, int x, int y, int width, int height, Axis axis) {
    if (axis != null) {
      if (axis instanceof ValueAxis) {
        final ValueAxis valueAxis = (ValueAxis) axis;
        for (TargetLine target : valueAxis.getTargetLines()) {
          final int x0 = valueAxis.getPosition(target.value);
          r.setColor(target.color);
          if (target.stroke != null) {
            r.setStroke(target.stroke);
          }
          if (valueAxis.isVertical()) {
            r.drawLine(x, y + x0, x + width, y + x0);
          } else {
            r.drawLine(x + x0, y, x + x0, y + height);
          }
          r.setStroke(ChartStroke.DEFAULT);
        }
      }
    }
  }
}
