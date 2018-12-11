package com.instantolap.charts.renderer;

public interface RendererListener extends LinkOpener {
  void repaint(boolean buildCubes) throws ChartException;
}
