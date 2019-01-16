package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Axis;
import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public abstract class BasicAxisImpl implements Axis {

  private Theme theme;
  private String title;
  private ChartColor titleColor;
  private ChartFont titleFont;
  private int titlePadding = 10;

  private boolean showLabels = true;
  private ChartColor[] colors;
  private ChartFont font;
  private int labelRotation;
  private boolean showLabelsInside;

  private ChartColor lineColor;
  private boolean showBaseLine = false;
  private ChartColor background;
  private int titleRotation;
  private int tickWidth;
  private int labelSpacing = 5;
  private boolean[] visibleGrid;
  private boolean autoSpacingOn = true;
  private String postfix;
  private String prefix;
  private Cube cube;
  private boolean visible = true;
  private boolean showGrid = true;
  private AxisRenderer renderer = new LineAxisRenderer();
  private int radius;

  protected BasicAxisImpl(Theme theme) {
    this.theme = theme;
    colors = new ChartColor[]{theme.getTextColor()};
  }

  protected void setRenderer(AxisRenderer renderer) {
    this.renderer = renderer;
  }

  public void setData(Cube cube) {
    this.cube = cube;
  }

  public Cube getCube() {
    return cube;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  public abstract void setData(
    int height,
    Renderer r,
    int axisNum,
    boolean isStacked,
    boolean isCentered,
    boolean vertical,
    int index);

  public void render(
    Renderer r,
    int x, int y,
    int width, int height,
    boolean isCentered,
    boolean flip,
    ChartFont font) {
    renderer.render(r, this, x, y, width, height, radius, isCentered, flip, font);
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void addDisplaySample(int dimension, int sample) {
  }

  @Override
  public int[] getDisplaySamples(int dimension) {
    return new int[0];
  }

  @Override
  public ChartColor getTitleColor() {
    return titleColor == null ? theme.getTextColor() : titleColor;
  }

  protected int getNeededTitleWidth(Renderer r, boolean vertical) {
    final String title = getTitle();
    if (title == null) {
      return 0;
    }

    int neededWidth = 2 * getTitlePadding();

    r.setFont(getTitleFont());
    final double[] size = r.getTextSize(getTitle(), getTitleRotation());
    neededWidth += vertical ? size[0] : size[1];
    return neededWidth;
  }

  public Cube buildSubCube(Cube cube) {
    return cube;
  }

  @Override
  public void setTitleColor(ChartColor titleColor) {
    this.titleColor = titleColor;
  }

  public int getRadius(Renderer r, int width, int height) {
    int xRadius = width / 2;
    int yRadius = height / 2;

    if (isVisible() && isShowLabels() && !isShowLabelsInside()) {
      final String[] texts = getTexts();
      if (texts != null) {
        r.setFont(getFont());
        int maxHeight = 0, maxWidth = 0;
        for (String text : texts) {
          maxWidth = Math.max(r.getTextWidth(text), maxWidth);
          maxHeight = Math.max(r.getTextHeight(text), maxHeight);
        }
        xRadius -= maxWidth;
        yRadius -= maxHeight;
      }
    }

    radius = Math.min(xRadius, yRadius);
    if (isVisible() && !isShowLabelsInside()) {
      if (isShowLabels()) {
        radius -= getLabelSpacing();
      }
      radius -= getTickWidth();
    }
    return radius;
  }

  @Override
  public boolean isVisible() {
    return visible;// && (cube != null);
  }

  @Override
  public void setTitleFont(ChartFont font) {
    this.titleFont = font;
  }

  @Override
  public void setVisible(boolean b) {
    this.visible = b;
  }

  @Override
  public int getTitleRotation() {
    return titleRotation;
  }

  @Override
  public ChartFont getTitleFont() {
    return titleFont != null ? titleFont : theme.getSubTitleFont();
  }

  @Override
  public void setTitleRotation(int titleRotation) {
    this.titleRotation = titleRotation;
  }

  @Override
  public ChartColor getLineColor() {
    return lineColor;
  }

  @Override
  public void setLineColor(ChartColor lineColor) {
    this.lineColor = lineColor;
  }

  @Override
  public boolean isShowBaseLine() {
    return showBaseLine;
  }

  @Override
  public void setShowBaseLine(boolean showBaseLine) {
    this.showBaseLine = showBaseLine;
  }

  @Override
  public boolean isShowLabels() {
    return showLabels;
  }

  @Override
  public void setShowLabels(boolean show) {
    this.showLabels = show;
  }

  @Override
  public ChartColor getColor() {
    if ((colors == null) || (colors.length == 0)) {
      return theme.getTextColor();
    }
    return colors[0];
  }

  @Override
  public void setColor(ChartColor color) {
    this.colors = new ChartColor[]{color};
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
  public ChartFont getFont() {
    return font != null ? font : theme.getDefaultFont();
  }

  @Override
  public void setFont(ChartFont font) {
    this.font = font;
  }

  @Override
  public int getLabelRotation() {
    return labelRotation;
  }

  @Override
  public void setLabelRotation(int rotation) {
    this.labelRotation = rotation;
  }

  @Override
  public int getLabelSpacing() {
    return labelSpacing;
  }

  @Override
  public void setLabelSpacing(int spacing) {
    this.labelSpacing = spacing;
  }

  @Override
  public boolean isAutoSpacingOn() {
    return autoSpacingOn;
  }

  @Override
  public void setAutoSpacingOn(boolean autoSpacing) {
    this.autoSpacingOn = autoSpacing;
  }

  @Override
  public int getTickWidth() {
    return tickWidth;
  }

  @Override
  public void setTickWidth(int tickWidth) {
    this.tickWidth = tickWidth;
  }

  @Override
  public String getPrefix() {
    return prefix;
  }

  @Override
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public String getPostfix() {
    return postfix;
  }

  @Override
  public void setPostfix(String postfix) {
    this.postfix = postfix;
  }

  @Override
  public boolean isShowLabelsInside() {
    return showLabelsInside;
  }

  @Override
  public void setShowLabelsInside(boolean showLabelsInside) {
    this.showLabelsInside = showLabelsInside;
  }

  @Override
  public boolean[] getVisibleGrid() {
    return visibleGrid;
  }

  @Override
  public void setVisibleGrid(boolean[] visibleGrid) {
    this.visibleGrid = visibleGrid;
  }

  @Override
  public void setTitlePadding(int padding) {
    this.titlePadding = padding;
  }

  @Override
  public abstract boolean isVertical();

  @Override
  public int getTitlePadding() {
    return titlePadding;
  }

  @Override
  public boolean isShowGrid() {
    return showGrid;
  }

  @Override
  public void setShowGrid(boolean show) {
    this.showGrid = show;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  @Override
  public ChartColor getBackground() {
    return background;
  }

  @Override
  public void setBackground(ChartColor background) {
    this.background = background;
  }


}
