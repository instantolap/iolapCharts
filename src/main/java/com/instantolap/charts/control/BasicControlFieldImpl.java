package com.instantolap.charts.control;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public abstract class BasicControlFieldImpl implements ControlField {

  private final String title;
  private final int width;
  private final int height;
  private final ChartColor titleColor = ChartColor.WHITE;
  private final ChartFont titleFont = new ChartFont("Verdana", 8, true);
  private final int padding = 2;

  public BasicControlFieldImpl(String title, int width, int height) {
    this.title = title;
    this.width = width;
    this.height = height;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public void render(Renderer r, int x, int y) {

    r.clipRoundedRect(x, y, width, height, 0);

    x += padding;
    y += padding;

    r.setColor(titleColor);
    r.setFont(titleFont);
    r.drawText(x + width / 2, y, title, 0, Renderer.NORTH);

    x += padding;
    y += padding;
    final int width = getWidth() - 4 * padding;
    int height = getHeight() - 4 * padding;

    final int textHeight = r.getTextHeight(title);
    y += textHeight;
    height -= textHeight;

    renderElement(r, x, y, width, height);

    r.resetClip();
  }

  @Override
  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  protected abstract void renderElement(Renderer r, int x, int y, int width,
    int height);
}
