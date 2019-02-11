package com.instantolap.charts.impl.util;

import com.instantolap.charts.HasValueLabels.ValueLabelType;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.Renderer;

import static com.instantolap.charts.HasValueLabels.ValueLabelType.OUTSIDE;


public class LabelDrawer {

  // =========================================================================
  // round labels
  // =========================================================================

  public static void roundOutsideLabel(
    Renderer r, double x, double y, double len, double a, String text
  ) {

    // calc anchor
    final int anchor = getAnchor(a);

    // calc position
    x += Math.sin(a) * len;
    y -= Math.cos(a) * len;

    r.drawText(x, y, text, 0, anchor, false);
  }

  private static int getAnchor(double a) {
    while (a < 0) {
      a += 2 * Math.PI;
    }

    final int anchor;
    if (a >= Math.PI * 1.75 || a <= Math.PI * 0.25) {
      anchor = Renderer.SOUTH;
    } else if (a >= Math.PI * 0.75 && a <= Math.PI * 1.25) {
      anchor = Renderer.NORTH;
    } else if (a <= Math.PI) {
      anchor = Renderer.WEST;
    } else {
      anchor = Renderer.EAST;
    }
    return anchor;
  }

  public static void roundInsideLabel(
    Renderer r, double x, double y, double len, double a, String text
  ) {

    // calc anchor
    final int anchor = invertAnchor(getAnchor(a));

    // calc position
    x += Math.sin(a) * len;
    y -= Math.cos(a) * len;

    r.drawText(x, y, text, 0, anchor, false);
  }

  private static int invertAnchor(int anchor) {
    switch (anchor) {
      case Renderer.NORTH:
        return Renderer.SOUTH;
      case Renderer.SOUTH:
        return Renderer.NORTH;
      case Renderer.WEST:
        return Renderer.EAST;
      case Renderer.EAST:
        return Renderer.WEST;
      default:
        return Renderer.CENTER;
    }
  }

  // =========================================================================
  // rect labels
  // =========================================================================

  public static void drawInsideOutside(
    Renderer r,
    double x, double y, double h, double w, double s,
    int anchor, double rotation, ChartColor outsideColor, ChartColor insideColor, String text,
    ValueLabelType type
  ) {
    final double[] size = r.getTextSize(text, rotation);

    switch (anchor) {
      case Renderer.EAST:
      case Renderer.WEST:
        if (type == OUTSIDE || (size[0] + 2 * s > w) || (size[1] > h)) {
          r.setColor(outsideColor);
          drawOutside(r, x, y, h, w, s, anchor, rotation, text);
        } else {
          r.setColor(insideColor);
          drawInside(r, x, y, h, w, s, anchor, rotation, text);
        }
        break;
      case Renderer.NORTH:
      case Renderer.SOUTH:
        if (type == OUTSIDE || (size[1] + 2 * s > h) || (size[0] > w)) {
          r.setColor(outsideColor);
          drawOutside(r, x, y, h, w, s, anchor, rotation, text);
        } else {
          r.setColor(insideColor);
          drawInside(r, x, y, h, w, s, anchor, rotation, text);
        }
        break;
    }
  }

  public static void drawOutside(
    Renderer r,
    double x, double y, double h, double w, double s,
    int anchor, double rotation, String text
  ) {
    switch (anchor) {
      case Renderer.EAST:
        r.drawText(x + w + s, y + h / 2, text, rotation, Renderer.WEST, true);
        break;
      case Renderer.WEST:
        r.drawText(x - s, y + h / 2, text, rotation, Renderer.EAST, true);
        break;
      case Renderer.NORTH:
        r.drawText(x + w / 2, y - s, text, rotation, Renderer.SOUTH, true);
        break;
      case Renderer.SOUTH:
        r.drawText(x + w / 2, y + h + s, text, rotation, Renderer.NORTH, true);
        break;
    }
  }

  public static void drawInside(
    Renderer r,
    double x, double y, double h, double w, double s,
    int anchor, double rotation, String text
  ) {
    switch (anchor) {
      case Renderer.EAST:
        r.drawText(x + w - s, y + h / 2, text, rotation, Renderer.EAST, true);
        break;
      case Renderer.WEST:
        r.drawText(x + s, y + h / 2, text, rotation, Renderer.WEST, true);
        break;
      case Renderer.NORTH:
        r.drawText(x + w / 2, y + s, text, rotation, Renderer.NORTH, true);
        break;
      case Renderer.SOUTH:
        r.drawText(x + w / 2, y + h - s, text, rotation, Renderer.SOUTH, true);
        break;
      case Renderer.CENTER:
        r.drawText(x + w / 2, y + h / 2, text, rotation, Renderer.CENTER, true);
        break;
    }
  }
}
