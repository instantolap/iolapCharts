package com.instantolap.charts.renderer;

import com.instantolap.charts.renderer.impl.TextInfo;
import com.instantolap.charts.renderer.popup.Popup;

import java.util.Date;


public interface Renderer {

  int NORTH = 0;
  int EAST = 1;
  int WEST = 2;
  int SOUTH = 3;
  int CENTER = 4;

  void setSize(double width, double height);

  double getWidth();

  double getHeight();

  TextInfo getTextInfo(double x, double y, String text, double rotation, int anchor);

  void drawText(double x, double y, String title, double angle, int anchor, boolean avoidOverlap);

  void drawLine(double x1, double y1, double x2, double y2);

  void setColor(ChartColor color);

  void setStroke(ChartStroke stroke);

  void resetStroke();

  void setFont(ChartFont font);

  void init();

  void finish();

  void fillRect(double x, double y, double width, double height);

  void fillRoundedRect(double x, double y, double width, double height, double arc);

  void clipRoundedRect(double x, double y, double width, double height, double arc);

  void resetClip();

  double getTextWidth(String text);

  double getTextHeight(String text);

  double[] getTextSize(String text, double angle);

  void drawRect(double x, double y, double width, double height);

  void drawRoundedRect(double x, double y, double width, double height, double arc);

  void drawPolyLine(double[] x, double[] y);

  void drawPolygon(double[] x, double[] y);

  void fillPolygon(double[] x, double[] y);

  void fillDonut(double x, double y, double r1, double r2, double a1, double a2,
                 boolean round);

  void drawDonut(double x, double y, double r1, double r2, double a1, double a2,
                 boolean round);

  void drawCircle(double i, double j, double size);

  void fillCircle(double i, double j, double size);

  String format(String format, double v);

  String format(String format, Date v);

  void animate(HasAnimation animated, long duration) throws ChartException;

  void addListener(RendererListener l);

  Popup addPopup(double x, double y, double w, double h, double rotation, int anchor,
                 String text, ChartFont font, Runnable onMouseOver,
                 Runnable onMouseOut, Runnable onMouseClick);

  void setCurrentPopup(Popup popup);

  void addPopup(double xx, double yy, double len0, double len1, double rad, double rad2,
    boolean round, String popupText, ChartFont popupFont,
    Runnable onMouseOver, Runnable onMouseOut, Runnable onMouseClick);

  boolean inPath(double cx1, double cy1, double[] path);

  boolean isInDonut(
    double xx, double yy, double x, double y, double r1, double r2, double a1, double a2, boolean round
  );

  void fireRepaint(boolean buildCubes) throws ChartException;

  void showClickPointer();

  void showNormalPointer();

  void fireOpenLink(String url, String target);

  void enableHandlers(boolean b);

  void openPopup(RendererContent chart) throws ChartException;

  void showError(Exception e);

  void drawBubble(double bx, double by, double bw, double bh, double x, double y, double arc);

  void fillBubble(double bx, double by, double bw, double bh, double x, double y, double arc);

  void addMouseListener(double x, double y, double width, double height, ChartMouseListener listener);
}