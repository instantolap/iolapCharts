package com.instantolap.charts.impl.legend;

import com.instantolap.charts.Data;
import com.instantolap.charts.Legend;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.Renderer;


public abstract class BasicLegendImpl implements Legend {

  private ChartFont font;
  private ChartColor color = null;
  private ChartColor border = ChartColor.BLACK;
  private ChartStroke borderStroke = null;
  private ChartColor background = ChartColor.WHITE;
  private ChartColor[] colors = null;
  private double spacing = 10, padding = 5;
  private boolean isVertical;
  private double roundedCorner = 10;
  private ChartColor shadow;
  private double shadowXOffset = 3;
  private double shadowYOffset = 3;
  private int dimension = 1;
  private boolean reverse = false;
  private String[] labels;

  @Override
  public ChartFont getFont() {
    return font;
  }

  @Override
  public void setFont(ChartFont font) {
    this.font = font;
  }

  @Override
  public ChartColor getColor() {
    return color;
  }

  @Override
  public void setColor(ChartColor color) {
    this.color = color;
  }

  @Override
  public ChartColor[] getColors() {
    return colors;
  }

  @Override
  public void setColors(ChartColor[] colors) {
    this.colors = colors;
  }

  @Override
  public double getPadding() {
    return padding;
  }

  @Override
  public void setPadding(double padding) {
    this.padding = padding;
  }

  @Override
  public double getSpacing() {
    return spacing;
  }

  @Override
  public void setSpacing(double spacing) {
    this.spacing = spacing;
  }

  @Override
  public void setShadow(ChartColor shadow) {
    this.shadow = shadow;
  }

  @Override
  public boolean isVertical() {
    return isVertical;
  }

  @Override
  public ChartColor getShadow() {
    return shadow;
  }

  @Override
  public void setVertical(boolean vertical) {
    this.isVertical = vertical;
  }

  @Override
  public void setShadowXOffset(double offset) {
    this.shadowXOffset = offset;
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  @Override
  public double getShadowXOffset() {
    return shadowXOffset;
  }

  @Override
  public void setDimension(int dimension) {
    this.dimension = dimension;
  }

  @Override
  public void setShadowYOffset(double offset) {
    this.shadowYOffset = offset;
  }

  @Override
  public String[] getLabels() {
    return this.labels;
  }

  @Override
  public double getShadowYOffset() {
    return shadowYOffset;
  }

  @Override
  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  @Override
  public boolean isReverse() {
    return reverse;
  }

  @Override
  public void setReverse(boolean reverse) {
    this.reverse = reverse;
  }

  @Override
  public ChartColor getBackground() {
    return background;
  }

  @Override
  public void setBackground(ChartColor background) {
    this.background = background;
  }

  @Override
  public ChartColor getBorder() {
    return border;
  }

  @Override
  public void setBorder(ChartColor border) {
    this.border = border;
  }

  @Override
  public ChartStroke getBorderStroke() {
    return borderStroke;
  }

  @Override
  public void setBorderStroke(ChartStroke stroke) {
    this.borderStroke = stroke;
  }

  @Override
  public double getRoundedCorner() {
    return roundedCorner;
  }

  @Override
  public void setRoundedCorner(double arc) {
    this.roundedCorner = arc;
  }

  public abstract void setData(Data data);

  public abstract void render(
    double progress,
    Renderer r,
    double x, double y,
    double width, double height,
    ChartColor foreground, ChartColor background,
    ChartFont font);

  public abstract double[] getNeededSize(Renderer r, double max, ChartFont font);


}
