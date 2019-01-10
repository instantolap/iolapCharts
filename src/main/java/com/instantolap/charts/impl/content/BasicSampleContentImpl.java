package com.instantolap.charts.impl.content;

import com.instantolap.charts.*;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.impl.math.SimpleRegression;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.Renderer;
import com.instantolap.charts.renderer.impl.annotation.Annotation;
import com.instantolap.charts.renderer.impl.annotation.AnnotationDrawer;

import java.util.ArrayList;
import java.util.List;


public abstract class BasicSampleContentImpl extends BasicContentImpl
  implements SampleContent, HasValueLabels, HasValueAxis
{

  private final Palette palette;
  private final static Runnable[] NO_LINK_RUNNABLES = new Runnable[3];
  private final List<Annotation> annotations = new ArrayList<>();
  private boolean showValueLabels, showSampleLabels, showSeriesLabels, showPercentLabels;
  private boolean showPopup = true;
  private boolean showSamplePopup = true;
  private boolean showSeriesPopup = true;
  private boolean showValuePopup = true;
  private boolean showPercentPopup = false;
  private String percentLabelFormat;
  private int valueLabelType = AUTO;
  private ChartColor labelColor;
  private ChartColor shadow;
  private int shadowXOffset = 3;
  private int shadowYOffset = 3;
  private ChartColor outlineColor;
  private int labelAngle;
  private int labelSpacing = 3;
  private ChartFont labelFont;
  private ChartFont popupFont;
  private String labelValuePrefix;
  private String labelValuePostfix;
  private double shine = 0;
  private ChartColor regressionColor;
  private ChartStroke regressionStroke;
  private int usedAxis = 0;

  BasicSampleContentImpl(Palette palette) {
    this.palette = palette;
  }

  public Palette getPalette() {
    return palette;
  }

  @Override
  public int getUsedValueAxis() {
    return usedAxis;
  }

  @Override
  public void setOutline(ChartColor color) {
    this.outlineColor = color;
  }

  @Override
  public void setUsedValueAxis(int axis) {
    this.usedAxis = axis;
  }

  @Override
  public ChartColor getOutline() {
    return outlineColor;
  }

  @Override
  public boolean needsSampleLegend() {
    return false;
  }

  @Override
  public void setShine(double shine) {
    this.shine = shine;
  }

  protected int[] shift(int[] a, int o) {
    final int[] r = new int[a.length];
    for (int n = 0; n < a.length; n++) {
      r[n] = a[n] + o;
    }
    return r;
  }

  @Override
  public double getShine() {
    return shine;
  }

  protected double getRad(double start, double end, double n, int count, double progress) {
    final double rad = (n / count) * (end - start) + start;
    return rad + (1 - progress) * 0.1 * Math.PI;
  }

  protected String buildPopupText(Cube cube, int sample, int series, String text) {
    return buildPopupText(cube, sample, series, text, null, null);
  }

  protected String buildPopupText(
    Cube cube, int sample, int series, String text, Double percent, Renderer renderer)
  {
    String popup = null;

    // show value?
    if (isShowValuePopup()) {
      if (popup == null) {
        popup = "";
      }
      final String prefix = getValueLabelPrefix();
      if (prefix != null) {
        popup += prefix;
      }
      popup += text;
      final String postfix = getValueLabelPostfix();
      if (postfix != null) {
        popup += postfix;
      }
    }

    // show series?
    if (isShowSeriesPopup() && cube.getDimensionCount() >= 2) {
      final String seriesName = cube.getSample(1, series);
      if (seriesName != null) {
        if (text == null) {
          popup = "";
        } else {
          popup += "\n";
        }
        popup += seriesName;
      }
    }

    // show sample?
    if (isShowSamplePopup()) {
      final String sampleName = cube.getSample(0, sample);
      if (sampleName != null) {
        if (text == null) {
          popup = "";
        } else {
          popup += "\n";
        }
        popup += sampleName;
      }
    }

    if (isShowPercentPopup()) {
      if ((percent != null) && (renderer != null)) {
        if (text == null) {
          popup = "";
        } else {
          popup += "\n";
        }

        final String format = getPercentLabelFormat();
        if (format != null) {
          popup += renderer.format(format, percent);
        }
      }
    }

    return popup;
  }

  protected String buildLabelText(
    Cube cube, int sample, int series, String text, boolean selected)
  {
    return buildLabelText(cube, sample, series, text, selected, null, null);
  }

  protected String buildLabelText(
    Cube cube,
    int sample,
    int series,
    String text,
    boolean selected,
    Double percent,
    Renderer renderer)
  {
    String result = null;
    if (isShowSeriesLabels()) {
      result = cube.getSample(1, series);
    }

    if (isShowSampleLabels()) {
      if (result == null) {
        result = "";
      } else {
        result += ", ";
      }
      result += cube.getSample(0, sample);
    }

    if (isShowPercentLabels()) {
      if (result == null) {
        result = "";
      } else {
        result += ", ";
      }
      if ((renderer != null) && (percent != null)) {
        final String format = getPercentLabelFormat();
        if (format != null) {
          result += renderer.format(format, percent);
        }
      }
    }

    if (selected || isShowValueLabels()) {
      if (result == null) {
        result = "";
      } else {
        result += ": ";
      }
      final String prefix = getValueLabelPrefix();
      if (prefix != null) {
        result += prefix;
      }
      result += text;
      final String postfix = getValueLabelPostfix();
      if (postfix != null) {
        result += postfix;
      }
    }

    return result;
  }

  @Override
  public boolean isShowValueLabels() {
    return showValueLabels;
  }

  @Override
  public void setShowValueLabels(boolean show) {
    this.showValueLabels = show;
  }

  @Override
  public boolean isShowSeriesLabels() {
    return showSeriesLabels;
  }

  @Override
  public void setShowSeriesLabels(boolean showSeriesLabels) {
    this.showSeriesLabels = showSeriesLabels;
  }

  @Override
  public boolean isShowSampleLabels() {
    return showSampleLabels;
  }

  @Override
  public void setShowSampleLabels(boolean showSampleLabels) {
    this.showSampleLabels = showSampleLabels;
  }

  @Override
  public boolean isShowPercentLabels() {
    return showPercentLabels;
  }

  @Override
  public void setShowPercentLabels(boolean show) {
    this.showPercentLabels = show;
  }

  @Override
  public String getPercentLabelFormat() {
    return percentLabelFormat;
  }

  @Override
  public void setPercentLabelFormat(String format) {
    this.percentLabelFormat = format;
  }

  @Override
  public int getValueLabelType() {
    return valueLabelType;
  }

  @Override
  public void setValueLabelType(int type) {
    this.valueLabelType = type;
  }

  @Override
  public String getValueLabelPrefix() {
    return labelValuePrefix;
  }

  @Override
  public void setValueLabelPrefix(String prefix) {
    this.labelValuePrefix = prefix;
  }

  @Override
  public String getValueLabelPostfix() {
    return labelValuePostfix;
  }

  @Override
  public void setValueLabelPostfix(String prefix) {
    this.labelValuePostfix = prefix;
  }

  @Override
  public ChartColor getLabelColor() {
    return labelColor != null ? labelColor : getPalette().getTextColor();
  }

  @Override
  public void setLabelColor(ChartColor color) {
    this.labelColor = color;
  }

  @Override
  public int getLabelAngle() {
    return labelAngle;
  }

  @Override
  public void setLabelAngle(int angle) {
    this.labelAngle = angle;
  }

  @Override
  public int getLabelSpacing() {
    return labelSpacing;
  }

  @Override
  public void setLabelSpacing(int labelSpacing) {
    this.labelSpacing = labelSpacing;
  }

  @Override
  public ChartFont getLabelFont() {
    return labelFont;
  }

  @Override
  public void setLabelFont(ChartFont font) {
    this.labelFont = font;
  }

  @Override
  public ChartFont getPopupFont() {
    return popupFont;
  }

  @Override
  public void setPopupFont(ChartFont popupFont) {
    this.popupFont = popupFont;
  }

  @Override
  public boolean isShowPopup() {
    return showPopup;
  }

  @Override
  public void setShowPopup(boolean show) {
    this.showPopup = show;
  }

  @Override
  public boolean isShowSamplePopup() {
    return showSamplePopup;
  }

  @Override
  public void setShowSamplePopup(boolean show) {
    this.showSamplePopup = show;
  }

  @Override
  public boolean isShowSeriesPopup() {
    return showSeriesPopup;
  }

  @Override
  public void setShowSeriesPopup(boolean show) {
    this.showSeriesPopup = show;
  }

  @Override
  public boolean isShowValuePopup() {
    return showValuePopup;
  }

  @Override
  public void setShowValuePopup(boolean show) {
    this.showValuePopup = show;
  }

  @Override
  public boolean isShowPercentPopup() {
    return showPercentPopup;
  }

  @Override
  public void setShowPercentPopup(boolean show) {
    this.showPercentPopup = show;
  }

  protected String buildPercentLabelText(
    Cube cube, int sample, int series, String text, boolean selected)
  {
    String result = null;

    if (selected || isShowValueLabels()) {
      result = "%";
    }

    return result;
  }

  protected ChartColor getOutlineColor(
    double progress, double bar, ContentAnimation anim, Data data, int c1, int c0)
  {
    ChartColor outlineColor = getOutline();
    if (outlineColor == null) {
      return null;
    }
    outlineColor = anim.getSampleColor(progress, bar, outlineColor);
    outlineColor = changeSelectedColor(outlineColor, data, 1, c1);
    return outlineColor;
  }

  protected ChartColor changeSelectedColor(
    ChartColor color, Data data, int dimension, int sample)
  {
    if (!data.hasSelection(dimension)) {
      return color;
    } else if (!data.isSelected(dimension, sample)) {
      return color.setOpacity(0.33);
    } else {
      return color;
    }
  }

  protected ChartColor getCurrentShadow(
    ContentAnimation anim, double progress, double bar, Data data, int c1, int c0)
  {
    ChartColor shadowColor = getShadow();
    if (shadowColor == null) {
      return null;
    }

    shadowColor = anim.getShadowColor(progress, bar, shadowColor);
    shadowColor = changeSelectedColor(shadowColor, data, 1, c1);
    return shadowColor;
  }

  @Override
  public ChartColor getShadow() {
    return shadow;
  }

  @Override
  public void setShadow(ChartColor shadow) {
    this.shadow = shadow;
  }

  @Override
  public int getShadowXOffset() {
    return shadowXOffset;
  }

  @Override
  public void setShadowXOffset(int offset) {
    this.shadowXOffset = offset;
  }

  @Override
  public int getShadowYOffset() {
    return shadowYOffset;
  }

  @Override
  public void setShadowYOffset(int offset) {
    this.shadowYOffset = offset;
  }

  protected ChartColor getSampleColor(
    double progress, double bar, ContentAnimation anim, Data data, int c1, int c0,
    boolean multiColor)
  {
    final int index = (multiColor ? c0 : c1);
    ChartColor sampleColor = data.getColor(getColorRange(), index);
    sampleColor = anim.getSampleColor(progress, bar, sampleColor);
    if (multiColor) {
      sampleColor = changeSelectedColor(sampleColor, data, 0, c0);
    } else {
      sampleColor = changeSelectedColor(sampleColor, data, 1, c1);
    }
    return sampleColor;
  }

  protected Runnable[] getLinkCommands(final Renderer r, Cube data, int pos1, int pos2) {
    final String url = data.getString(Cube.MEASURE_LINK, pos1, pos2);
    if (url == null) {
      return NO_LINK_RUNNABLES;
    }

    final Runnable over = r::showClickPointer;
    final Runnable out = r::showNormalPointer;

    final String target = data.getString(Cube.MEASURE_TARGET, pos1, pos2);
    final Runnable click = () -> r.fireOpenLink(url, target);

    return new Runnable[]{over, out, click};
  }

  protected SimpleRegression initRegression() {
    SimpleRegression regression = null;
    if (getRegressionColor() != null) {
      regression = new SimpleRegression();
    }
    return regression;
  }

  public ChartColor getRegressionColor() {
    return regressionColor;
  }

  public void setRegressionColor(ChartColor regressionColor) {
    this.regressionColor = regressionColor;
  }

  protected void drawRegression(
    Renderer r, int x, int y, int width, int height, boolean isRotated, SimpleRegression regression)
  {
    if (regression != null) {
      final int y0 = (int) regression.predict(0);
      final int y1 = (int) regression.predict(width);
      r.setStroke(getRegressionStroke());

      final ChartColor shadow = getShadow();
      final int xOffset = getShadowXOffset();
      final int yOffset = getShadowYOffset();

      if (isRotated) {
        if (shadow != null) {
          r.setColor(shadow);
          r.drawLine(x + y0 + xOffset, y + yOffset, x + xOffset + y1, y + height + yOffset);
        }
        r.setColor(getRegressionColor());
        r.drawLine(x + y0, y, x + y1, y + height);
      } else {
        if (shadow != null) {
          r.setColor(shadow);
          r.drawLine(x + xOffset, y + y0 + yOffset, x + width + xOffset, y + y1 + yOffset);
        }
        r.setColor(getRegressionColor());
        r.drawLine(x, y + y0, x + width, y + y1);
      }

      r.resetStroke();
    }
  }

  public ChartStroke getRegressionStroke() {
    return regressionStroke;
  }

  public void setRegressionStroke(ChartStroke regressionStroke) {
    this.regressionStroke = regressionStroke;
  }

  @Override
  public void addAnnotation(
    Double x, Double y,
    Integer pos,
    Integer series,
    ChartColor foreground,
    ChartColor background,
    ChartFont font,
    String text)
  {
    final Annotation a = new Annotation();
    a.x = x;
    a.y = y;
    a.pos = pos;
    a.series = series;
    a.text = text;
    a.foreground = foreground;
    a.background = background;
    a.font = font;
    annotations.add(a);
  }

  protected void drawAnnotations(
    Renderer r,
    PositionAxis xAxis, ValueAxis yAxis,
    int x, int y,
    int width, int height,
    boolean isCentered)
  {
    for (Annotation a : annotations) {

      // fixed X position or sample?
      Integer pointerX = null;
      if (a.pos != null) {
        pointerX = x + xAxis.getSamplePosition(getCube(), a.pos);
        if (isCentered) {
          pointerX += xAxis.getSampleWidth() / 2;
        }
      }

      Integer boxX = null;
      if (a.x != null) {
        if (a.x > 0 && a.x < 1) {
          boxX = (int) (x + width * a.x);
        } else {
          boxX = a.x.intValue();
        }
      }

      // fixed Y position or value?
      Integer boxY = null;
      if (a.y != null) {
        if (a.y > 0 && a.y < 1) {
          boxY = (int) (y + height * a.y);
        } else {
          boxY = a.y.intValue();
        }
      }

      Integer pointerY = null;
      if (a.pos != null && a.series != null) {
        final Double v = getCube().get(Cube.MEASURE_VALUE, a.pos, a.series);
        if (v == null) {
          continue;
        }
        pointerY = y + yAxis.getPosition(v);
      }

      if (boxX == null && pointerX == null) {
        continue;
      } else if (boxY == null && pointerY == null) {
        continue;
      }

      // collect colors and fonts
      ChartColor foreground = ChartColor.BLACK;
      if (a.foreground != null) {
        foreground = a.foreground;
      }

      ChartColor background = ChartColor.WHITE;
      if (a.background != null) {
        background = a.background;
      }

      AnnotationDrawer.draw(
        r,
        labelFont,
        foreground, background,
        pointerX, pointerY,
        boxX, boxY,
        Renderer.NORTH,
        0,
        a.text
      );
    }
  }








}
