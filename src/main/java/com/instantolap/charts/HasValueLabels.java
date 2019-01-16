package com.instantolap.charts;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;


public interface HasValueLabels {

  enum ValueLabelType {
    AUTO, INSIDE, OUTSIDE, POINTER
  }

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

  ValueLabelType getValueLabelType();

  void setValueLabelType(ValueLabelType type);

  String getValueLabelPrefix();

  void setValueLabelPrefix(String prefix);

  String getValueLabelPostfix();

  void setValueLabelPostfix(String prefix);

  ChartColor getLabelColor();

  void setLabelColor(ChartColor color);

  double getLabelAngle();

  void setLabelAngle(double angle);

  double getLabelSpacing();

  void setLabelSpacing(double spacing);

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