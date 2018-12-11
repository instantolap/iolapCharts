package com.instantolap.charts.control;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.Renderer;


public class KnobField extends BasicControlFieldImpl {

  private final ChartColor outlineColor = ChartColor.WHITE;

  public KnobField(String title, int width, int height) {
    super(title, width, height);
  }

  @Override
  protected void renderElement(Renderer r, int x, int y, int width, int height) {
    r.setColor(outlineColor);
    final int size = Math.min(width, height);
    final int sx = (width - size) / 2;

    r.drawCircle(sx, y, size);
  }

}
