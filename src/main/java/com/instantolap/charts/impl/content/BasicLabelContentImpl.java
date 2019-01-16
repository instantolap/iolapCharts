package com.instantolap.charts.impl.content;

import com.instantolap.charts.LabelContent;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public abstract class BasicLabelContentImpl extends BasicContentImpl implements LabelContent {

  private final Theme theme;
  private String text;
  private double x, y;
  private ChartFont font;
  private ChartColor color;

  public BasicLabelContentImpl(Theme theme) {
    this.theme = theme;
  }

  public Theme getTheme() {
    return theme;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public double getX() {
    return x;
  }

  @Override
  public void setX(double x) {
    this.x = x;
  }

  @Override
  public double getY() {
    return y;
  }

  @Override
  public void setY(double y) {
    this.y = y;
  }

  @Override
  public ChartFont getFont() {
    return font != null ? font : getTheme().getDefaultFont();
  }

  @Override
  public void setFont(ChartFont font) {
    this.font = font;
  }

  @Override
  public ChartColor getColor() {
    return color != null ? color : getTheme().getTextColor();
  }

  @Override
  public void setColor(ChartColor color) {
    this.color = color;
  }
}
