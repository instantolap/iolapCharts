package com.instantolap.charts.renderer;

public interface RendererContent extends HasAnimation {

  void setRenderer(Renderer renderer);

  void setPopup(boolean b);

  void render() throws ChartException;
}
