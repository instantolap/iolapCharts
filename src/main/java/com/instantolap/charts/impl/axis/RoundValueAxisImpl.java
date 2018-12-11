package com.instantolap.charts.impl.axis;

import com.instantolap.charts.RoundAxis;
import com.instantolap.charts.renderer.Renderer;


public class RoundValueAxisImpl extends ValueAxisImpl implements RoundAxis {

  private double startAngle = 0;
  private double stopAngle = 2 * Math.PI;
  private boolean rotateLabels = false;

  public RoundValueAxisImpl() {
    setShowBaseLine(true);
    setTickWidth(10);
    setRenderer(new RoundAxisRenderer());
  }

  @Override
  public void doZoom(Renderer r, int wheelMotion, int x, int startX, int width) {
  }

  @Override
  public void setStartAngle(double angle) {
    this.startAngle = angle;
  }

  @Override
  public void doDrag(Renderer r, int dx, int dy) {
  }

  @Override
  public double getStartAngle() {
    return startAngle;
  }

  @Override
  public double getStopAngle() {
    return stopAngle;
  }

  @Override
  public void setStopAngle(double angle) {
    this.stopAngle = angle;
  }

  @Override
  public void setRotateLabels(boolean showLabels) {
    this.rotateLabels = showLabels;
  }

  @Override
  public boolean isRotateLabels() {
    return rotateLabels;
  }


  @Override
  public void rotate(Renderer r, int v) {
    try {
      double f = Math.PI / 16;
      if (v > 0) {
        f *= -1;
      }
      startAngle += f;
      stopAngle += f;
      r.fireRepaint(true);
    }
    catch (Exception ignored) {
    }
  }
}
