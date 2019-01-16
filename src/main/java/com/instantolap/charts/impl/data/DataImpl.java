package com.instantolap.charts.impl.data;

import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.WriteableCube;
import com.instantolap.charts.impl.util.ArrayHelper;
import com.instantolap.charts.impl.util.SymbolDrawer;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class DataImpl implements Data {

  private final transient Map<Integer, Integer> selectedSamples = new HashMap<>();
  private WriteableCube cube;
  private Cube currentCube;
  private ChartColor[][] sampleColors = new ChartColor[0][];
  private ChartStroke defaultStroke = new ChartStroke(1);
  private ChartStroke[] strokes = new ChartStroke[0];
  private int defaultSymbol = SymbolDrawer.SYMBOL_CIRCLE_OPAQUE;
  private int[] symbols = new int[0];
  private int defaultSymbolSize = 0;
  private int[] symbolSizes = new int[0];

  public DataImpl() {
    cube = new CubeImpl();
    currentCube = cube;
  }

  @Override
  public WriteableCube getMainCube() {
    return cube;
  }

  @Override
  public Cube getCurrentCube() {
    return currentCube;
  }

  @Override
  public void setCurrentCube(Cube cube) {
    this.currentCube = cube;
  }

  @Override
  public void setColors(int range, ChartColor[] sampleColors) {
    this.sampleColors = Arrays.copyOfRange(this.sampleColors, 0, range + 1);
    this.sampleColors[range] = sampleColors;
  }

  @Override
  public ChartColor[] getColors(Theme theme, int range) {
    if (sampleColors.length == 0) {
      return theme.getColors();
    }

    range = Math.min(sampleColors.length, range);
    return sampleColors[range];
  }

  @Override
  public ChartColor getColor(Theme theme, int range, int series) {
    ChartColor[] colors = getColors(theme, range);
    return colors[series % colors.length];
  }

  @Override
  public void setStroke(int series, ChartStroke stroke) {
    if (series < 0) {
      defaultStroke = stroke;
    } else {
      strokes = ArrayHelper.add(strokes, series, stroke);
    }
  }

  @Override
  public ChartStroke getStroke(int series) {
    if (strokes.length <= series || strokes[series] == null) {
      return defaultStroke;
    }

    return strokes[series];
  }

  @Override
  public void setSymbol(int series, int symbol) {
    if (series < 0) {
      defaultSymbol = symbol;
    } else {
      symbols = ArrayHelper.add(symbols, series, symbol);
    }
  }

  @Override
  public int getSymbol(int series) {
    if (symbols.length <= series || symbols[series] == 0) {
      return defaultSymbol;
    }
    return symbols[series];
  }

  @Override
  public void setSymbolSize(int series, int size) {
    if (series < 0) {
      defaultSymbolSize = size;
    } else {
      symbolSizes = ArrayHelper.add(symbolSizes, series, size);
    }
  }

  @Override
  public int getSymbolSize(int series) {
    if (symbolSizes.length <= series) {
      return defaultSymbolSize;
    }
    return symbolSizes[series];
  }

  @Override
  public void setSelectedSample(int dimension, Integer pos) {
    if (pos == null) {
      selectedSamples.remove(dimension);
    } else {
      selectedSamples.put(dimension, pos);
    }
  }

  @Override
  public Integer getSelectedSample(int dimension) {
    return selectedSamples.get(dimension);
  }

  @Override
  public boolean isSelected(int dimension, int sample) {
    final Integer selected = selectedSamples.get(dimension);
    if (selected == null) {
      return false;
    } else {
      return (selected == sample);
    }
  }

  @Override
  public boolean hasSelection(int dimension) {
    return selectedSamples.get(dimension) != null;
  }

  @Override
  public Data getCopy() {
    final DataImpl copy = new DataImpl();
    copy.cube = cube;
    copy.currentCube = currentCube;
    copy.sampleColors = sampleColors;
    copy.strokes = strokes;
    copy.symbols = symbols;
    copy.symbolSizes = symbolSizes;
    copy.selectedSamples.putAll(selectedSamples);
    return copy;
  }

  public Cube getContentSubCube(int content) {
    return getCurrentCube();
  }

  public void setSampleVisible(int dimension, int sample, boolean visible) {
    cube.setVisible(dimension, sample, visible);
  }

  public boolean isSampleVisible(int dimension, int sample) {
    return currentCube.isVisible(dimension, sample);
  }

  public String getTarget(int series, int sample) {
    return null;
  }

  public String getURL(int series, int sample) {
    return null;
  }
}
