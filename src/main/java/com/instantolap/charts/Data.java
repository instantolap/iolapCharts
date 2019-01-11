package com.instantolap.charts;

import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;

import java.io.Serializable;


public interface Data extends Serializable {

  Theme getTheme();

  // axes

  WriteableCube getMainCube();

  Cube getCurrentCube();

  void setCurrentCube(Cube cube);

  // display information

  void setColors(int range, ChartColor[] sampleColors);

  ChartColor[] getColors(int range);

  ChartColor getColor(int range, int series);

  // strokes

  void setStroke(int index, ChartStroke stroke);

  ChartStroke getStroke(int index);

  // symbols

  void setSymbol(int index, int symbol);

  int getSymbol(int index);

  void setSymbolSize(int index, int size);

  int getSymbolSize(int index);

  // selections

  void setSelectedSample(int dimension, Integer pos);

  Integer getSelectedSample(int dimension);

  boolean isSelected(int dimension, int sample);

  boolean hasSelection(int dimension);

  Data getCopy();
}
