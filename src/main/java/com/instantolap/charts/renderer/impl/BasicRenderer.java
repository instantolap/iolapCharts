package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.renderer.*;
import com.instantolap.charts.renderer.popup.DonutPopup;
import com.instantolap.charts.renderer.popup.Popup;
import com.instantolap.charts.renderer.popup.RectPopup;
import com.instantolap.charts.renderer.util.StringHelper;

import java.util.ArrayList;
import java.util.List;


public abstract class BasicRenderer implements Renderer {

  protected final static int TEXTLINE_SPACING = 4;
  private final List<RendererListener> listeners = new ArrayList<>();
  private final List<Popup> popups = new ArrayList<>();
  protected MouseListeners mouseListeners = new MouseListeners();
  private boolean enableHandlers = true;
  private Popup currentPopup, nextPopup;
  private ChartColor color;

  protected abstract int getTextLineWidth(String text);

  protected abstract int getTextLineHeight(String text);

  @Override
  public TextInfo getTextInfo(final int x, final int y, String text, double angle, int anchor) {
    final double[] size = getTextSize(text, angle);
    final TextInfo i = new TextInfo();
    i.w = size[0];
    i.h = size[1];
    i.rad = Math.toRadians(angle);
    i.tw = getTextWidth(text);
    i.th = getTextHeight(text);
    final double shift;
    final double r = (i.tw - i.th) / 2.0;
    switch (anchor) {
      case EAST:
        shift = r * (1 - Math.abs(Math.cos(i.rad)));
        i.x = x - size[0];
        i.y = y - size[1] / 2.0;
        i.tx = x - i.tw + shift;
        i.ty = y + (i.th / 2.0);
        i.rx = x - (i.tw / 2.0) + shift;
        i.ry = y;
        break;
      case WEST:
        shift = r * (1 - Math.abs(Math.cos(i.rad)));
        i.x = x;
        i.y = y - size[1] / 2.0;
        i.tx = x - shift;
        i.ty = y + (i.th / 2.0);
        i.rx = x + (i.tw / 2.0) - shift;
        i.ry = y;
        break;
      case SOUTH:
        shift = r * Math.abs(Math.sin(i.rad));
        i.x = x - size[0] / 2.0;
        i.y = y - size[1];
        i.tx = x - i.tw / 2.0;
        i.ty = y - shift;
        i.rx = x;
        i.ry = y - (i.th / 2.0) - shift;
        break;
      case NORTH:
        shift = r * Math.abs(Math.sin(i.rad));
        i.x = x - size[0] / 2.0;
        i.y = y;
        i.tx = x - i.tw / 2.0;
        i.ty = y + i.th + shift;
        i.rx = x;
        i.ry = y + (i.th / 2.0) + shift;
        break;
      case CENTER:
      default:
        i.x = x - size[0] / 2.0;
        i.y = y - size[1] / 2.0;
        i.tx = x - i.tw / 2.0;
        i.ty = y + i.th / 2.0;
        i.rx = x;
        i.ry = y;
        break;
    }

    return i;
  }

  protected void fireMouseClick(int x, int y) {
    for (Popup p : popups) {
      if (p.isInside(this, x, y)) {
        if (p.onMouseClick != null) {
          p.onMouseClick.run();
          break;
        }
      }
    }
  }

  protected void fireMouseMove(int x, int y) throws ChartException {
    for (Popup p : popups) {
      if (p.isInside(this, x, y)) {

        // hide current visible popup?
        if ((currentPopup != null) && (currentPopup != p)) {

          // change pointer?
          if (currentPopup.onMouseClick != null) {
            showNormalPointer();
          }
          if (currentPopup.onMouseOut != null) {
            currentPopup.onMouseOut.run();
          }
        }

        currentPopup = p;

        // clickable? show cursor?
        if (p.onMouseClick != null) {
          showClickPointer();
        }

        // repaint if text exists
        if (p.text != null) {
          nextPopup = p;
          fireRepaint(false);
        }

        if (p.onMouseOver != null) {
          p.onMouseOver.run();
        }

        return;
      }
    }

    // if mouse is over no popup now, fire mouse out to an existing one
    if (currentPopup != null) {
      if (currentPopup.onMouseOut != null) {
        currentPopup.onMouseOut.run();
      }

      fireRepaint(false);
      currentPopup = null;
    }
  }

  protected void fireMouseOut(int x, int y) throws ChartException {
    if (currentPopup != null) {
      if (currentPopup.onMouseOut != null) {
        currentPopup.onMouseOut.run();
      }

      currentPopup = null;
      fireRepaint(false);
    }
  }

  public ChartColor getColor() {
    return color;
  }

  @Override
  public void setColor(ChartColor color) {
    this.color = color;
  }

  @Override
  public void init() {
    popups.clear();
    mouseListeners.clear();
    currentPopup = null;
    if (nextPopup != null) {
      currentPopup = nextPopup;
      nextPopup = null;
    }
  }

  @Override
  public void finish() {
    if (currentPopup != null) {
      currentPopup.display(this);
    }
  }

  @Override
  public int getTextWidth(String text) {
    if (text == null) {
      return 0;
    } else if (!text.contains("\n")) {
      return getTextLineWidth(text);
    }

    int width = 0;
    final String[] lines = StringHelper.splitString(text, "\n");
    for (String line : lines) {
      width = Math.max(getTextLineWidth(line), width);
    }
    return width;
  }

  @Override
  public int getTextHeight(String text) {
    if (text == null) {
      return 0;
    } else if (!text.contains("\n")) {
      return getTextLineHeight(text);
    }

    if (!text.contains("\n")) {
      return getTextLineHeight(text);
    }

    int height = 0;
    final String[] lines = StringHelper.splitString(text, "\n");
    for (String line : lines) {
      if (height > 0) {
        height += TEXTLINE_SPACING;
      }
      height += getTextLineHeight(line);
    }
    return height;
  }

  @Override
  public double[] getTextSize(String text, double angle) {

    final int width = getTextWidth(text);
    final int height = getTextHeight(text);
    final double a = Math.toRadians(angle);

    final double w = Math.abs(Math.cos(a) * width) + Math.abs(Math.sin(a) * height);
    final double h = Math.abs(Math.sin(a) * width) + Math.abs(Math.cos(a) * height);
    return new double[]{w, h};
  }

  @Override
  public void addListener(RendererListener l) {
    listeners.add(l);
  }

  @Override
  public void addPopup(int x, int y, int width, int height, int rotation,
    int anchor, String text, ChartFont font, Runnable onMouseOver,
    Runnable onMouseOut, Runnable onMouseClick)
  {
    if (!enableHandlers) {
      return;
    }

    final boolean hasText = (text != null) && (text.length() > 0);
    if (!hasText
      && (onMouseOver == null)
      && (onMouseOut == null)
      && (onMouseClick == null))
    {
      return;
    }

    final RectPopup p = new RectPopup();
    p.x = x;
    p.y = y;
    p.width = width;
    p.height = height;
    p.rotation = rotation;
    p.anchor = anchor;
    p.text = text;
    p.font = font;
    p.onMouseOver = onMouseOver;
    p.onMouseOut = onMouseOut;
    p.onMouseClick = onMouseClick;

    popups.add(0, p);
  }

  @Override
  public void addPopup(int x, int y, int r1, int r2, double a1, double a2,
    boolean round, String text, ChartFont font, Runnable onMouseOver,
    Runnable onMouseOut, Runnable onMouseClick)
  {
    if (!enableHandlers) {
      return;
    }

    final boolean hasText = (text != null) && (text.length() > 0);
    if (!hasText
      && (onMouseOver == null)
      && (onMouseOut == null)
      && (onMouseClick == null))
    {
      return;
    }

    final DonutPopup p = new DonutPopup();
    p.x = x;
    p.y = y;
    p.r1 = r1;
    p.r2 = r2;
    p.a1 = a1;
    p.a2 = a2;
    p.round = round;
    p.text = text;
    p.font = font;
    p.onMouseOver = onMouseOver;
    p.onMouseOut = onMouseOut;
    p.onMouseClick = onMouseClick;

    popups.add(0, p);
  }

  @Override
  public void fireRepaint(boolean buildCubes) throws ChartException {
    for (RendererListener l : listeners) {
      l.repaint(buildCubes);
    }
  }

  @Override
  public void fireOpenLink(String url, String target) {
    for (RendererListener l : listeners) {
      l.openLink(url, target);
    }
  }

  @Override
  public void enableHandlers(boolean b) {
    this.enableHandlers = b;
  }

  @Override
  public void addMouseListener(int x, int y, int width, int height, ChartMouseListener listener) {
    if (!enableHandlers) {
      return;
    }

    mouseListeners.addMouseListener(x, y, width, height, listener);
  }

  protected void prepareFillDonut(int x, int y, int r2, double a1, double a2) {
    setGradient(x - r2, y - r2, x + r2 * 2, y + r2 * 2);
  }

  protected abstract void setGradient(int x, int y, int width, int height);

  protected void prepareFillRect(int x, int y, int width, int height) {
    setGradient(x, y, width, height);
  }

  protected void prepareFillPolygon(int[] x, int[] y) {
    setGradient(getMin(x), getMin(y), getWidth(x), getWidth(y));
  }

  private int getMin(int[] v) {
    int min = Integer.MAX_VALUE;
    for (int i : v) {
      min = Math.min(min, i);
    }
    return min;
  }

  private int getWidth(int[] v) {
    int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
    for (int i : v) {
      min = Math.min(min, i);
      max = Math.min(max, i);
    }
    return max - min;
  }

  protected int findAnchor(double x, double y, int w, int h, int ax, int ay) {
    if (ay < y) {
      return NORTH;
    } else if (ax > (x + w)) {
      return EAST;
    } else if (ay > (y + h)) {
      return SOUTH;
    } else if (ax < x) {
      return WEST;
    } else {
      return CENTER;
    }
  }
}
