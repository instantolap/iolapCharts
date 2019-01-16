package com.instantolap.charts.impl.util;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.Renderer;


public class SymbolDrawer {

  public final static int SYMBOL_CIRCLE = 1;
  public final static int SYMBOL_CIRCLE_OPAQUE = 2;
  public final static int SYMBOL_CIRCLE_FILLED = 3;
  public final static int SYMBOL_SQUARE = 4;
  public final static int SYMBOL_SQUARE_OPAQUE = 5;
  public final static int SYMBOL_SQUARE_FILLED = 6;
  public final static int SYMBOL_DIAMOND = 7;
  public final static int SYMBOL_DIAMOND_OPAQUE = 8;
  public final static int SYMBOL_DIAMOND_FILLED = 9;
  public final static int SYMBOL_TRIANGLE = 10;
  public final static int SYMBOL_TRIANGLE_OPAQUE = 11;
  public final static int SYMBOL_TRIANGLE_FILLED = 12;
  public final static int SYMBOL_DOUGNUT = 13;
  public final static int SYMBOL_DOUGNUT_OPAQUE = 14;
  public final static int SYMBOL_DOUGNUT_FILLED = 15;
  public final static int SYMBOL_CROSS = 16;

  public static int getSymbol(String symbol) {
    int s = SymbolDrawer.SYMBOL_CIRCLE;
    if ("circle".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_CIRCLE;
    } else if ("circle_opaque".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_CIRCLE_OPAQUE;
    } else if ("circle_filled".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_CIRCLE_FILLED;
    } else if ("square".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_SQUARE;
    } else if ("square_opaque".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_SQUARE_OPAQUE;
    } else if ("square_filled".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_SQUARE_FILLED;
    } else if ("diamond".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_DIAMOND;
    } else if ("diamond_opaque".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_DIAMOND_OPAQUE;
    } else if ("diamond_filled".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_DIAMOND_FILLED;
    } else if ("triangle".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_TRIANGLE;
    } else if ("triangle_opaque".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_TRIANGLE_OPAQUE;
    } else if ("triangle_filled".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_TRIANGLE_FILLED;
    } else if ("dougnut".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_DOUGNUT;
    } else if ("dougnut_opaque".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_DOUGNUT_OPAQUE;
    } else if ("dougnut_filled".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_DOUGNUT_FILLED;
    } else if ("cross".equals(symbol)) {
      s = SymbolDrawer.SYMBOL_CROSS;
    }
    return s;
  }

  public static void draw(
    Renderer r,
    int symbol,
    double x, double y,
    double size,
    ChartColor color, ChartColor outline, ChartColor background)
  {
    if (outline != null) {
      size -= 2;
    }

    size -= size % 2;
    final double xx = x - size / 2;
    final double yy = y - size / 2;

    switch (symbol) {
      case SYMBOL_CIRCLE:
      case SYMBOL_CIRCLE_OPAQUE:
      case SYMBOL_CIRCLE_FILLED:

        switch (symbol) {
          case SYMBOL_CIRCLE:
            background = null;
            break;
          case SYMBOL_CIRCLE_FILLED:
            background = color;
            break;
        }

        if (background != null) {
          r.setColor(background);
          r.fillCircle(xx, yy, size);
        }

        r.setColor(color);
        r.drawCircle(xx, yy, size);

        if (outline != null) {
          r.setColor(outline);
          r.drawCircle(xx - 1, yy - 1, size + 2);
        }

        break;

      case SYMBOL_SQUARE:
      case SYMBOL_SQUARE_OPAQUE:
      case SYMBOL_SQUARE_FILLED:

        switch (symbol) {
          case SYMBOL_SQUARE:
            background = null;
            break;
          case SYMBOL_SQUARE_FILLED:
            background = color;
            break;
        }

        if (background != null) {
          r.setColor(background);
          r.fillRect(xx, yy, size, size);
        }

        r.setColor(color);
        r.drawRect(xx, yy, size, size);

        if (outline != null) {
          r.setColor(outline);
          r.drawRect(xx - 1, yy - 1, size + 2, size + 2);
        }

        break;

      case SYMBOL_DIAMOND:
      case SYMBOL_DIAMOND_OPAQUE:
      case SYMBOL_DIAMOND_FILLED:

        switch (symbol) {
          case SYMBOL_DIAMOND:
            background = null;
            break;
          case SYMBOL_DIAMOND_FILLED:
            background = color;
            break;
        }

        if (background != null) {
          r.setColor(background);
          fillDiamond(r, xx, yy, size, size);
        }

        r.setColor(color);
        drawDiamond(r, xx, yy, size, size);

        if (outline != null) {
          r.setColor(outline);
          drawDiamond(r, xx - 1, yy - 1, size + 2, size + 2);
        }
        break;

      case SYMBOL_CROSS:
        r.setColor(color);
        r.drawLine(x - size / 2, y, x + size / 2, y);
        r.drawLine(x, y - size / 2, x, y + size / 2);
        break;
    }
  }

  private static void fillDiamond(Renderer r, double x, double y, double w, double h) {
    final double[] xx = new double[]{x, x + w / 2, x + w, x + w / 2};
    final double[] yy = new double[]{y + h / 2, y, y + h / 2, y + h};
    r.fillPolygon(xx, yy);
  }

  private static void drawDiamond(Renderer r, double x, double y, double w, double h) {
    final double[] xx = new double[]{x, x + w / 2, x + w, x + w / 2};
    final double[] yy = new double[]{y + h / 2, y, y + h / 2, y + h};
    r.drawPolygon(xx, yy);
  }
}
