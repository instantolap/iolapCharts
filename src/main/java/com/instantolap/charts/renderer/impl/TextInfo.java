package com.instantolap.charts.renderer.impl;

public class TextInfo {
  public double rad;
  public double w, h, tw, th;
  public double x, y;
  public double tx, ty;
  public double rx, ry;

  public boolean overlaps(TextInfo other) {
    return x < other.x + other.w
      && y < other.y + other.h
      && x + w > other.x
      && y + h > other.y;
  }
}
