package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface HasValueLabels {

  int AUTO = 0;
  int INSIDE = 1;
  int OUTSIDE = 2;
  int POINTER = 3;

  boolean isShowValueLabels();

  void setShowValueLabels(boolean show);

  boolean isShowSeriesLabels();

  void setShowSeriesLabels(boolean show);

  boolean isShowSampleLabels();

  void setShowSampleLabels(boolean show);

  boolean isShowPercentLabels();

  void setShowPercentLabels(boolean show);

  String getPercentLabelFormat();

  void setPercentLabelFormat(String format);

  int getValueLabelType();

  void setValueLabelType(int type);

  String getValueLabelPrefix();

  void setValueLabelPrefix(String prefix);

  String getValueLabelPostfix();

  void setValueLabelPostfix(String prefix);

  ChartColor getLabelColor();

  void setLabelColor(ChartColor color);

  int getLabelAngle();

  void setLabelAngle(int angle);

  int getLabelSpacing();

  void setLabelSpacing(int spacing);

  ChartFont getLabelFont();

  void setLabelFont(ChartFont font);

  ChartFont getPopupFont();

  void setPopupFont(ChartFont font);

  boolean isShowPopup();

  void setShowPopup(boolean show);

  boolean isShowSamplePopup();

  void setShowSamplePopup(boolean show);

  boolean isShowSeriesPopup();

  void setShowSeriesPopup(boolean show);

  boolean isShowValuePopup();

  void setShowValuePopup(boolean show);

  boolean isShowPercentPopup();

  void setShowPercentPopup(boolean show);
}