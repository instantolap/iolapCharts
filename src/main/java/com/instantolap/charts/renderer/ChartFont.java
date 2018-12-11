package com.instantolap.charts.renderer;

import com.instantolap.charts.renderer.util.StringHelper;


public class ChartFont {

  private static final int DEFAULT_SIZE = 11;
  private static final String DEFAULT_NAME = "Arial";

  public static final ChartFont DEFAULT_FONT = new ChartFont(DEFAULT_NAME, DEFAULT_SIZE, false);
  public static final ChartFont CHART_TITLE_FONT = new ChartFont(DEFAULT_NAME, 14, true);
  public static final ChartFont CHART_SUBTITLE_FONT = new ChartFont(DEFAULT_NAME, 12, false);
  public static final ChartFont AXIS_TITLE_FONT = new ChartFont(DEFAULT_NAME, DEFAULT_SIZE, true);
  public static final ChartFont WATERMARK_FONT = new ChartFont(DEFAULT_NAME, 50, true);

  private String name;
  private int size = 11;
  private boolean bold;
  private boolean italic;

  public ChartFont(String name) {
    final String[] parts = StringHelper.splitString(name);
    for (String p : parts) {
      p = StringHelper.trim(p);
      if (p.equalsIgnoreCase("bold")) {
        bold = true;
      } else if (p.equalsIgnoreCase("plain")) {
        bold = false;
        italic = false;
      } else if (p.equalsIgnoreCase("italic")) {
        italic = true;
      } else if (isNumber(p)) {
        size = Integer.parseInt(p);
      } else {
        this.name = p;
      }
    }
  }

  private boolean isNumber(String p) {
    try {
      Integer.parseInt(p);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  public ChartFont(String name, int size) {
    this(name, size, false, false);
  }

  public ChartFont(String name, int size, boolean bold, boolean italic) {
    this.name = name;
    this.size = size;
    this.bold = bold;
    this.italic = italic;
  }

  public ChartFont(String name, int size, boolean bold) {
    this(name, size, bold, false);
  }

  public String getName() {
    return name;
  }

  public ChartFont setName(String name) {
    this.name = name;
    return this;
  }

  public int getSize() {
    return size;
  }

  public ChartFont setSize(int size) {
    this.size = size;
    return this;
  }

  public boolean isBold() {
    return bold;
  }

  public ChartFont setBold(boolean bold) {
    this.bold = bold;
    return this;
  }

  public boolean isItalic() {
    return italic;
  }

  public ChartFont setItalic(boolean italic) {
    this.italic = italic;
    return this;
  }

  @Override
  public String toString() {
    return (italic ? "italic " : "") + (bold ? "bold " : "") + size + "px" + " " + name;
  }
}
