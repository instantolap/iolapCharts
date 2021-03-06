package com.instantolap.charts.impl.data;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public class Theme {

  public static final String[] DEFAULT_PALETTE = new String[]{
    "1874CD", "CD0000", "2E8B57", "ECCA06", "FF7F00", "838B83", "5D478B", "CD1076", "B22222",
    "8B6508", "8B4513", "C0C0C0", "696969", "080808"
  };

  public static final String[] DEFAULT_PALETTE_2 = new String[]{
    "fff8a3", "fae16b", "f8d753", "f3c01c", "f0b400", "a9cc8f", "82b16a", "5c9746", "3d8128",
    "1e6c0b", "b2c8d9", "779dbf", "3e75a7", "205f9a", "00488c", "bea37a", "907a52", "7a653e",
    "63522b", "332600", "f3aa79", "eb8953", "e1662a", "dc5313", "d84000", "b5b5a9", "8a8d82",
    "74796f", "5d645a", "434c43", "e6a5a4", "d6707b", "c4384f", "bc1c39", "b30023"
  };

  public static final int[] DEFAULT_SCHEME_2B = new int[]{
    4, 9, 14, 19, 24, 29, 34, 2, 7, 12, 17, 22, 27, 32, 0, 5, 10, 15, 20, 25, 30, 3, 8, 13, 18, 23,
    28, 33, 1, 6, 11, 16, 21, 26, 31
  };

  public static final Theme DEFAULT_THEME = new Theme();

  // Canvas.
  private ChartColor background = ChartColor.WHITE;

  // Chart contents.
  private ChartColor[] colors;
  private ChartColor outlineColor = ChartColor.WHITE;

  // Grid.
  private ChartColor baseLine;
  private ChartColor[] horizontalGrid = {ChartColor.LIGHT_GRAY};
  private ChartColor[] verticalGrid;

  // Text.
  private int baseFontSize = 11;
  private String baseFontName = "Arial";
  private ChartColor textColor = ChartColor.BLACK;

  // Animation
  private boolean animationEnabled = true;

  /**
   * Constructs a new set of defaults that uses {@link #DEFAULT_PALETTE_2} and {@link #DEFAULT_SCHEME_2B} for the color
   * palette.
   */
  public Theme() {
    setColors(DEFAULT_PALETTE_2, DEFAULT_SCHEME_2B);
  }

  public ChartColor getBackground() {
    return background;
  }

  public void setBackground(ChartColor background) {
    this.background = background;
  }

  public ChartColor[] getColors() {
    return colors;
  }

  private void setColors(String[] palette, int[] scheme) {
    colors = new ChartColor[scheme.length];
    for (int n = 0; n < scheme.length; n++) {
      colors[n] = new ChartColor(palette[scheme[n]], false);
    }
  }

  public void setColors(String[] palette) {
    colors = new ChartColor[palette.length];
    for (int n = 0; n < palette.length; n++) {
      colors[n] = new ChartColor(palette[n], false);
    }
  }

  public void setOutlineColor(ChartColor outlineColor) {
    this.outlineColor = outlineColor;
  }

  public ChartColor getOutlineColor() {
    return outlineColor;
  }

  public ChartColor getBaseLine() {
    return baseLine;
  }

  public void setBaseLine(ChartColor baseLine) {
    this.baseLine = baseLine;
  }

  public ChartColor[] getHorizontalGrid() {
    return horizontalGrid;
  }

  public void setHorizontalGrid(ChartColor... horizontalGrid) {
    this.horizontalGrid = horizontalGrid;
  }

  public ChartColor[] getVerticalGrid() {
    return verticalGrid;
  }

  public void setVerticalGrid(ChartColor... verticalGrid) {
    this.verticalGrid = verticalGrid;
  }

  public int getBaseFontSize() {
    return baseFontSize;
  }

  public void setBaseFontSize(int baseFontSize) {
    this.baseFontSize = baseFontSize;
  }

  public String getBaseFontName() {
    return baseFontName;
  }

  public void setBaseFontName(String baseFontName) {
    this.baseFontName = baseFontName;
  }

  public ChartColor getTextColor() {
    return textColor;
  }

  public void setTextColor(ChartColor textColor) {
    this.textColor = textColor;
  }

  public ChartFont getDefaultFont() {
    return new ChartFont(baseFontName, baseFontSize, false);
  }

  public ChartFont getTitleFont() {
    return new ChartFont(baseFontName, baseFontSize + 3, true);
  }

  public ChartFont getSubTitleFont() {
    return new ChartFont(baseFontName, baseFontSize + 1, false);
  }

  public void setAnimationEnabled(boolean animationEnabled) {
    this.animationEnabled = animationEnabled;
  }

  public boolean isAnimationEnabled() {
    return animationEnabled;
  }
}
