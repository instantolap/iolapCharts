package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.Renderer;
import com.instantolap.charts.renderer.impl.BasicRenderer;


public class RectPopup extends Popup {
  public double x, y, width, height;

  @Override
  public boolean isInside(Renderer r, double x, double y) {
    if ((x >= this.x) && (x <= this.x + this.width)) {
      if ((y >= this.y) && (y <= this.y + this.height)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void display(BasicRenderer r) {
    if ((text == null) || (text.length() == 0)) {
      return;
    }

    final double x;
    final double y;
    switch (anchor) {
      case Renderer.EAST:
        x = this.x + width;
        y = this.y + height / 2;
        break;
      case Renderer.WEST:
        x = this.x;
        y = this.y + height / 2;
        break;
      case Renderer.NORTH:
        x = this.x + width / 2;
        y = this.y;
        break;
      default:
        x = this.x + width / 2;
        y = this.y + height;
        break;
    }

    display(r, x, y, anchor);
  }
}
