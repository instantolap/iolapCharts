package com.instantolap.charts.factories;

import com.instantolap.charts.*;
import com.instantolap.charts.control.RoundChart;
import com.instantolap.charts.impl.chart.RoundChartImpl;
import com.instantolap.charts.impl.chart.SampleChartImpl;
import com.instantolap.charts.impl.content.*;
import com.instantolap.charts.impl.data.transform.StackedTransform;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.util.StringHelper;

import java.util.Map;


public class EasyChartFactory {

  public static Chart create(Map<String, String> a, Data data, int overlaySeries) throws Exception {

    final String type = a.get("format");
    final Chart chart;
    if (type == null) {
      chart = null;
    } else if (type.equalsIgnoreCase("bar")) {
      chart = createBarChart(a, data);
    } else if (type.equalsIgnoreCase("roundbar")) {
      chart = createRoundBarChart(a, data);
    } else if (type.equalsIgnoreCase("rose")) {
      chart = createRoundBarChart(a, data);
    } else if (type.equalsIgnoreCase("line")) {
      chart = createLineChart(a, data, false, false);
    } else if (type.equalsIgnoreCase("spline")) {
      chart = createLineChart(a, data, true, false);
    } else if (type.equalsIgnoreCase("stepline")) {
      chart = createLineChart(a, data, false, true);
    } else if (type.equalsIgnoreCase("area")) {
      chart = createLineChart(a, data, false, false);
      final LineContent content = (LineContent) chart.getContents().get(0);
      content.setAreaChart(true);
    } else if (type.equalsIgnoreCase("pie")) {
      chart = createPieChart(a, data);
    } else if (type.equalsIgnoreCase("doughnut")) {
      chart = createDoughnutChart(a, data);
    } else if (type.equalsIgnoreCase("radar")) {
      chart = createRoundLineChart(a, data, true);
    } else if (type.equalsIgnoreCase("spider")) {
      chart = createRoundLineChart(a, data, false);
    } else if (type.equalsIgnoreCase("meter")) {
      chart = createMeterChart(a, data);
    } else if (type.equalsIgnoreCase("heatmap")) {
      chart = createHeatmapChart(a, data);
      // } else if (type.equalsIgnoreCase("scatter")) {
      // chart = createScatterChart(a, data);
      // } else if (type.equalsIgnoreCase("bubble")) {
      // chart = createBubbleChart(a, data);
    } else if (type.equalsIgnoreCase("bar_line")) {
      chart = createBarChart(a, data);
      addLineOverlay(chart.getContents().get(0), a, data, chart, false, false, overlaySeries);
    } else if (type.equalsIgnoreCase("bar_spline")) {
      chart = createBarChart(a, data);
      addLineOverlay(chart.getContents().get(0), a, data, chart, true, false, overlaySeries);
    } else if (type.equalsIgnoreCase("bar_stepline")) {
      chart = createBarChart(a, data);
      addLineOverlay(chart.getContents().get(0), a, data, chart, false, true, overlaySeries);
    } else {
      throw new Exception("Unknown chart format '" + type + "'");
    }

    chart.setData(data);
    return chart;
  }

  private static void addLineOverlay(
    HasSamples oldContent,
    Map<String, String> a,
    Data data,
    Chart chart,
    boolean interpolated,
    boolean stepline,
    int overlaySeries)
  {

    final LineContent content = new LineContentImpl(data.getTheme());
    chart.addContent(content);
    content.setInterpolated(interpolated);
    content.setStepLine(stepline);

    final Cube cube = data.getCurrentCube();
    final int count = cube.getSampleCount(1);
    for (int s = 0; s < count; s++) {
      if (s >= overlaySeries) {
        content.addDisplaySample(1, s);
      } else {
        oldContent.addDisplaySample(1, s);
      }
    }

    addOverlayColors(data, content, a);
    initLineContent(a, "overlay_", content, data);
  }

  private static void addOverlayColors(
    Data data, HasSampleColors hasColors, Map<String, String> a)
  {
    // add overlay colors
    final ChartColor[] overlaySampleColors = parseColors(a, "overlay_samplecolors");
    if (overlaySampleColors != null) {
      data.setColors(1, overlaySampleColors);
      hasColors.setColorRange(1);
    }
  }

  private static Chart createBarChart(Map<String, String> a, Data data) {
    final SampleChart chart = createSampleChart(a, data);
    initBarChart(a, chart, data);

    final BarContent content = new BarContentImpl(data.getTheme());
    chart.addContent(content);
    initBarContent(content, a);

    return chart;
  }

  private static Chart createPieChart(Map<String, String> a, Data data) {
    final RoundChart chart = createRoundChart(a, data, true);

    chart.getCanvas().setGrid(null);
    chart.getCanvas().setScaleBackground(null);
    chart.getCanvas().setScaleBackground2(null);
    chart.getSampleAxis().setVisible(false);
    chart.getScaleAxis().setVisible(false);

    // content
    final PieContent content = new PieContentImpl(data.getTheme());
    chart.addContent(content);
    initPieContent(content, a);

    return chart;
  }

  private static Chart createDoughnutChart(Map<String, String> a, Data data) {
    final Chart chart = createPieChart(a, data);
    final PieContent content = (PieContent) chart.getContents().get(0);
    content.setSeriesSpace(0.5);
    return chart;
  }

  private static Chart createMeterChart(Map<String, String> a, Data data) {
    final RoundChart chart = createRoundChart(a, data, true);

    chart.getCanvas().setGrid(null);
    chart.getCanvas().setScaleBackground(null);
    chart.getCanvas().setScaleBackground2(null);
    chart.setInnerBorder(ChartColor.LIGHT_GRAY);
    chart.setInnerBorderStroke(new ChartStroke(10));
    chart.setInnerPadding(10);
    chart.getSampleAxis().setVisible(false);

    // content
    final MeterContent content = new MeterContentImpl(data.getTheme());
    chart.addContent(content);
    initMeterContent(chart, content, a);

    return chart;
  }

  private static Chart createHeatmapChart(Map<String, String> a, Data data) {
    final RoundChart chart = createRoundChart(a, data, true);

    // content
    final HeatMapContent content = new HeatMapContentImpl(data.getTheme());
    chart.addContent(content);

    return chart;
  }

  private static Chart createRoundBarChart(Map<String, String> a, Data data) {
    final RoundChart chart = createRoundChart(a, data, false);

    chart.getScaleAxis().setLabelRotation(0);

    final String type = a.get("bartype");
    if ("stacked".equalsIgnoreCase(type)) {
      transformStacked(data);
      chart.setStacked(true);
    } else if ("behind".equalsIgnoreCase(type)) {
      chart.setStacked(true);
    }

    // content
    final BarContent content = new RoundBarContentImpl(data.getTheme());
    chart.addContent(content);

    initBarContent(content, a);

    return chart;
  }

  private static void transformStacked(Data data) {
    final Transformation transformation = new StackedTransform();
    data.setCurrentCube(transformation.transform(data.getCurrentCube()));
  }

  private static Chart createLineChart(
    Map<String, String> a, Data data, boolean interpolated, boolean steps)
  {
    final SampleChart chart = createSampleChart(a, data);

    // in easycharts, areacharts are always stacked
    final String type = a.get("stackedon");
    if ("true".equalsIgnoreCase(type)) {
      transformStacked(data);
      chart.setStacked(true);
    }

    final LineContent content = new LineContentImpl(data.getTheme());
    chart.addContent(content);
    content.setInterpolated(interpolated);
    content.setStepLine(steps);
    initLineContent(a, "", content, data);

    return chart;
  }

  private static Chart createRoundLineChart(Map<String, String> a, Data data, boolean round) {
    final RoundChart chart = createRoundChart(a, data, false);

    // in easycharts, areacharts are always stacked
    final String type = a.get("stackedon");
    if ("true".equalsIgnoreCase(type)) {
      transformStacked(data);
      chart.setStacked(true);
    }

    // spider / radar?
    chart.getCanvas().setRound(round);

    // add content
    final LineContent content = new RoundLineContentImpl(data.getTheme());
    chart.addContent(content);
    initLineContent(a, "", content, data);

    return chart;
  }

  private static SampleChart createSampleChart(Map<String, String> a, Data data) {
    final SampleChart chart = new SampleChartImpl(data.getTheme());
    initChart(chart, a, data);
    initSampleChart(chart, a, data);

    return chart;
  }

  private static RoundChart createRoundChart(
    Map<String, String> a, Data data, boolean scaleOutside)
  {
    final RoundChart chart = new RoundChartImpl(data.getTheme(), scaleOutside);
    initChart(chart, a, data);

    // colors
    final String background = a.get("chartbackground");
    if (background != null) {
      chart.getCanvas().setBackground(new ChartColor(background));
    }

    final String foreground = a.get("chartforeground");
    if (foreground != null) {
    }

    final ChartStroke stroke = new ChartStroke(1, 2, 2);
    chart.getCanvas().setGridStroke(stroke);
    chart.getCanvas().setGrid(new ChartColor("#ddd"));
    chart.getCanvas().setScaleBackground2(new ChartColor(0xee, 0xee, 0xee, 0xc0));

    return chart;
  }

  private static void initChart(Chart chart, Map<String, String> a, Data data) {
    // title
    final String title = a.get("charttitle");
    if (title != null) {
      chart.setTitle(title);
    }

    final String titleFont = a.get("titlefont");
    if (titleFont != null) {
      chart.setTitleFont(new ChartFont(titleFont));
    }

    // basic colors and font
    final String background = a.get("background");
    if (background != null) {
      chart.setBackground(new ChartColor(background));
    }

    final String foreground = a.get("foreground");
    if (foreground != null) {
      chart.setForeground(new ChartColor(foreground));
    }

    final String font = a.get("font");
    if (font != null) {
      chart.setFont(new ChartFont(font));
    }

    // insets
    final int[] graphInsets = parseInts(a, "graphinsets");
    if (graphInsets != null) {
      chart.setContentInsets(graphInsets[0], graphInsets[1], graphInsets[2], graphInsets[3]);
    }

    // colors
    final ChartColor[] sampleColors = parseColors(a, "samplecolors");
    if (sampleColors != null) {
      data.setColors(0, sampleColors);
    }

    final String gradientSampleColor = a.get("gradientsampleson");
    if ("true".equalsIgnoreCase(gradientSampleColor)) {
      final ChartColor[] colors = data.getColors(0);
      for (int n = 0; n < colors.length; n++) {
        colors[n] = colors[n].setGradient(true);
      }
      data.setColors(0, colors);
    }

    // legend
    final String legendOn = a.get("legendon");
    if ("true".equalsIgnoreCase(legendOn)) {
      chart.setShowLegend(true);
    } else {
      chart.setShowLegend(false);
    }

    final String legendPosition = a.get("legendposition");
    if ("top".equalsIgnoreCase(legendPosition)) {
      chart.setLegendPosition(Chart.NORTH);
    } else if ("left".equalsIgnoreCase(legendPosition)) {
      chart.setLegendPosition(Chart.WEST);
    } else if ("right".equalsIgnoreCase(legendPosition)) {
      chart.setLegendPosition(Chart.EAST);
    } else {
      chart.setLegendPosition(Chart.SOUTH);
    }

    final String legendFont = a.get("legendfont");
    if (legendFont != null) {
      chart.getLegend().setFont(new ChartFont(legendFont));
    }

    ChartColor[] legendColors = parseColors(a, "legendcolors");
    if (legendColors == null) {
      legendColors = parseColors(a, "samplelabelcolors");
    }
    if (legendColors != null) {
      chart.getLegend().setColors(legendColors);
    }

    final String legendLabels = a.get("legendlabels");
    if (legendLabels != null) {
      chart.getLegend().setLabels(StringHelper.splitString(legendLabels));
    }

    final String legendReverseOn = a.get("legendreverseon");
    if (legendReverseOn != null) {
      chart.getLegend().setReverse("true".equalsIgnoreCase(legendReverseOn));
    }

    // animation
    final String animationOn = a.get("animationon");
    if (legendReverseOn != null) {
      chart.setAnimationEnabled("true".equalsIgnoreCase(animationOn));
    }

    final String animationTime = a.get("animationtime");
    if (animationTime != null) {
      chart.setAnimationTime(Long.parseLong(animationTime));
    }
  }

  private static void initSampleChart(SampleChart chart, Map<String, String> a, Data data) {

    chart.getSampleAxis().setLabelRotation(0);
    chart.getScaleAxis().setLabelRotation(0);

    // colors
    final String background = a.get("chartbackground");
    if (background != null) {
      chart.getCanvas().setBackground(new ChartColor(background));
    }

    final String foreground = a.get("chartforeground");
    if (foreground != null) {
    }

    // sample axis
    final SampleAxis sampleAxis = chart.getSampleAxis();
    final ValueAxis scaleAxis = chart.getScaleAxis();
    final ValueAxis scaleAxis2 = chart.getScaleAxis2();

    scaleAxis.setTickWidth(3);
    scaleAxis2.setTickWidth(3);
    sampleAxis.setTickWidth(3);

    scaleAxis.setShowBaseLine(true);
    scaleAxis2.setShowBaseLine(true);
    sampleAxis.setShowBaseLine(true);

    // only display sample labels if the style is "below"
    sampleAxis.setShowLabels(false);
    initSampleAxisLabels(a, "sample", sampleAxis);
    initSampleAxisLabels(a, "bar", sampleAxis);

    // sample axis title
    final String sampleAxisLabel = a.get("sampleaxislabel");
    if (sampleAxisLabel != null) {
      sampleAxis.setTitle(sampleAxisLabel);

      final String sampleAxisLabelFont = a.get("sampleaxislabelfont");
      if (sampleAxisLabelFont != null) {
        sampleAxis.setTitleFont(new ChartFont(sampleAxisLabelFont));
      }

      final String sampleAxisLabelAngle = a.get("sampleaxislabelangle");
      if (sampleAxisLabelAngle != null) {
        sampleAxis.setTitleRotation(Integer.parseInt(sampleAxisLabelAngle));
      }
    }

    final String autospacingOn = a.get("autolabelspacingon");
    if (autospacingOn != null) {
      final boolean on = "true".equalsIgnoreCase(autospacingOn);
      sampleAxis.setAutoSpacingOn(on);
    } else {
      sampleAxis.setAutoSpacingOn(false);
    }

    // ranges
    initScaleAxis(a, scaleAxis, "");
    initScaleAxis(a, chart.getScaleAxis2(), "_2");

    // value lines
    final String valueLinesOn = a.get("valuelineson");
    if ("true".equalsIgnoreCase(valueLinesOn)) {

      String valueLinesColor = a.get("valuelinescolor");
      if (valueLinesColor == null) {
        valueLinesColor = "#ddd";
      }

      chart.getCanvas().setHorizontalGrid(new ChartColor(valueLinesColor));
    } else {
      chart.getCanvas().setHorizontalGrid((ChartColor[]) null);
    }

    // default grid (sample)
    final String defaultGrid = a.get("defaultgridlineson");
    if ("true".equalsIgnoreCase(defaultGrid)) {
      String color = a.get("defaultgridlinescolor");
      if (color == null) {
        color = "#ddd";
      }
      chart.getCanvas().setVerticalGrid(new ChartColor(color));
    } else {
      chart.getCanvas().setVerticalGrid((ChartColor[]) null);
    }

    final ChartColor[] gridColors = parseColors(a, "gridlinecolors");
    if (gridColors != null) {
      chart.getCanvas().setVerticalGrid(gridColors);
    }

    final String gridLines = a.get("gridlines");
    if (gridLines != null) {
      final String[] parts = StringHelper.splitString(gridLines, ",");
      final double[] positions = new double[parts.length];
      for (int n = 0; n < parts.length; n++) {
        positions[n] = Double.parseDouble(parts[n]);
      }
      chart.getSampleAxis().setGridPositions(positions);
    }

    // target value line
    final ValueAxis axis = chart.getScaleAxis();
    initTargetLines(a, axis, "");

    ValueAxis axis2 = axis;
    if ("2".equals(a.get("overlay_seriesrange_0"))) {
      axis2 = chart.getScaleAxis2();
    }
    initTargetLines(a, axis2, "overlay_");
  }

  private static void initTargetLines(Map<String, String> a, ValueAxis axis, String p) {
    if (axis != null) {
      for (int n = 0; n < Integer.MAX_VALUE; n++) {
        final String line = a.get(p + "targetvalueline_" + n);
        if (line == null) {
          break;
        }

        final String[] parts = StringHelper.splitString(line);
        if (parts.length < 2) {
          continue;
        }

        final String text = parts[0];
        final Double value = Double.parseDouble(parts[1]);
        ChartColor color = ChartColor.BLACK;
        if (parts.length >= 3) {
          color = new ChartColor(parts[2]);
        }

        axis.addTargetLine(value, text, color, null, null);
      }
    }
  }

  private static void initScaleAxis(Map<String, String> a, ValueAxis scaleAxis, String postfix) {

    final String rangeOn = a.get("rangeon" + postfix);
    if ("false".equalsIgnoreCase(rangeOn)) {
      scaleAxis.setVisible(false);
    } else if ("true".equalsIgnoreCase(rangeOn)) {
      scaleAxis.setVisible(true);
    }

    final String rangeColor = a.get("rangecolor" + postfix);
    if (rangeColor != null) {
      final ChartColor color = new ChartColor(rangeColor);
      scaleAxis.setColor(color);
      scaleAxis.setTitleColor(color);
    }

    final String range = a.get("range" + postfix);
    if (range != null) {
      scaleAxis.setMax(Double.parseDouble(range));
    }

    final String lowerRange = a.get("lowerrange" + postfix);
    if (lowerRange != null) {
      scaleAxis.setMin(Double.parseDouble(lowerRange));
    }

    final String rangeInterval = a.get("rangeinterval" + postfix);
    if (rangeInterval != null) {
      scaleAxis.setUserTick(Double.parseDouble(rangeInterval));
    }

    String rangeDecimalCount = a.get("rangedecimalcount" + postfix);
    if (rangeDecimalCount == null) {
      rangeDecimalCount = a.get("sampledecimalcount" + postfix);
    }
    if (rangeDecimalCount != null) {
      scaleAxis.setDecimalCount(Integer.parseInt(rangeDecimalCount));
    }

    final String maxValueLineCount = a.get("maxvaluelinecount" + postfix);
    if (maxValueLineCount != null) {
      scaleAxis.setMaxLineCount(Integer.parseInt(maxValueLineCount));
    }

    final String rangeLabelsOff = a.get("rangelabelsoff" + postfix);
    if ("true".equalsIgnoreCase(rangeLabelsOff)) {
      scaleAxis.setShowLabels(false);
    } else {
      scaleAxis.setShowLabels(true);
    }

    final String rangeLabelFont = a.get("rangelabelfont" + postfix);
    if (rangeLabelFont != null) {
      scaleAxis.setFont(new ChartFont(rangeLabelFont));
    }

    final String rangeAxisLabel = a.get("rangeaxislabel" + postfix);
    if (rangeAxisLabel != null) {
      scaleAxis.setTitle(rangeAxisLabel);
    }

    final String rangeAxisLabelFont = a.get("rangeaxislabelfont" + postfix);
    if (rangeAxisLabelFont != null) {
      scaleAxis.setTitleFont(new ChartFont(rangeAxisLabelFont));
    }

    final String rangeAxisLabelAngle = a.get("rangeaxislabelangle" + postfix);
    if (rangeAxisLabelAngle != null) {
      scaleAxis.setTitleRotation(Integer.parseInt(rangeAxisLabelAngle));
    }

    final String rangeLabelPrefix = a.get("rangelabelprefix" + postfix);
    if (rangeLabelPrefix != null) {
      scaleAxis.setPrefix(rangeLabelPrefix);
    }

    final String rangeLabelPostfix = a.get("rangelabelpostfix" + postfix);
    if (rangeLabelPostfix != null) {
      scaleAxis.setPostfix(rangeLabelPostfix);
    }
  }

  private static void initSampleAxisLabels(Map<String, String> a, String p, SampleAxis axis) {
    final String sampleLabelsOn = a.get(p + "labelson");
    if ("true".equalsIgnoreCase(sampleLabelsOn)) {
      final String sampleLabelStyle = a.get(p + "labelstyle");
      if ((sampleLabelStyle == null)
        || ("below".equalsIgnoreCase(sampleLabelStyle))
        || ("below_and_floating".equalsIgnoreCase(sampleLabelStyle)))
      {
        axis.setShowLabels(true);

        final String sampleLabelAngle = a.get(p + "labelangle");
        if (sampleLabelAngle != null) {
          axis.setLabelRotation(Integer.parseInt(sampleLabelAngle));
        }

        final String sampleLabelFont = a.get(p + "labelfont");
        if (sampleLabelFont != null) {
          axis.setFont(new ChartFont(sampleLabelFont));
        }

        final ChartColor[] sampleLabelColors = parseColors(a, p + "labelcolors");
        if (sampleLabelColors != null) {
          axis.setColors(sampleLabelColors);
        }

        if (!p.equals("sample")) {
          final String sampleLabels = a.get(p + "labels");
          if (sampleLabels != null) {
            axis.setLabels(StringHelper.splitString(sampleLabels));
          }
        }
      }
    }
  }

  private static void initLineContent(
    Map<String, String> a, String p, LineContent content, Data data)
  {
    initHasValueLabels(content, a);
    initHasAnnotations(content, a);

    // in easycharts, areacharts are always stacked
    final String type = a.get(p + "stackedon");
    if ("true".equalsIgnoreCase(type)) {
      transformStacked(data);
      content.setAreaChart(true);
    }

    final String connected = a.get(p + "connectedlineson");
    if ("true".equalsIgnoreCase(connected)) {
      content.setConnected(true);
    }

    final int[] lineWidth = parseInts(a, p + "linewidth");
    if (lineWidth != null) {
      for (int n = 0; n < lineWidth.length; n++) {
        final ChartStroke stroke = new ChartStroke(lineWidth[n]);
        if (n == 0) {
          data.setStroke(-1, stroke);
        }
        data.setStroke(n, stroke);
      }
    }

    final String seriesLineOff = a.get(p + "serieslineoff");
    if ("true".equalsIgnoreCase(seriesLineOff)) {
      final ChartStroke stroke = ChartStroke.HIDDEN;
      data.setStroke(-1, stroke);
    }

    final String lineStroke = a.get(p + "linestroke");
    if (lineStroke != null) {
      final String[] strokes = StringHelper.splitString(lineStroke);
      for (int n = 0; n < strokes.length; n++) {
        final String[] lens = StringHelper.splitString(strokes[n], "|");
        int len1 = 1, len2 = 0;
        if (lens.length == 1) {
          len1 = Integer.parseInt(lens[0]);
          len2 = len1;
        } else if (lens.length > 1) {
          len1 = Integer.parseInt(lens[0]);
          len2 = Integer.parseInt(lens[1]);
        }
        final ChartStroke stroke = data.getStroke(n);
        data.setStroke(n, stroke.setPattern(len1, len2));
      }
    }

    // sample highlights
    final String sampleHighlightOn = a.get(p + "samplehighlighton");
    if ("true".equalsIgnoreCase(sampleHighlightOn)) {

      final String sampleHighlightSize = a.get(p + "samplehighlightsize");
      if (sampleHighlightSize != null) {
        final int size = Integer.parseInt(sampleHighlightSize);
        data.setSymbolSize(-1, size);
      } else {
        data.setSymbolSize(-1, 8);
      }

      final String sampleHighlightStyle = a.get(p + "samplehighlightstyle");
      if (sampleHighlightStyle != null) {
        int style = SymbolDrawer.SYMBOL_CIRCLE;
        if ("circle".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_CIRCLE;
        } else if ("circle_opaque".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_CIRCLE_OPAQUE;
        } else if ("circle_filled".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_CIRCLE_FILLED;
        } else if ("square".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_SQUARE;
        } else if ("square_opaque".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_SQUARE_OPAQUE;
        } else if ("square_filled".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_SQUARE_FILLED;
        } else if ("diamond".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_DIAMOND;
        } else if ("diamond_opaque".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_DIAMOND_OPAQUE;
        } else if ("diamond_filled".equalsIgnoreCase(sampleHighlightStyle)) {
          style = SymbolDrawer.SYMBOL_DIAMOND_FILLED;
        }
        data.setSymbol(-1, style);
      }
    } else {
      data.setSymbolSize(-1, 0);
    }

    // area colors
    for (int n = 0; n < Integer.MAX_VALUE; n++) {
      final String area = a.get(p + "area_" + n);
      if (area == null) {
        break;
      }
      try {
        final String[] areaData = StringHelper.splitString(area);
        final int start = Integer.parseInt(areaData[0]);
        final int end = Integer.parseInt(areaData[1]);
        final ChartColor color = new ChartColor(areaData[2]);
        content.setFillColor(start, end, color);
      }
      catch (Exception ignore) {
      }
    }

    // shadow
    final String shadowOn = a.get(p + "shadowon");
    if (!"true".equalsIgnoreCase(shadowOn)) {
      content.setShadow(null);
    }

    // series range
    final String seriesRange = a.get(p + "seriesrange_0");
    if (seriesRange != null) {
      if ("2".equalsIgnoreCase(seriesRange)) {
        content.setUsedValueAxis(1);
      } else {
        content.setUsedValueAxis(0);
      }
    }
  }

  private static void initBarChart(Map<String, String> a, SampleChart chart, Data data) {
    // bars
    final String type = a.get("bartype");
    if ("stacked".equalsIgnoreCase(type)) {
      transformStacked(data);
      chart.setStacked(true);
    } else if ("behind".equalsIgnoreCase(type)) {
      chart.setStacked(true);
    }

    final String alignment = a.get("baralignment");
    if ("vertical".equalsIgnoreCase(alignment)) {
      chart.setRotated(false);
    } else if ("horizontal".equalsIgnoreCase(alignment)) {
      chart.setRotated(true);
    }
  }

  private static void initBarContent(BarContent content, Map<String, String> a) {
    initHasValueLabels(content, a);
    initHasAnnotations(content, a);

    // bars
    final String outlineColor = a.get("baroutlinecolor");
    if (outlineColor != null) {
      content.setOutline(new ChartColor(outlineColor));
    }

    final String outlineOff = a.get("baroutlineoff");
    if ("true".equalsIgnoreCase(outlineOff)) {
      content.setOutline(null);
    }

    final String barWidth = a.get("barwidth");
    if (barWidth != null) {
      content.setBarWidth(Double.parseDouble(barWidth));
    }

    // bar labels
    final String barLabelsOn = a.get("barlabelson");
    if ("true".equalsIgnoreCase(barLabelsOn)) {
    }

    // multi color
    final String multiColorOn = a.get("multicoloron");
    if ("true".equalsIgnoreCase(multiColorOn)) {
      content.setMultiColor(true);
    }

    // shadow
    final String shadowOn = a.get("shadowon");
    if (!"true".equalsIgnoreCase(shadowOn)) {
      content.setShadow(null);
    }
  }

  private static void initHasAnnotations(HasAnnotations content, Map<String, String> a) {
    // free labels
    for (int n = 0; n < Integer.MAX_VALUE; n++) {
      final String label = a.get("label_" + n);
      if (label == null) {
        break;
      }
      final String[] labelData = StringHelper.splitString(label);
      final String text = labelData[0];
      final Double x = Double.parseDouble(labelData[1]);
      final Double y = Double.parseDouble(labelData[2]);

      Integer pos = null, series = null;
      if (labelData.length > 3) {
        pos = Integer.parseInt(labelData[3]);
        if (labelData.length > 4) {
          series = Integer.parseInt(labelData[4]);
        }
      }

      content.addAnnotation(x, y, pos, series, null, null, null, text);
    }

  }

  private static void initHasValueLabels(HasValueLabels content, Map<String, String> a)
  {

    // value labels
    final String valueLabelsOn = a.get("valuelabelson");
    if ("true".equalsIgnoreCase(valueLabelsOn)) {
      final String valueLabelStyle = a.get("valuelabelstyle");

      // floating
      if ("floating".equalsIgnoreCase(valueLabelStyle)) {
        content.setShowValuePopup(true);
      }

      // inside
      if ((valueLabelStyle == null)
        || ("outside".equalsIgnoreCase(valueLabelStyle))
        || ("inside".equalsIgnoreCase(valueLabelStyle)))
      {

        content.setShowValueLabels(true);

        final String valueLabelAngle = a.get("valuelabelangle");
        if (valueLabelAngle != null) {
          content.setLabelAngle(Integer.parseInt(valueLabelAngle));
        }

        final String valueLabelFont = a.get("valuelabelfont");
        if (valueLabelFont != null) {
          content.setLabelFont(new ChartFont(valueLabelFont));
        }
      }

      content.setValueLabelPrefix(a.get("valuelabelprefix"));
      content.setValueLabelPostfix(a.get("valuelabelpostfix"));
    } else {
      content.setShowValueLabels(false);
    }

    final String sampleLabelsOn = a.get("samplelabelson");
    if ("true".equalsIgnoreCase(sampleLabelsOn)) {
      final String sampleLabelStyle = a.get("samplelabelstyle");

      // floating
      if ("floating".equalsIgnoreCase(sampleLabelStyle)
        || "below_and_floating".equalsIgnoreCase(sampleLabelStyle))
      {
        content.setShowSamplePopup(true);
      }

      // inside
      if (("outside".equalsIgnoreCase(sampleLabelStyle))
        || ("inside".equalsIgnoreCase(sampleLabelStyle)))
      {

        content.setShowSampleLabels(true);

        final String sampleLabelAngle = a.get("samplelabelangle");
        if (sampleLabelAngle != null) {
          content.setLabelAngle(Integer.parseInt(sampleLabelAngle));
        }

        final String sampleLabelFont = a.get("samplelabelfont");
        if (sampleLabelFont != null) {
          content.setLabelFont(new ChartFont(sampleLabelFont));
        }
      }
    }

    final String seriesLabelsOn = a.get("serieslabelson");
    if ("true".equalsIgnoreCase(seriesLabelsOn)) {
      final String seriesLabelStyle = a.get("serieslabelstyle");

      // floating
      if ("floating".equalsIgnoreCase(seriesLabelStyle)) {
        content.setShowSeriesPopup(true);
      }

      // inside
      if (("outside".equalsIgnoreCase(seriesLabelStyle))
        || ("inside".equalsIgnoreCase(seriesLabelStyle)))
      {

        content.setShowSeriesLabels(true);

        final String seriesLabelAngle = a.get("serieslabelangle");
        if (seriesLabelAngle != null) {
          content.setLabelAngle(Integer.parseInt(seriesLabelAngle));
        }

        final String seriesLabelFont = a.get("serieslabelfont");
        if (seriesLabelFont != null) {
          content.setLabelFont(new ChartFont(seriesLabelFont));
        }
      }
    }

    // popup font
    final String floatingLabelFont = a.get("floatinglabelfont");
    if (floatingLabelFont != null) {
      content.setPopupFont(new ChartFont(floatingLabelFont));
    }
  }

  private static void initPieContent(PieContent content, Map<String, String> a) {
    initHasValueLabels(content, a);

    // use percent labels?
    final String percentLabelsOn = a.get("percentlabelson");
    if ("true".equals(percentLabelsOn)) {
      final String valueLabelStyle = a.get("percentlabelstyle");

      // floating
      if ("floating".equalsIgnoreCase(valueLabelStyle)) {
        content.setShowPercentPopup(true);
      }

      // inside
      if ((valueLabelStyle == null)
        || ("outside".equalsIgnoreCase(valueLabelStyle))
        || ("inside".equalsIgnoreCase(valueLabelStyle)))
      {

        content.setShowPercentLabels(true);

        final String decimals = a.get("percentdecimalcount");
        final StringBuilder format = new StringBuilder();
        format.append("0");
        if (decimals != null) {
          try {
            final int count = Integer.parseInt(decimals);
            format.append(".");
            for (int n = 0; n < count; n++) {
              format.append("0");
            }
          }
          catch (Exception ignored) {
          }
        }
        format.append("%");
        content.setPercentLabelFormat(format.toString());
      }

    }

    final String startAngle = a.get("startangle");
    if (startAngle != null) {
      content.setStartAngle(Double.parseDouble(startAngle));
    }

    // detached slices
    final String detachedSlices = a.get("detachedslices");
    if (detachedSlices != null) {
      final String[] slices = StringHelper.splitString(detachedSlices);
      for (String slice : slices) {
        content.addDetachedSample(Integer.parseInt(slice));
      }
    }

    final String detachedDistance = a.get("detacheddistance");
    if (detachedDistance != null) {
      content.setDetachedDistance(Double.parseDouble(detachedDistance));
    }

    // separator
    final String sliceSeparatorOn = a.get("sliceseperatoron");
    if ("false".equalsIgnoreCase(sliceSeparatorOn)) {
      content.setOutline(null);
    } else {
      String sliceSeparatorColor = a.get("sliceseperatorcolor");
      if (sliceSeparatorColor == null) {
        sliceSeparatorColor = "666";
      }
      content.setOutline(new ChartColor(sliceSeparatorColor));
    }

    // shadow
    final String shadowOn = a.get("shadowon");
    if (!"true".equalsIgnoreCase(shadowOn)) {
      content.setShadow(null);
    }
  }

  private static void initMeterContent(
    RoundChart chart, MeterContent content, Map<String, String> a)
  {
    initHasValueLabels(content, a);

    final RoundAxis round = (RoundAxis) chart.getScaleAxis();
    round.setShowLabelsInside(true);
    round.setStartAngle(-Math.PI * 0.75);
    round.setStopAngle(+Math.PI * 0.75);

    final String minText = a.get("min");
    if (minText != null) {
      final double min = Double.parseDouble(minText);
      chart.getScaleAxis().setMin(min);
    }

    final String maxText = a.get("max");
    if (maxText != null) {
      final double max = Double.parseDouble(maxText);
      chart.getScaleAxis().setMax(max);
    }

    addGaugeArea(chart, a, "red", ChartColor.RED);
    addGaugeArea(chart, a, "yellow", ChartColor.YELLOW);
    addGaugeArea(chart, a, "green", ChartColor.GREEN);

    // shadow
    final String shadowOn = a.get("shadowon");
    if (!"true".equalsIgnoreCase(shadowOn)) {
      content.setShadow(null);
    }
  }

  private static void addGaugeArea(
    RoundChart chart, Map<String, String> a, String prefix, ChartColor color)
  {
    final String redFrom = a.get(prefix + "from");
    final String redTo = a.get(prefix + "to");
    if ((redFrom != null) && (redTo != null)) {
      final String colorText = a.get(prefix + "color");
      if (colorText != null) {
        color = new ChartColor(colorText);
      }
      chart.getScaleAxis().addCriticalArea(
        Double.parseDouble(redFrom), Double.parseDouble(redTo), null, color
      );
    }
  }

  private static ChartColor[] parseColors(Map<String, String> a, String name) {
    final String value = a.get(name);
    if (value == null) {
      return null;
    }
    final String[] values = StringHelper.splitString(value);
    final ChartColor[] result = new ChartColor[values.length];
    for (int n = 0; n < values.length; n++) {
      result[n] = new ChartColor(values[n]);
    }
    return result;
  }

  private static int[] parseInts(Map<String, String> a, String name) {
    final String value = a.get(name);
    if (value == null) {
      return null;
    }
    final String[] values = StringHelper.splitString(value);
    final int[] result = new int[values.length];
    for (int n = 0; n < values.length; n++) {
      result[n] = Integer.parseInt(values[n]);
    }
    return result;
  }
}
