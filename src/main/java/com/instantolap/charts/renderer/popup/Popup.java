package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;
import com.instantolap.charts.renderer.impl.BasicRenderer;
import com.instantolap.charts.renderer.impl.annotation.AnnotationDrawer;


public abstract class Popup {
  public String text;
  public int rotation;
  public int anchor = Renderer.CENTER;
  public ChartFont font;
  public Runnable onMouseOver, onMouseOut, onMouseClick;

  protected void display(BasicRenderer r, int x, int y, int anchor) {
    final ChartColor foreground = ChartColor.BLACK;
    final ChartColor background = ChartColor.WHITE;
    AnnotationDrawer.draw(
      r, font, foreground, background, x, y, null, null, anchor, rotation, text
    );
  }

  public abstract boolean isInside(Renderer r, int x, int y);

  public abstract void display(BasicRenderer r);
}
