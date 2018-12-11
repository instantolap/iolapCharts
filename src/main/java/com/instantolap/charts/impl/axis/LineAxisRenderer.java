package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Axis;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public class LineAxisRenderer implements AxisRenderer {

  @Override
  public void render(
    Renderer r,
    Axis axis,
    int x, int y,
    int width, int height,
    int radius,
    boolean isCentered,
    boolean flip,
    ChartFont font)
  {
    final boolean isVertical = axis.isVertical();

    ChartFont tickFont = axis.getFont();
    if (tickFont == null) {
      tickFont = font;
    }

    final boolean autoSpacing = axis.isAutoSpacingOn();

    // draw background
    final ChartColor background = axis.getBackground();
    if (background != null) {
      r.setColor(background);
      r.fillRect(x, y, width, height);
    }

    // draw title
    final String title = axis.getTitle();
    if (title != null) {

      ChartFont titleFont = axis.getTitleFont();
      if (titleFont == null) {
        titleFont = tickFont;
      }

      r.setFont(titleFont);
      r.setColor(axis.getTitleColor());
      final int titlePadding = axis.getTitlePadding();

      if (isVertical) {
        final int textY = y + (height / 2);
        final int textX = flip ? (x + titlePadding) : (x + width - titlePadding);
        final int anchor = flip ? Renderer.WEST : Renderer.EAST;
        r.drawText(textX, textY, title, axis.getTitleRotation(), anchor);
      } else {
        final int textX = x + (width / 2);
        final int textY = flip ? (y + titlePadding) : (y + height - titlePadding);
        final int anchor = flip ? Renderer.NORTH : Renderer.SOUTH;
        r.drawText(textX, textY, title, axis.getTitleRotation(), anchor);
      }
    }

    // draw baseline
    final ChartColor lineColor = axis.getLineColor();
    if (axis.isShowBaseLine() && (lineColor != null)) {
      r.setColor(axis.getLineColor());
      if (isVertical) {
        if (flip) {
          r.drawLine(x + width, y, x + width, y + height - 1);
        } else {
          r.drawLine(x, y, x, y + height - 1);
        }
      } else {
        if (flip) {
          r.drawLine(x, y + height, x + width - 1, y + height);
        } else {
          r.drawLine(x, y, x + width - 1, y);
        }
      }
    }

    // draw ticks
    final int tickWidth = axis.getTickWidth();
    final int[] grid = isCentered ? axis.getCenteredGrid() : axis.getGrid();
    if (grid == null) {
      return;
    }
    final boolean[] visible = new boolean[grid.length];
    final String[] texts = axis.getTexts();
    int lastStart = Integer.MIN_VALUE;
    int lastEnd = Integer.MIN_VALUE;
    final ChartColor[] colors = axis.getColors();

    r.setFont(tickFont);
    for (int n = 0; n < grid.length; n++) {
      visible[n] = true;

      // draw label
      if (axis.isShowLabels()) {
        final String text = texts[n];
        final int rot = axis.getLabelRotation();
        final int pos = grid[n];

        // enough space?
        if (autoSpacing) {
          final double[] size = r.getTextSize(text, rot);
          final int minSpace = 5;
          final double space = ((isVertical ? size[1] : size[0]) + minSpace) / 2.0;
          if ((pos + space) > lastStart && (pos - space) < lastEnd) {
            continue;
          }

          lastStart = pos - (int) space;
          lastEnd = pos + (int) space;
        }

        // draw ticks
        if ((lineColor != null) && (tickWidth > 0)) {
          r.setColor(axis.getLineColor());
          if (isVertical) {
            if (flip) {
              r.drawLine(x + width - tickWidth, y + grid[n], x + width, y + grid[n]);
            } else {
              r.drawLine(x, y + grid[n], x + tickWidth, y + grid[n]);
            }
          } else {
            if (flip) {
              r.drawLine(x + grid[n], y + height - tickWidth, x + grid[n], y + height);
            } else {
              r.drawLine(x + grid[n], y, x + grid[n], y + tickWidth);
            }
          }
        }

        // draw
        r.setColor(colors[n % colors.length]);
        final int spacing = axis.getLabelSpacing();
        if (isVertical) {
          if (flip) {
            r.drawText(x + width - spacing - tickWidth, y + pos, text, rot, Renderer.EAST);
          } else {
            r.drawText(x + tickWidth + spacing, y + pos, text, rot, Renderer.WEST);
          }
        } else {
          if (flip) {
            r.drawText(x + pos, y + height - tickWidth - spacing, text, rot, Renderer.SOUTH);
          } else {
            r.drawText(x + pos, y + spacing + tickWidth, text, rot, Renderer.NORTH);
          }
        }
      }
    }

    axis.setVisibleGrid(visible);
  }
}
