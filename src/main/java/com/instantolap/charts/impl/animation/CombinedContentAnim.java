package com.instantolap.charts.impl.animation;

import com.instantolap.charts.renderer.ChartColor;

import java.util.ArrayList;
import java.util.List;


public class CombinedContentAnim implements ContentAnimation {

  private final List<ContentAnimation> animations = new ArrayList<>();

  public void addAnimation(ContentAnimation anim) {
    animations.add(anim);
  }

  @Override
  public double getValue(double progress, double bar, double value) {
    for (ContentAnimation a : animations) {
      value = a.getValue(progress, bar, value);
    }
    return value;
  }

  @Override
  public ChartColor getShadowColor(double progress, double bar, ChartColor color) {
    for (ContentAnimation a : animations) {
      color = a.getShadowColor(progress, bar, color);
    }
    return color;
  }

  @Override
  public ChartColor getSampleColor(double progress, double bar, ChartColor color) {
    for (ContentAnimation a : animations) {
      color = a.getSampleColor(progress, bar, color);
    }
    return color;
  }

  @Override
  public ChartColor getOutlineColor(double progress, double bar, ChartColor color) {
    for (ContentAnimation a : animations) {
      color = a.getOutlineColor(progress, bar, color);
    }
    return color;
  }

  @Override
  public ChartColor getLabelColor(double progress, double bar, ChartColor color) {
    for (ContentAnimation a : animations) {
      color = a.getLabelColor(progress, bar, color);
    }
    return color;
  }

  @Override
  public double getDistance(double progress, double bar, double distance) {
    for (ContentAnimation a : animations) {
      distance = a.getDistance(progress, bar, distance);
    }
    return distance;
  }

  @Override
  public int getX(double progress, int x, int width, int height) {
    for (ContentAnimation a : animations) {
      x = a.getX(progress, x, width, height);
    }
    return x;
  }

  @Override
  public int getY(double progress, int y, int width, int height) {
    for (ContentAnimation a : animations) {
      y = a.getX(progress, y, width, height);
    }
    return y;
  }
}
