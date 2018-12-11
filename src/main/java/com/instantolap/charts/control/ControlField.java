package com.instantolap.charts.control;

import com.instantolap.charts.renderer.Renderer;


public interface ControlField {

  void render(Renderer renderer, int x, int y);

  int getWidth();

}
