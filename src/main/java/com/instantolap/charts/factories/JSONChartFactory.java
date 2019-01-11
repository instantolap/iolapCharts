package com.instantolap.charts.factories;

import com.instantolap.charts.*;
import com.instantolap.charts.HeatMapContent.HeatColor;
import com.instantolap.charts.control.RoundChart;
import com.instantolap.charts.impl.animation.*;
import com.instantolap.charts.impl.chart.*;
import com.instantolap.charts.impl.content.*;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.impl.data.transform.StackedTransform;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.json.JSONArray;
import com.instantolap.charts.json.JSONException;
import com.instantolap.charts.json.JSONObject;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.HasAnimation;

import java.util.ArrayList;
import java.util.List;


public class JSONChartFactory {

  public static Chart create(JSONObject json, Data data, Theme theme) throws JSONException {

    // data
    final JSONObject dataObject = json.optJSONObject("data");
    if (dataObject != null) {
      if (data == null) {
        data = JSONDataFactory.parseData(dataObject, theme);
      }
    }

    // transformation
    JSONDataFactory.addTransformations(json, data);

    // chart type
    final Chart chart;
    Content defaultContent = null;
    boolean round = false;
    final String type = json.getString("type");
    if ("sample".equalsIgnoreCase(type)) {
      chart = createSampleChart(json, data);
    } else if ("round".equalsIgnoreCase(type)) {
      chart = createRoundChart(json, data, false, true, false, false);
      round = true;
    } else if ("xy".equalsIgnoreCase(type)) {
      chart = createXYChart(json, data);
    } else if ("samplesample".equalsIgnoreCase(type)) {
      chart = createSampleSampleChart(json, data);
    } else if ("time".equalsIgnoreCase(type)) {
      chart = createTimeChart(json, data);
    } else if ("bar".equalsIgnoreCase(type)) {
      chart = createSampleChart(json, data);
      defaultContent = new BarContentImpl(data.getTheme());
    } else if ("column".equalsIgnoreCase(type)) {
      final SampleChart sampleChart = createSampleChart(json, data);
      sampleChart.setRotated(true);
      chart = sampleChart;
      defaultContent = new BarContentImpl(data.getTheme());
    } else if ("line".equalsIgnoreCase(type)) {
      chart = createSampleChart(json, data);
      defaultContent = new LineContentImpl(data.getTheme());
    } else if ("spline".equalsIgnoreCase(type)) {
      chart = createSampleChart(json, data);
      final LineContent lineContent = new LineContentImpl(data.getTheme());
      lineContent.setInterpolated(true);
      defaultContent = lineContent;
    } else if ("area".equalsIgnoreCase(type)) {
      chart = createSampleChart(json, data);
      final LineContent line = new LineContentImpl(data.getTheme());
      line.setAreaChart(true);
      defaultContent = line;
    } else if ("pie".equalsIgnoreCase(type)) {
      transformStacked(data);
      chart = createRoundChart(json, data, true, false, false, false);
      defaultContent = new PieContentImpl(data.getTheme());
    } else if ("doughnut".equalsIgnoreCase(type)) {
      chart = createRoundChart(json, data, true, false, false, false);
      final PieContent pieContent = new PieContentImpl(data.getTheme());
      pieContent.setSeriesSpace(0.5);
      defaultContent = pieContent;
    } else if ("radar".equalsIgnoreCase(type)) {
      chart = createRoundChart(json, data, false, true, true, true);
      defaultContent = new RoundLineContentImpl(data.getTheme());
    } else if ("rose".equalsIgnoreCase(type)) {
      chart = createRoundChart(json, data, false, true, true, true);
      defaultContent = new RoundBarContentImpl(data.getTheme());
    } else if ("scatter".equalsIgnoreCase(type)) {
      chart = createXYChart(json, data);
      defaultContent = new ScatterContentImpl(data.getTheme());
    } else if ("bubble".equalsIgnoreCase(type)) {
      chart = createXYChart(json, data);
      final ScatterContent scatterContent = new ScatterContentImpl(data.getTheme());
      scatterContent.setBubble(true);
      defaultContent = scatterContent;
    } else if ("heatmap".equalsIgnoreCase(type)) {
      chart = createSampleSampleChart(json, data);
      defaultContent = new HeatMapContentImpl(data.getTheme());
    } else if ("timeline".equalsIgnoreCase(type)) {
      chart = createTimeChart(json, data);
      defaultContent = new LineContentImpl(data.getTheme());
    } else if ("candle".equalsIgnoreCase(type)) {
      chart = createTimeChart(json, data);
      final BarContent barContent = new BarContentImpl(data.getTheme());
      barContent.setLowerMeasure("entry");
      barContent.setMeasure("exit");
      barContent.setMinMeasure("min");
      barContent.setMaxMeasure("max");
      barContent.setBarSpacing(0);
      defaultContent = barContent;
    } else if ("meter".equalsIgnoreCase(type)) {
      chart = createRoundChart(json, data, true, false, true, false);
      defaultContent = new MeterContentImpl(data.getTheme());
    } else {
      throw new JSONException("Unknown chart type '" + type + "'");
    }

    if (defaultContent != null) {
      chart.addContent(defaultContent);
    }

    chart.setData(data);

    // chart content (array or single object)
    final JSONArray contents = json.optJSONArray("content");
    if (contents != null) {
      for (int n = 0; n < contents.length(); n++) {
        final JSONObject contentData = (JSONObject) contents.get(n);
        final Content content = createContent(contentData, defaultContent, round, data, n);
        if (content != null) {
          chart.addContent(content);
        }
      }
    }

    final JSONObject contentData = json.optJSONObject("content");
    if (contentData != null) {
      final Content content = createContent(contentData, defaultContent, round, data, 0);
      if (content != null) {
        chart.addContent(content);
      }
    }

    // legend
    final JSONObject legendObject = json.optJSONObject("legend");
    if (legendObject != null) {
      chart.setShowLegend(true);
      initLegend(chart.getLegend(), chart, legendObject);
    }

    return chart;
  }

  private static void initLegend(Legend legend, Chart chart, JSONObject json) throws JSONException {
    initHasShadow(legend, json);
    initHasBorder(legend, json);
    initHasBackground(legend, json);

    final String position = json.optString("position", null);
    if ("north".equalsIgnoreCase(position) || "top".equalsIgnoreCase(position)) {
      chart.setLegendPosition(Chart.NORTH);
    } else if ("east".equalsIgnoreCase(position) || "right".equalsIgnoreCase(position)) {
      chart.setLegendPosition(Chart.EAST);
    } else if ("south".equalsIgnoreCase(position) || "bottom".equalsIgnoreCase(position)) {
      chart.setLegendPosition(Chart.SOUTH);
    } else if ("west".equalsIgnoreCase(position) || "left".equalsIgnoreCase(position)) {
      chart.setLegendPosition(Chart.WEST);
    }

    final String alignment = json.optString("alignment", null);
    if ("north".equalsIgnoreCase(alignment) || "top".equalsIgnoreCase(alignment)) {
      chart.setLegendAlignment(Chart.NORTH);
    } else if ("east".equalsIgnoreCase(alignment) || "right".equalsIgnoreCase(alignment)) {
      chart.setLegendAlignment(Chart.EAST);
    } else if ("south".equalsIgnoreCase(alignment) || "bottom".equalsIgnoreCase(alignment)) {
      chart.setLegendAlignment(Chart.SOUTH);
    } else if ("west".equalsIgnoreCase(alignment) || "left".equalsIgnoreCase(alignment)) {
      chart.setLegendAlignment(Chart.WEST);
    } else if ("center".equalsIgnoreCase(alignment) || "middle".equalsIgnoreCase(alignment)) {
      chart.setLegendAlignment(Chart.CENTER);
    }

    chart.setLegendInside(json.optBoolean("inside", chart.isLegendInside()));
    chart.setLegendSpacing(json.optInt("spacing", chart.getLegendSpacing()));
    legend.setSpacing(json.optInt("labelspacing", legend.getSpacing()));

    final String color = json.optString("color", null);
    if (color != null) {
      legend.setColor(new ChartColor(color));
    }

    final ChartColor[] colors = getColorArray(json, "colors");
    if (colors != null) {
      legend.setColors(colors);
    }

    final String font = json.optString("font", null);
    if (font != null) {
      legend.setFont(new ChartFont(font));
    }

    legend.setPadding(json.optInt("padding", legend.getPadding()));
    legend.setReverse(json.optBoolean("reverse", legend.isReverse()));

    final String[] labels = getStringArray(json, "labels");
    if (labels != null) {
      legend.setLabels(labels);
    }
  }

  private static void initChart(JSONObject json, Chart chart) throws JSONException {
    initTitle(chart, json);
    initHasBackground(chart, json);

    final String font = json.optString("font", null);
    if (font != null) {
      chart.setFont(new ChartFont(font));
    }

    final String foreground = json.optString("foreground", null);
    if (foreground != null) {
      chart.setForeground(new ChartColor(foreground));
    }

    // insets
    // content insets
    final JSONObject contentInsets = json.optJSONObject("contentinsets");
    if (contentInsets != null) {
      final int top = contentInsets.optInt("top", 0);
      final int left = contentInsets.optInt("left", 0);
      final int right = contentInsets.optInt("right", 0);
      final int bottom = contentInsets.optInt("bottom", 0);
      chart.setContentInsets(top, left, right, bottom);
    }

    chart.setSubTitle(json.optString("subtitle", chart.getSubTitle()));

    final String subTitleColor = json.optString("subtitlecolor", null);
    if (subTitleColor != null) {
      chart.setSubTitleColor(new ChartColor(subTitleColor));
    }

    initHasAnimation(json, chart);

    chart.setPopup(json.optBoolean("popup", chart.isPopup()));
  }

  private static void initHasAnimation(JSONObject json, HasAnimation chart) {
    chart.setAnimationEnabled(json.optBoolean("animation", chart.isAnimationEnabled()));
    chart.setAnimationTime(json.optLong("animationtime", chart.getAnimationTime()));
  }

  private static SampleChart createSampleChart(JSONObject json, Data data) throws JSONException {
    final SampleChart chart = new SampleChartImpl(data.getTheme());
    initChart(json, chart);

    final JSONObject canvasData = json.optJSONObject("canvas");
    if (canvasData != null) {
      initXYCanvas(chart.getCanvas(), canvasData);
    }

    if (json.has("rotated")) {
      chart.setRotated(json.getBoolean("rotated"));
    }

    if (json.has("stacked")) {
      chart.setStacked(json.getBoolean("stacked"));
      if (chart.isStacked()) {
        transformStacked(data);
      }
    }

    final JSONObject scaleData = json.optJSONObject("scale");
    if (scaleData != null) {
      initValueAxis(chart.getScaleAxis(), scaleData, data);
    }

    final JSONObject scaleData2 = json.optJSONObject("scale2");
    if (scaleData2 != null) {
      initValueAxis(chart.getScaleAxis2(), scaleData2, data);
    }

    final JSONObject sampleData = json.optJSONObject("samples");
    if (sampleData != null) {
      initSampleAxis(chart.getSampleAxis(), sampleData);
    }

    return chart;
  }

  private static void transformStacked(Data data) {
    final Transformation transformation = new StackedTransform();
    data.setCurrentCube(transformation.transform(data.getCurrentCube()));
  }

  private static RoundChart createRoundChart(
    JSONObject json,
    Data data,
    boolean scaleOutside,
    boolean showSampleAxis,
    boolean showValueAxis,
    boolean showGrid)
    throws JSONException
  {
    final RoundChart chart = new RoundChartImpl(data.getTheme(), scaleOutside);
    initChart(json, chart);

    if (!showGrid) {
      chart.getCanvas().setBaseLine(null);
      chart.getCanvas().setGrid(null);
    }

    final String innerBorder = json.optString("innerborder", null);
    if (innerBorder != null) {
      chart.setInnerBorder(new ChartColor(innerBorder));
    }

    final String innerBorderStroke = json.optString("innerborderstroke", null);
    if (innerBorderStroke != null) {
      chart.setInnerBorderStroke(new ChartStroke(innerBorderStroke));
    }

    chart.setInnerPadding(json.optInt("innerpadding", chart.getInnerPadding()));

    final JSONObject canvasData = json.optJSONObject("canvas");
    if (canvasData != null) {
      initRoundCanvas(chart.getCanvas(), canvasData);
    }

    if (json.has("stacked")) {
      transformStacked(data);
      chart.setStacked(json.getBoolean("stacked"));
    }

    chart.setEnableRotation(json.optBoolean("rotation", true));

    chart.getScaleAxis().setVisible(showValueAxis);
    final JSONObject scaleData = json.optJSONObject("scale");
    if (scaleData != null) {
      initValueAxis(chart.getScaleAxis(), scaleData, data);
    }

    chart.getSampleAxis().setVisible(showSampleAxis);
    final JSONObject sampleData = json.optJSONObject("samples");
    if (sampleData != null) {
      initSampleAxis(chart.getSampleAxis(), sampleData);
    }

    return chart;
  }

  private static Chart createXYChart(JSONObject json, Data data) throws JSONException {
    final XYChart chart = new XYChartImpl(data.getTheme());
    initChart(json, chart);

    final JSONObject canvasData = json.optJSONObject("canvas");
    if (canvasData != null) {
      initXYCanvas(chart.getCanvas(), canvasData);
    }

    final JSONObject xScaleData = json.optJSONObject("x");
    if (xScaleData != null) {
      initValueAxis(chart.getXAxis(), xScaleData, data);
    }

    final JSONObject yScaleData = json.optJSONObject("y");
    if (yScaleData != null) {
      initValueAxis(chart.getYAxis(), yScaleData, data);
    }

    return chart;
  }

  private static SampleSampleChart createSampleSampleChart(JSONObject json, Data data)
    throws JSONException
  {
    final SampleSampleChart chart = new SampleSampleChartImpl(data.getTheme());
    initChart(json, chart);

    final JSONObject canvasData = json.optJSONObject("canvas");
    if (canvasData != null) {
      initXYCanvas(chart.getCanvas(), canvasData);
    }

    final JSONObject xData = json.optJSONObject("x");
    if (xData != null) {
      initSampleAxis(chart.getXAxis(), xData);
    }

    final JSONObject yData = json.optJSONObject("y");
    if (yData != null) {
      initSampleAxis(chart.getYAxis(), yData);
    }

    return chart;
  }

  private static TimeChart createTimeChart(JSONObject json, Data data) throws JSONException {
    final TimeChart chart = new TimeChartImpl(data.getTheme());
    initChart(json, chart);

    final JSONObject canvasData = json.optJSONObject("canvas");
    if (canvasData != null) {
      initXYCanvas(chart.getCanvas(), canvasData);
    }

    if (json.has("rotated")) {
      chart.setRotated(json.getBoolean("rotated"));
    }

    if (json.has("stacked")) {
      transformStacked(data);
      chart.setStacked(json.getBoolean("stacked"));
    }

    final JSONObject xData = json.optJSONObject("time");
    if (xData != null) {
      initTimeAxis(chart.getTimeAxis(), xData, data);
    }

    final JSONObject yData = json.optJSONObject("scale");
    if (yData != null) {
      initValueAxis(chart.getScaleAxis(), yData, data);
    }

    return chart;
  }

  private static void initXYCanvas(XYCanvas canvas, JSONObject json) throws JSONException {
    initCanvas(canvas, json);

    final String baseLine = json.optString("baseline", null);
    if (baseLine != null) {
      canvas.setBaseLine(new ChartColor(baseLine));
    }

    final String horizontalBackground = json.optString("horizontalbackground", null);
    if (horizontalBackground != null) {
      canvas.setHorizontalBackground2(new ChartColor(horizontalBackground));
    }

    final String verticalBackground = json.optString("verticalbackground", null);
    if (verticalBackground != null) {
      canvas.setVerticalBackground2(new ChartColor(verticalBackground));
    }

    final String horizontalGrid = json.optString("horizontalgrid", null);
    if (horizontalGrid != null) {
      canvas.setHorizontalGrid(new ChartColor(horizontalGrid));
    }

    final String horizontalGridStroke = json.optString("horizontalstroke", null);
    if (horizontalGridStroke != null) {
      canvas.setHorizontalGridStroke(new ChartStroke(horizontalGridStroke));
    }

    final String verticalGrid = json.optString("verticalgrid", null);
    if (verticalGrid != null) {
      canvas.setVerticalGrid(new ChartColor(verticalGrid));
    }

    final String verticalGridStroke = json.optString("verticalstroke", null);
    if (verticalGridStroke != null) {
      canvas.setVerticalGridStroke(new ChartStroke(verticalGridStroke));
    }
  }

  private static void initRoundCanvas(RoundCanvas canvas, JSONObject json) throws JSONException
  {
    initCanvas(canvas, json);

    final String baseLine = json.optString("baseline", null);
    if (baseLine != null) {
      canvas.setBaseLine(new ChartColor(baseLine));
    }

    if (json.has("grid")) {
      final String grid = json.optString("grid", null);
      if (grid != null) {
        canvas.setGrid(new ChartColor(grid));
      } else {
        canvas.setGrid(null);
      }
    }

    final String gridStroke = json.optString("gridstroke", null);
    if (gridStroke != null) {
      canvas.setGridStroke(new ChartStroke(gridStroke));
    }

    canvas.setRound(json.optBoolean("round", canvas.isRound()));
  }

  private static void initCanvas(Canvas canvas, JSONObject json) throws JSONException {
    initHasShadow(canvas, json);
    initHasBorder(canvas, json);
    initHasBackground(canvas, json);
  }

  private static void initTimeAxis(TimeAxis axis, JSONObject json, Data data) throws JSONException {
    initScaleAxis(axis, json, data);

    axis.setMinSampleWidth(json.optInt("minunitsize", axis.getMinSampleWidth()));
    axis.setMaxSampleWidth(json.optInt("maxunitsize", axis.getMaxSampleWidth()));
    axis.setSampleMilliseconds(json.optLong("unitms", axis.getSampleMilliseconds()));
  }

  private static void initScaleAxis(ScaleAxis axis, JSONObject json, Data data)
    throws JSONException
  {
    initAxis(axis, json);
    initHasSamples(axis, json);

    final String min = json.optString("min", null);
    if (min != null) {
      axis.setMin(Double.parseDouble(min));
    }

    final String max = json.optString("max", null);
    if (max != null) {
      axis.setMax(Double.parseDouble(max));
    }

    axis.setMaxLineCount(json.optInt("maxlinecount", axis.getMaxLineCount()));
    axis.setMinTickSize(json.optInt("minticksize", axis.getMinTickSize()));

    // zoom
    axis.enableZoom(json.optBoolean("zoom", axis.isZoomEnabled()));
    axis.setZoomStep(json.optDouble("zoomstep", axis.getZoomStep()));
  }

  private static void initValueAxis(ValueAxis axis, JSONObject json, Data data)
    throws JSONException
  {
    initScaleAxis(axis, json, data);

    axis.setFormat(json.optString("format", axis.getFormat()));

    final String decimalCount = json.optString("decimalcount", null);
    if (decimalCount != null) {
      axis.setDecimalCount(Integer.parseInt(decimalCount));
    }

    final JSONArray targets = json.optJSONArray("targets");
    if (targets != null) {
      for (int n = 0; n < targets.length(); n++) {
        final JSONObject target = targets.getJSONObject(n);

        final double value = target.getDouble("value");
        final String text = target.optString("text");
        final String color = target.optString("color");
        final String background = target.optString("background");

        ChartColor targetColor = null;
        if (color != null && !color.isEmpty()) {
          targetColor = new ChartColor(color);
        }

        ChartColor backgroundColor = null;
        if (background != null && !background.isEmpty()) {
          backgroundColor = new ChartColor(background);
        }

        axis.addTargetLine(value, text, targetColor, backgroundColor, null);
      }
    }

    final JSONArray areas = json.optJSONArray("areas");
    if (areas != null) {
      for (int n = 0; n < areas.length(); n++) {
        final JSONObject area = areas.getJSONObject(n);

        final double minArea = area.getDouble("min");
        final double maxArea = area.getDouble("max");
        final String text = area.optString("text");
        final String color = area.optString("color");

        ChartColor targetColor = null;
        if (color != null && !color.isEmpty()) {
          targetColor = new ChartColor(color);
        }

        axis.addCriticalArea(minArea, maxArea, text, targetColor);
      }
    }
  }

  private static void initHasSamples(HasSamples content, JSONObject json) throws JSONException {
    final JSONArray series = json.optJSONArray("series");
    if (series != null) {
      for (int n = 0; n < series.length(); n++) {
        final int s = series.getInt(n);
        content.addDisplaySample(1, s);
      }
    }
  }

  private static void initSampleAxis(SampleAxis axis, JSONObject json) throws JSONException {
    initAxis(axis, json);

    final String[] labels = getStringArray(json, "labels");
    if (labels != null) {
      axis.setLabels(labels);
    }
  }

  private static void initAxis(Axis axis, JSONObject json) throws JSONException {
    initTitle(axis, json);
    initHasBackground(axis, json);

    axis.setVisible(json.optBoolean("visible", axis.isVisible()));

    axis.setTitleRotation(json.optInt("titleangle", axis.getTitleRotation()));

    axis.setAutoSpacingOn(json.optBoolean("autospacing", axis.isAutoSpacingOn()));

    final String font = json.optString("font", null);
    if (font != null) {
      axis.setFont(new ChartFont(font));
    }

    final String foreground = json.optString("foreground", null);
    if (foreground != null) {
      axis.setColor(new ChartColor(foreground));
    }

    axis.setShowLabels(json.optBoolean("showlabels", axis.isShowLabels()));
    axis.setShowLabelsInside(json.optBoolean("showlabelsinside", axis.isShowLabelsInside()));
    axis.setLabelRotation(json.optInt("labelangle", axis.getLabelRotation()));
    axis.setLabelSpacing(json.optInt("labelspacing", axis.getLabelSpacing()));
    axis.setPrefix(json.optString("prefix", axis.getPrefix()));
    axis.setPostfix(json.optString("postfix", axis.getPostfix()));
    axis.setTickWidth(json.optInt("tickwidth", axis.getTickWidth()));

    if (json.has("line")) {
      if (json.isNull("line")) {
        axis.setLineColor(null);
      } else {
        final String lineColor = json.optString("line", null);
        if (lineColor != null) {
          axis.setLineColor(new ChartColor(lineColor));
        }
      }
    }

    final ChartColor[] colors = getColorArray(json, "colors");
    if (colors != null) {
      axis.setColors(colors);
    }

    axis.setShowBaseLine(json.optBoolean("baseline", axis.isShowBaseLine()));

    // properties for round axes
    if (axis instanceof RoundAxis) {
      final RoundAxis round = (RoundAxis) axis;
      round.setRotateLabels(json.optBoolean("rotatelabels", round.isRotateLabels()));
      round.setStartAngle(
        Math.toRadians(json.optInt("start", (int) Math.toDegrees(round.getStartAngle())))
      );
      round.setStopAngle(
        Math.toRadians(json.optInt("end", (int) Math.toDegrees(round.getStopAngle())))
      );

    }
  }

  private static void initTitle(HasTitle content, JSONObject json) {
    content.setTitle(json.optString("title", content.getTitle()));

    final String titleColor = json.optString("titlecolor", null);
    if (titleColor != null) {
      content.setTitleColor(new ChartColor(titleColor));
    }

    final String titleFont = json.optString("titlefont", null);
    if (titleFont != null) {
      content.setTitleFont(new ChartFont(titleFont));
    }
  }

  private static Content createContent(
    JSONObject json, Content defaultContent, boolean round, Data data, int index)
    throws JSONException
  {
    final String type = json.optString("type", null);
    if (type == null) {
      if (defaultContent instanceof BarContent) {
        initBarContent((BarContent) defaultContent, json, data, 0);
      } else if (defaultContent instanceof LineContent) {
        initLineContent((LineContent) defaultContent, json, data, 0);
      } else if (defaultContent instanceof PieContent) {
        initPieContent((PieContent) defaultContent, json, data, 0);
      } else if (defaultContent instanceof ScatterContent) {
        initScatterContent((ScatterContent) defaultContent, json, data, 0);
      } else if (defaultContent instanceof HeatMapContent) {
        initHeatMapContent((HeatMapContent) defaultContent, json, data, 0);
      } else if (defaultContent instanceof MeterContent) {
        initMeterContent((MeterContent) defaultContent, json, data, 0);
      }
      return null;
    } else if ("bar".equalsIgnoreCase(type)) {
      final BarContent content = round ? new RoundBarContentImpl(data.getTheme()) : new BarContentImpl(data.getTheme());
      initBarContent(content, json, data, index);
      return content;
    } else if ("line".equalsIgnoreCase(type)) {
      final LineContent content = round ? new RoundLineContentImpl(data.getTheme()) : new LineContentImpl(data.getTheme());
      initLineContent(content, json, data, index);
      return content;
    } else if ("pie".equalsIgnoreCase(type)) {
      final PieContent content = new PieContentImpl(data.getTheme());
      initPieContent(content, json, data, index);
      return content;
    } else if ("scatter".equalsIgnoreCase(type)) {
      final ScatterContent content = new ScatterContentImpl(data.getTheme());
      initScatterContent(content, json, data, index);
      return content;
    } else if ("heatmap".equalsIgnoreCase(type)) {
      final HeatMapContent content = new HeatMapContentImpl(data.getTheme());
      initHeatMapContent(content, json, data, index);
      return content;
    } else if ("candle".equalsIgnoreCase(type)) {
      final BarContent content = new BarContentImpl(data.getTheme());
      initBarContent(content, json, data, index);
      return content;
    } else if ("meter".equalsIgnoreCase(type)) {
      final MeterContent content = new MeterContentImpl(data.getTheme());
      initMeterContent(content, json, data, index);
      return content;
    } else {
      return null;
    }
  }

  private static void initMeterContent(MeterContent content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initContent(content, json, data, index);
    initSampleContent(content, json);
    initHasValueLabels(content, json);
    initHasMeasure(content, json);

    // pin
    final String pinColor = json.optString("pincolor", null);
    if (pinColor != null) {
      content.setPinColor(new ChartColor(pinColor));
    }

    content.setPinSize(json.optDouble("pinsize", content.getPinSize()));
  }

  private static void initBarContent(BarContent content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initContent(content, json, data, index);
    initSampleContent(content, json);
    initHasValueLabels(content, json);
    initHasRegression(content, json);
    initHasValueAxis(content, json);
    initHasMeasure(content, json);

    content.setBarWidth(json.optDouble("barwidth", content.getBarWidth()));
    content.setBarSpacing(json.optInt("barspacing", content.getBarSpacing()));
    content.setMultiColor(json.optBoolean("multicolor", content.isMultiColor()));

    content.setLowerMeasure(json.optString("lowermeasure", content.getLowerMeasure()));
    content.setMinMeasure(json.optString("minmeasure", content.getMinMeasure()));
    content.setMaxMeasure(json.optString("maxmeasure", content.getMaxMeasure()));

    final String colorUp = json.optString("colorup", null);
    if (colorUp != null) {
      content.setColorUp(new ChartColor(colorUp));
    }

    final String colorDown = json.optString("colordown", null);
    if (colorDown != null) {
      content.setColorDown(new ChartColor(colorDown));
    }

    if (content instanceof RoundBarContentImpl) {
      final RoundBarContentImpl round = (RoundBarContentImpl) content;
      round.setRound(json.optBoolean("round", round.isRound()));
    }

  }

  private static void initLineContent(LineContent content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initContent(content, json, data, index);
    initSampleContent(content, json);
    initHasValueLabels(content, json);
    initHasRegression(content, json);
    initHasValueAxis(content, json);
    initHasMeasure(content, json);

    content.setAreaChart(json.optBoolean("area", content.isAreaChart()));
    content.setAreaOpacity(json.optDouble("areaopacity", content.getAreaOpacity()));
    content.setInterpolated(json.optBoolean("interpolated", content.isInterpolated()));
    content.setStepLine(json.optBoolean("stepline", content.isStepLine()));
    content.setConnected(json.optBoolean("connected", content.isConnected()));

    final JSONArray fillColors = json.optJSONArray("fill");
    if (fillColors != null) {
      for (int n = 0; n < fillColors.length(); n++) {
        final JSONObject fillObject = fillColors.getJSONObject(n);
        final String color = fillObject.getString("color");
        final int start = fillObject.getInt("start");
        final int end = fillObject.optInt("end", start + 1);
        content.setFillColor(start, end, new ChartColor(color));
      }
    }
  }

  private static void initDataPresentation(JSONObject json, Data data) throws JSONException {
    final String symbol = json.optString("symbol", null);
    if (symbol != null) {
      data.setSymbol(-1, SymbolDrawer.getSymbol(symbol));
    }

    final JSONArray symbols = json.optJSONArray("symbols");
    if (symbols != null) {
      for (int n = 0; n < symbols.length(); n++) {
        data.setSymbol(n, SymbolDrawer.getSymbol(symbols.getString(n)));
      }
    }

    if (json.has("symbolsize")) {
      data.setSymbolSize(-1, json.getInt("symbolsize"));
    }

    final JSONArray symbolSizes = json.optJSONArray("symbolsizes");
    if (symbolSizes != null) {
      for (int n = 0; n < symbolSizes.length(); n++) {
        data.setSymbolSize(n, symbolSizes.getInt(n));
      }
    }

    final String stroke = json.optString("stroke", null);
    if (stroke != null) {
      data.setStroke(-1, new ChartStroke(stroke));
    }

    final JSONArray strokes = json.optJSONArray("strokes");
    if (strokes != null) {
      for (int n = 0; n < strokes.length(); n++) {
        data.setStroke(n, new ChartStroke(strokes.getString(n)));
      }
    }

    final ChartColor[] colors = getColorArray(json, "colors");
    if (colors != null) {
      data.setColors(0, colors);
    }
  }

  private static void initPieContent(PieContent content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initContent(content, json, data, index);
    initSampleContent(content, json);
    initHasValueLabels(content, json);
    initHasMeasure(content, json);

    final JSONArray detached = json.optJSONArray("detached");
    if (detached != null) {
      for (int n = 0; n < detached.length(); n++) {
        final int slice = detached.getInt(n);
        content.addDetachedSample(slice);
      }
    }

    content.setDetachedDistance(json.optDouble("detacheddistance", content.getDetachedDistance()));
    content.setRound(json.optBoolean("round", content.isRound()));
    content.setStartAngle(json.optDouble("startangle", content.getStartAngle()));
    content.setSeriesSpace(json.optDouble("seriesspace", content.getSeriesSpace()));

    if (json.optBoolean("showpercentlabels")) {
      content.setShowPercentLabels(true);

      final String format = json.optString("percentlabelformat", null);
      if (format != null) {
        content.setPercentLabelFormat(format);
      }
    }
  }

  private static void initScatterContent(
    ScatterContent content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initContent(content, json, data, index);
    initHasShadow(content, json);
    initHasValueLabels(content, json);
    initHasRegression(content, json);
    initHasMeasure(content, json);

    content.setBubble(json.optBoolean("bubble", content.isBubble()));
    content.setXMeasure(json.optString("xmeasure", content.getXMeasure()));
    content.setYMeasure(json.optString("ymeasure", content.getYMeasure()));
  }

  private static void initHeatMapContent(
    HeatMapContent content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initContent(content, json, data, index);
    initHasShadow(content, json);
    initHasValueLabels(content, json);

    final JSONArray heatColors = json.optJSONArray("heatcolors");
    if (heatColors != null) {
      Double start = null, end = null;
      ChartColor startColor = null, endColor = null;
      final List<HeatColor> result = new ArrayList<>();
      for (int n = 0; n < heatColors.length(); n += 2) {
        end = heatColors.getDouble(n);
        endColor = new ChartColor(heatColors.getString(n + 1));
        if (start != null && end != null) {
          final HeatColor heat = new HeatColor();
          heat.start = start;
          heat.end = end;
          heat.startColor = startColor;
          heat.endColor = endColor;
          result.add(heat);
        }
        start = end;
        startColor = endColor;
      }
      if (start != null) {
        final HeatColor heat = new HeatColor();
        heat.start = start;
        heat.end = Double.MAX_VALUE;
        heat.startColor = startColor;
        heat.endColor = startColor;
        result.add(heat);
      }
      content.setHeatColors(result.toArray(new HeatColor[0]));
    }

    content.setShowSymbols(json.optBoolean("showsymbols", content.isShowSymbols()));
    content.setSymbolAutoSize(json.optBoolean("symbolautosize", content.isSymbolAutoSize()));
    content.setMinTickSize(json.optInt("minticksize", content.getMinTickSize()));
    content.setSymbolAutoColor(json.optBoolean("symbolautocolor", content.isSymbolAutoColor()));
    content.setFormat(json.optString("format", content.getFormat()));

    content.setFill(json.optBoolean("fill", content.isFill()));
    content.setFillPadding(json.optInt("fillpadding", content.getFillPadding()));
  }

  private static void initContent(Content content, JSONObject json, Data data, int index)
    throws JSONException
  {
    initHasSamples(content, json);
    initDataPresentation(json, data);

    // animations
    final JSONArray animData = json.optJSONArray("animations");
    if (animData != null) {
      final CombinedContentAnim anims = new CombinedContentAnim();
      content.setAnimation(anims);
      for (int n = 0; n < animData.length(); n++) {
        final String type = animData.optString(n);
        if ("grow".equalsIgnoreCase(type)) {
          anims.addAnimation(new GrowContentAnim());
        } else if ("shrink".equalsIgnoreCase(type)) {
          anims.addAnimation(new ShrinkContentAnim());
        } else if ("fly".equalsIgnoreCase(type)) {
          anims.addAnimation(new FlyInContentAnim());
        } else if ("fade".equalsIgnoreCase(type)) {
          anims.addAnimation(new FadeInContentAnim());
        } else if ("left".equalsIgnoreCase(type)) {
          anims.addAnimation(new LeftToRightContentAnim());
        } else if ("right".equalsIgnoreCase(type)) {
          anims.addAnimation(new RightToLeftContentAnim());
        }
      }
    }
  }

  private static void initSampleContent(SampleContent content, JSONObject json)
    throws JSONException
  {
    initHasShadow(content, json);
    initHasAnnotations(content, json);

    if (json.has("outline")) {
      if (json.isNull("outline")) {
        content.setOutline(null);
      } else {
        final String outline = json.optString("outline", null);
        if (outline != null) {
          content.setOutline(new ChartColor(outline));
        }
      }
    }

    content.setShine(json.optDouble("shine", content.getShine()));
  }

  private static void initHasShadow(HasShadow content, JSONObject json) {
    if (json.has("shadow")) {
      if (json.isNull("shadow")) {
        content.setShadow(null);
      } else {
        final String shadow = json.optString("shadow", null);
        if (shadow != null) {
          content.setShadow(new ChartColor(shadow));
        }
      }
    }

    content.setShadowXOffset(json.optInt("shadowxoffset", content.getShadowXOffset()));
    content.setShadowYOffset(json.optInt("shadowyoffset", content.getShadowYOffset()));
  }

  private static void initHasBackground(HasBackground content, JSONObject json)
    throws JSONException
  {
    if (json.has("background")) {
      if (json.isNull("background")) {
        content.setBackground(null);
      } else {
        final String background = json.optString("background", null);
        if (background != null) {
          content.setBackground(new ChartColor(background));
        }
      }
    }
  }

  private static void initHasValueAxis(HasValueAxis content, JSONObject json) {
    content.setUsedValueAxis(json.optInt("axis", content.getUsedValueAxis()));
  }

  private static void initHasMeasure(HasMeasure content, JSONObject json) {
    content.setMeasure(json.optString("measure", content.getMeasure()));
  }

  private static void initHasValueLabels(HasValueLabels content, JSONObject json) {
    content.setLabelAngle(json.optInt("labelangle", content.getLabelAngle()));

    final String labelColor = json.optString("labelcolor", null);
    if (labelColor != null) {
      content.setLabelColor(new ChartColor(labelColor));
    }

    final String labelFont = json.optString("labelfont", null);
    if (labelFont != null) {
      content.setLabelFont(new ChartFont(labelFont));
    }

    content.setLabelSpacing(json.optInt("labelspacing", content.getLabelSpacing()));

    // popups
    content.setShowPopup(json.optBoolean("popup", content.isShowPopup()));
    final String popupFont = json.optString("popupfont", null);
    if (popupFont != null) {
      content.setPopupFont(new ChartFont(popupFont));
    }

    content.setShowSampleLabels(json.optBoolean("samplelabels", content.isShowSampleLabels()));
    content.setShowSamplePopup(json.optBoolean("samplepopup", content.isShowSamplePopup()));
    content.setShowSeriesLabels(json.optBoolean("serieslabels", content.isShowSeriesLabels()));
    content.setShowPercentLabels(json.optBoolean("percentlabels", content.isShowPercentLabels()));
    content.setShowSeriesPopup(json.optBoolean("seriespopup", content.isShowSeriesPopup()));
    content.setShowValueLabels(json.optBoolean("valuelabels", content.isShowValueLabels()));
    content.setShowValuePopup(json.optBoolean("valuepopup", content.isShowValuePopup()));
    content.setShowPercentPopup(json.optBoolean("percentpopup", content.isShowPercentPopup()));

    // prefix / postfix
    content.setValueLabelPostfix(json.optString("postfix", content.getValueLabelPostfix()));
    content.setValueLabelPrefix(json.optString("prefix", content.getValueLabelPrefix()));

    // format
    final String format = json.optString("percentformat");
    if (format != null) {
      content.setPercentLabelFormat(format);
    }
  }

  private static void initHasBorder(HasBorder content, JSONObject json) {
    if (json.has("border")) {
      if (json.isNull("border")) {
        content.setBorder(null);
      } else {
        final String border = json.optString("border", null);
        if (border != null) {
          content.setBorder(new ChartColor(border));
        }
      }
    }

    final String borderStroke = json.optString("borderstroke", null);
    if (borderStroke != null) {
      content.setBorderStroke(new ChartStroke(borderStroke));
    }

    content.setRoundedCorner(json.optInt("corner", content.getRoundedCorner()));
  }

  private static void initHasRegression(HasRegression content, JSONObject json) {

    final String regression = json.optString("regression", null);
    if (regression != null) {
      content.setRegressionColor(new ChartColor(regression));
    }

    final String regressionStroke = json.optString("regressionstroke", null);
    if (regressionStroke != null) {
      content.setRegressionStroke(new ChartStroke(regressionStroke));
    }
  }

  private static void initHasAnnotations(HasAnnotations content, JSONObject json)
    throws JSONException
  {
    final JSONArray annotationsData = json.optJSONArray("annotations");
    if (annotationsData != null) {
      for (int n = 0; n < annotationsData.length(); n++) {
        final JSONObject annotation = annotationsData.getJSONObject(n);
        final String text = annotation.getString("text");

        Double x = annotation.optDouble("x");
        if (Double.isNaN(x)) {
          x = null;
        }
        Double y = annotation.optDouble("y");
        if (Double.isNaN(y)) {
          y = null;
        }

        final Integer pos = annotation.optInt("pos");
        final Integer series = annotation.optInt("series");

        ChartFont font = null;
        final String fontText = json.optString("font", null);
        if (fontText != null) {
          font = new ChartFont(fontText);
        }

        ChartColor foreground = null;
        final String foregroundText = json.optString("foreground", null);
        if (foregroundText != null) {
          foreground = new ChartColor(foregroundText);
        }

        ChartColor background = null;
        final String backgroundText = json.optString("background", null);
        if (backgroundText != null) {
          background = new ChartColor(backgroundText);
        }

        content.addAnnotation(x, y, pos, series, foreground,
          background, font, text
        );
      }
    }
  }

  // -------------------------------------------------------------------------
  // helpers
  // -------------------------------------------------------------------------

  private static String[] getStringArray(JSONObject json, String name) throws JSONException {
    final JSONArray array = json.optJSONArray(name);
    if (array == null) {
      return null;
    }

    final String[] result = new String[array.length()];
    for (int n = 0; n < result.length; n++) {
      result[n] = array.getString(n);
    }

    return result;
  }

  private static ChartColor[] getColorArray(JSONObject json, String name) throws JSONException {
    final JSONArray array = json.optJSONArray(name);
    if (array == null) {
      return null;
    }

    final ChartColor[] result = new ChartColor[array.length()];
    for (int n = 0; n < result.length; n++) {
      if (!array.isNull(n)) {
        result[n] = new ChartColor(array.getString(n));
      }
    }

    return result;
  }
}
