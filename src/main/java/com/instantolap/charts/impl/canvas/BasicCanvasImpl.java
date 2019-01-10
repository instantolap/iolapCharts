package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.Canvas;
import com.instantolap.charts.impl.animation.CanvasAnimation;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.Renderer;


public abstract class BasicCanvasImpl implements Canvas {

  private final Palette palette;
  private ChartColor border;
  private ChartStroke borderStroke;
  private ChartColor background;
  private int roundedCorner;
  private ChartColor shadow;
  private int shadowXOffset = 3;
  private int shadowYOffset = 3;

  public BasicCanvasImpl(Palette palette) {
    this.palette = palette;
  }

  public Palette getPalette() {
    return palette;
  }

  public void render(
    CanvasAnimation anim, double progress, Renderer r, int x, int y, int width, int height)
  {
    final int roundedCorner = getRoundedCorner();

    // background
    final ChartColor background = getBackground();
    if (background != null) {

      // draw shadow
      final ChartColor shadow = getShadow();
      if (shadow != null) {
        final int xOffset = getShadowXOffset();
        final int yOffset = getShadowYOffset();
        r.setColor(shadow);
        r.fillRoundedRect(x + xOffset, y + yOffset, width, height, roundedCorner);
      }

      r.setColor(anim.getBackground(progress, background));
      r.fillRoundedRect(x, y, width, height, roundedCorner);
    }
  }

  @Override
  public ChartColor getBackground() {
    return background;
  }

  @Override
  public void setBackground(ChartColor background) {
    this.background = background;
  }

  @Override
  public ChartColor getShadow() {
    return shadow;
  }

  @Override
  public void setShadow(ChartColor shadow) {
    this.shadow = shadow;
  }

  @Override
  public int getShadowXOffset() {
    return shadowXOffset;
  }

  @Override
  public void setShadowXOffset(int offset) {
    this.shadowXOffset = offset;
  }

  @Override
  public int getShadowYOffset() {
    return shadowYOffset;
  }

  @Override
  public void setShadowYOffset(int offset) {
    this.shadowYOffset = offset;
  }

  public void postRender(
    CanvasAnimation anim, double progress, Renderer r, int x, int y, int width, int height)
  {
    // border
    ChartColor border = getBorder();
    if (border != null) {
      r.setStroke(getBorderStroke());
      border = anim.getBorder(progress, border);
      if (border != null) {
        r.setColor(border);
        r.drawRoundedRect(x, y, width, height, roundedCorner);
      }
      r.resetStroke();
    }
  }

  @Override
  public ChartColor getBorder() {
    return border;
  }

  @Override
  public void setBorder(ChartColor border) {
    this.border = border;
  }

  @Override
  public ChartStroke getBorderStroke() {
    return borderStroke;
  }

  @Override
  public void setBorderStroke(ChartStroke stroke) {
    this.borderStroke = stroke;
  }

  @Override
  public int getRoundedCorner() {
    return roundedCorner;
  }

  @Override
  public void setRoundedCorner(int arc) {
    this.roundedCorner = arc;
  }
}
