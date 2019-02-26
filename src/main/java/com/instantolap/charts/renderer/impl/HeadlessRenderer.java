package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.impl.data.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;


public class HeadlessRenderer extends BasicGraphics2dRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessRenderer.class);

  private BufferedImage image;

  @Override
  public void setSize(double width, double height) {
    image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);

    final Graphics2D graphics = (Graphics2D) image.getGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    setGraphics(graphics);
    setFont(new Theme().getDefaultFont());
  }

  @Override
  public double getWidth() {
    return image.getWidth();
  }

  @Override
  public double getHeight() {
    return image.getHeight();
  }

  public BufferedImage getImage() {
    return image;
  }
}
