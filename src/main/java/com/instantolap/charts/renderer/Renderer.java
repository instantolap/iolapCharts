package com.instantolap.charts.renderer;

import com.instantolap.charts.renderer.impl.TextInfo;

import java.util.Date;


public interface Renderer {

  int NORTH = 0;
  int EAST = 1;
  int WEST = 2;
  int SOUTH = 3;
  int CENTER = 4;

  void setSize(int width, int height);

  int getWidth();

  int getHeight();

  TextInfo getTextInfo(int x, int y, String text, double rotation, int anchor);

  void drawText(int x, int y, String title, double angle, int anchor);

  void drawLine(int x1, int y1, int x2, int y2);

  void setColor(ChartColor color);

  void setStroke(ChartStroke stroke);

  void resetStroke();

  void setFont(ChartFont font);

  void init();

  void finish();

  void fillRect(int x, int y, int width, int height);

  void fillRoundedRect(int x, int y, int width, int height, int arc);

  void clipRoundedRect(int x, int y, int width, int height, int arc);

  void resetClip();

  int getTextWidth(String text);

  int getTextHeight(String text);

  double[] getTextSize(String text, double angle);

  void drawRect(int x, int y, int width, int height);

  void drawRoundedRect(int x, int y, int width, int height, int arc);

  void drawPolyLine(int[] x, int[] y);

  void drawPolygon(int[] x, int[] y);

  void fillPolygon(int[] x, int[] y);

  void fillDonut(int x, int y, int r1, int r2, double a1, double a2,
    boolean round);

  void drawDonut(int x, int y, int r1, int r2, double a1, double a2,
    boolean round);

  void drawCircle(int i, int j, int size);

  void fillCircle(int i, int j, int size);

  String format(String format, double v);

  String format(String format, Date v);

  void animate(HasAnimation animated, long duration) throws ChartException;

  void addListener(RendererListener l);

  void addPopup(int x, int y, int w, int h, int rotation, int anchor,
    String text, ChartFont font, Runnable onMouseOver,
    Runnable onMouseOut, Runnable onMouseClick);

  void addPopup(int xx, int yy, int len0, int len1, double rad, double rad2,
    boolean round, String popupText, ChartFont popupFont,
    Runnable onMouseOver, Runnable onMouseOut, Runnable onMouseClick);

  boolean inPath(double cx1, double cy1, double[] path);

  boolean isInDonut(
    int xx, int yy, int x, int y, int r1, int r2, double a1, double a2, boolean round
  );

  void fireRepaint(boolean buildCubes) throws ChartException;

  void showClickPointer();

  void showNormalPointer();

  void fireOpenLink(String url, String target);

  void enableHandlers(boolean b);

  void openPopup(RendererContent chart) throws ChartException;

  void showError(Exception e);

  void drawBubble(int bx, int by, int bw, int bh, int x, int y, int arc);

  void fillBubble(int bx, int by, int bw, int bh, int x, int y, int arc);

  void addMouseListener(int x, int y, int width, int height, ChartMouseListener listener);
}