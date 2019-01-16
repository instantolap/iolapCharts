package com.instantolap.charts;

import com.instantolap.charts.renderer.*;

import java.util.List;


public interface Chart extends RendererContent, HasBackground, HasTitle, HasAnimation {
  int NORTH = 0;
  int WEST = 1;
  int SOUTH = 2;
  int EAST = 3;
  int CENTER = 4;

  boolean isPopup();

  boolean isInteractive();

  void setInteractive(boolean popup);

  ChartColor getForeground();

  void setForeground(ChartColor background);

  ChartFont getFont();

  void setFont(ChartFont font);

  String getSubTitle();

  void setSubTitle(String title);

  ChartFont getSubTitleFont();

  void setSubTitleFont(ChartFont font);

  ChartColor getSubTitleColor();

  void setSubTitleColor(ChartColor color);

  Data getData();

  void setData(Data data);

  void setInsets(double top, double left, double right, double bottom);

  double getInsetLeft();

  double getInsetRight();

  double getInsetTop();

  double getInsetBottom();

  Legend getLegend();

  boolean isShowLegend();

  void setShowLegend(boolean show);

  int getLegendPosition();

  void setLegendPosition(int position);

  int getLegendAlignment();

  void setLegendAlignment(int alignment);

  boolean isLegendInside();

  void setLegendInside(boolean inside);

  double getLegendSpacing();

  void setLegendSpacing(double padding);

  void addContent(Content content);

  List<Content> getContents();

  void setContentInsets(double top, double left, double right, double bottom);

  Double getContentInsetLeft();

  Double getContentInsetRight();

  Double getContentInsetTop();

  Double getContentInsetBottom();

  LinkOpener getLinkOpener();

  void setLinkOpener(LinkOpener linkOpener);

  @Override
  void setRenderer(Renderer renderer);

  void renderUnanimated() throws ChartException;

  void repaint(boolean buildCubes) throws ChartException;
}
