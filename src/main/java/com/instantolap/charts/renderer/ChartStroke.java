package com.instantolap.charts.renderer;

import com.instantolap.charts.renderer.util.StringHelper;

import java.io.Serializable;


@SuppressWarnings("serial")
public class ChartStroke implements Serializable {

  public static ChartStroke DEFAULT = new ChartStroke(1);
  public static ChartStroke HIDDEN = new ChartStroke(0);

  private double width;
  private double len1, len2;

  public ChartStroke() {
    this(1, 1, 0);
  }

  public ChartStroke(double width, double len1, double len2) {
    this.width = width;
    this.len1 = len1;
    this.len2 = len2;
  }

  public ChartStroke(String stroke) {
    width = 1;
    len1 = 1;
    len2 = 0;

    final String[] parts = StringHelper.splitString(stroke);
    for (String p : parts) {
      p = StringHelper.trim(p);
      if (p.contains("|")) {
        final String[] lens = StringHelper.splitString(p, "|");
        len1 = Integer.parseInt(lens[0]);
        len2 = Integer.parseInt(lens[1]);
      } else {
        width = Integer.parseInt(p);
      }
    }
  }

  public ChartStroke(int width) {
    this(width, 1, 0);
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public double getLen1() {
    return len1;
  }

  public double getLen2() {
    return len2;
  }

  public ChartStroke incStroke(int i) {
    return new ChartStroke(width + i, len1, len2);
  }

  public ChartStroke setPattern(int len1, int len2) {
    return new ChartStroke(width, len1, len2);
  }
}
