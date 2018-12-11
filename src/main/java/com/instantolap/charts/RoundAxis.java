package com.instantolap.charts;

import com.instantolap.charts.renderer.Renderer;


public interface RoundAxis extends Axis {

  double getStartAngle();

  void setStartAngle(double angle);

  double getStopAngle();

  void setStopAngle(double angle);

  boolean isRotateLabels();

  void setRotateLabels(boolean rotate);

  void rotate(Renderer r, int v);
}
