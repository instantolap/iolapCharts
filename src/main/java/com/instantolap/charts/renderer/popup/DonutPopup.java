package com.instantolap.charts.renderer.popup;

import com.instantolap.charts.renderer.Renderer;
import com.instantolap.charts.renderer.impl.BasicRenderer;


public class DonutPopup extends Popup {
  public double x, y, r1, r2;
  public double a1, a2;
  public boolean round;

  @Override
  public String toString() {
    return "DONUT " + x + "," + y + "," + r1 + "-->" + r2 + "," + a1 + "-->" + a2;
  }

  @Override
  public boolean isInside(Renderer r, double x, double y) {
    return r.isInDonut(x, y, this.x, this.y, r1, r2, a1, a2, round);
  }

  @Override
  public void display(BasicRenderer r) {
    if ((text == null) || (text.length() == 0)) {
      return;
    }

    final double len = (double) (r1 + r2) / 2.0;
    final double rad = (double) (a1 + a2) / 2.0;

    final double xx = Math.sin(rad) * len;
    final double yy = -Math.cos(rad) * len;

    display(r, x + xx, y + yy, Renderer.SOUTH);
  }
}
