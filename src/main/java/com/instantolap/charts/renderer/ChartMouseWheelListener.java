package com.instantolap.charts.renderer;

public interface ChartMouseWheelListener extends ChartMouseListener {

  void onMouseWheel(int x, int y, int v);
}
