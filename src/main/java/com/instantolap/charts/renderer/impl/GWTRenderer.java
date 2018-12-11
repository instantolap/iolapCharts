package com.instantolap.charts.renderer.impl;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.HasAnimation;
import com.instantolap.charts.renderer.RendererContent;
import com.instantolap.charts.renderer.util.StringHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GWTRenderer extends BasicRenderer {

  private final Canvas canvas;
  private final Context2d context;
  private final Map<String, NumberFormat> formats = new HashMap<>();
  private IECanvas ieCanvas;
  private ChartFont font = ChartFont.DEFAULT_FONT;
  private int dashLen1 = 1, dashLen2 = 0;
  private double shift = 0.0;

  public GWTRenderer() throws Exception {
    // init canvas
    canvas = Canvas.createIfSupported();
    FocusWidget focus = null;
    if (canvas != null) {
      context = canvas.getContext2d();
      focus = canvas;
      shift = -0.5;
    } else {
      ieCanvas = IECanvas.createIfSupported();
      if (ieCanvas != null) {
        context = ieCanvas.getContext2d();
        focus = ieCanvas;
      } else {
        throw new Exception("Canvas not supported");
      }
    }

    focus.addMouseOutHandler(event -> {
      try {
        fireMouseOut(event.getX(), event.getY());
      }
      catch (Exception e) {
        showError(e);
      }
    });

    focus.addClickHandler(event -> {
      try {
        fireMouseClick(event.getX(), event.getY());
      }
      catch (Exception e) {
        showError(e);
      }
    });

    focus.addMouseMoveHandler(event -> {
      try {
        mouseListeners.fireMouseMove(event.getX(), event.getY());
        fireMouseMove(event.getX(), event.getY());
      }
      catch (Exception e) {
        showError(e);
      }
    });

    focus.addMouseWheelHandler(event -> {
      try {
        if (mouseListeners.fireMouseWheel(event.getX(), event.getY(), event.getDeltaY())) {
          event.preventDefault();
        }
      }
      catch (Exception e) {
        showError(e);
      }
    });

    focus.addMouseDownHandler(event -> {
      try {
        mouseListeners.fireMouseDown(event.getX(), event.getY());
      }
      catch (Exception e) {
        showError(e);
      }
    });

    focus.addMouseUpHandler(event -> {
      try {
        mouseListeners.fireMouseUp(event.getX(), event.getY());
      }
      catch (Exception e) {
        showError(e);
      }
    });

    setStroke(null);
    setColor(ChartColor.BLACK);
  }

  @Override
  protected int getTextLineWidth(String text) {
    return (int) context.measureText(text).getWidth();
  }

  public Widget getCanvas() {
    if (canvas != null) {
      return canvas;
    } else {
      return ieCanvas;
    }
  }

  @Override
  protected int getTextLineHeight(String text) {
    if (font == null) {
      return 10;
    }
    return font.getSize();
  }

  @Override
  public void setSize(int width, int height) {
    if (canvas != null) {
      canvas.setWidth(width + "px");
      canvas.setCoordinateSpaceWidth(width);
      canvas.setHeight(height + "px");
      canvas.setCoordinateSpaceHeight(height);
    } else {
      ieCanvas.setWidth(width + "px");
      ieCanvas.setCoordinateSpaceWidth(width);
      ieCanvas.setHeight(height + "px");
      ieCanvas.setCoordinateSpaceHeight(height);
    }
  }

  @Override
  protected void setGradient(int x, int y, int width, int height) {
    if (getColor().isGradient()) {
      final CanvasGradient grad = context.createLinearGradient(x, y, x + width * 2, y + height * 2);
      grad.addColorStop(0, getColor().toString());
      grad.addColorStop(1, "#ffffff");
      context.setFillStyle(grad);
    }
  }

  @Override
  public int getWidth() {
    if (canvas != null) {
      return canvas.getCoordinateSpaceWidth();
    } else {
      return ieCanvas.getCoordinateSpaceWidth();
    }
  }

  @Override
  public void setColor(ChartColor color) {
    if (color == null) {
      return;
    }
    super.setColor(color);
    context.setStrokeStyle(color.toString());
    context.setFillStyle(color.toString());
    context.setGlobalAlpha((double) color.getA() / 255.0);
  }

  @Override
  public int getHeight() {
    if (canvas != null) {
      return canvas.getCoordinateSpaceHeight();
    } else {
      return ieCanvas.getCoordinateSpaceHeight();
    }
  }

  @Override
  public void drawPolyLine(int[] x, int[] y) {
    context.beginPath();
    context.moveTo(x[0] + shift, y[0] + shift);
    for (int n = 1; n < x.length; n++) {
      context.lineTo(x[n] + shift, y[n] + shift);
    }
    context.stroke();
  }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2) {
    context.beginPath();
    if ((dashLen1 == 0) || (dashLen2 == 0)) {
      context.moveTo(x1 + shift, y1 + shift);
      context.lineTo(x2 + shift, y2 + shift);
    } else {
      addDashLine(x1, y1, x2, y2);
    }
    context.stroke();
  }

  private void addDashLine(int x1, int y1, int x2, int y2) {
    final double len = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    final int dashLen = dashLen1 + dashLen2;
    final double steps = len / dashLen;
    final double part = (double) dashLen1 / (double) dashLen;

    for (int n = 0; n < steps; n++) {
      final double xx1 = (x1 + (x2 - x1) / steps * n);
      final double yy1 = (y1 + (y2 - y1) / steps * n);
      double xx2 = (x1 + (x2 - x1) / steps * (n + part));
      double yy2 = (y1 + (y2 - y1) / steps * (n + part));

      if (x2 > x1) {
        xx2 = Math.min(x2, xx2);
      } else {
        xx2 = Math.max(x2, xx2);
      }
      if (y2 > y1) {
        yy2 = Math.min(y2, yy2);
      } else {
        yy2 = Math.max(y2, yy2);
      }

      context.moveTo(xx1 + shift, yy1 + shift);
      context.lineTo(xx2 + shift, yy2 + shift);
    }
  }

  @Override
  public void drawRect(int x, int y, int width, int height) {
    context.beginPath();
    if ((dashLen1 == 0) || (dashLen2 == 0)) {
      context.rect(x + shift, y + shift, width, height);
    } else {
      addDashLine(x, y, x + width, y);
      addDashLine(x + width, y, x + width, y + height);
      addDashLine(x + width, y + height, x, y + height);
      addDashLine(x, y + height, x, y);
    }
    context.closePath();
    context.stroke();
  }

  @Override
  public void drawRoundedRect(int xx, int yy, int w, int h, int rr) {
    createRoundedRecPath(xx, yy, w, h, rr);
    context.stroke();
  }

  @Override
  public void fillRect(int xx, int yy, int width, int height) {
    prepareFillRect(xx, yy, width, height);

    final double x = xx + shift;
    final double y = yy + shift;
    context.fillRect(x + shift, y + shift, width, height);
  }

  @Override
  public void fillRoundedRect(int x, int y, int width, int height, int rr) {
    prepareFillRect(x, y, width, height);
    createRoundedRecPath(x, y, width, height, rr);
    context.fill();
  }

  private void createRoundedRecPath(int xx, int yy, int w, int h, int rr) {
    final double x = xx + shift;
    final double y = yy + shift;
    final double r = rr / 2.0;
    context.beginPath();
    context.moveTo(x + r, y);
    context.lineTo(x + w - r, y);
    if (r > 0) {
      context.quadraticCurveTo(x + w, y, x + w, y + r);
    }
    context.lineTo(x + w, y + h - r);
    if (r > 0) {
      context.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    }
    context.lineTo(x + r, y + h);
    if (r > 0) {
      context.quadraticCurveTo(x, y + h, x, y + h - r);
    }
    context.lineTo(x, y + r);
    if (r > 0) {
      context.quadraticCurveTo(x, y, x + r, y);
    }
  }

  @Override
  public void drawPolygon(int[] x, int[] y) {
    setPolygon(x, y);
    context.stroke();
  }

  @Override
  public void fillPolygon(int[] x, int[] y) {
    prepareFillPolygon(x, y);
    setPolygon(x, y);
    context.fill();
  }

  @Override
  public void drawCircle(int x, int y, int size) {
    final int hSize = size / 2;
    if (hSize >= 0) {
      context.beginPath();
      context.arc(x + hSize + shift, y + hSize + shift, hSize, 0, 2 * Math.PI);
      context.closePath();
      context.stroke();
    }
  }

  @Override
  public void fillCircle(int x, int y, int size) {
    final int hSize = size / 2;
    if (hSize >= 0) {
      prepareFillRect(x, y, size, size);
      context.beginPath();
      context.arc(x + hSize + shift, y + hSize + shift, hSize, 0, 2 * Math.PI);
      context.closePath();
      context.fill();
    }
  }

  @Override
  public void fillDonut(int x, int y, int r1, int r2, double a1, double a2, boolean round) {
    prepareFillDonut(x, y, r2, a1, a2);
    createDonutPath(x, y, r1, r2, a1, a2, round);
    context.fill();
  }

  @Override
  public void drawDonut(int x, int y, int r1, int r2, double a1, double a2, boolean round) {
    createDonutPath(x, y, r1, r2, a1, a2, round);
    context.stroke();
  }

  private void createDonutPath(int x, int y, int r1, int r2, double a1, double a2, boolean round) {
    context.beginPath();
    if (round) {
      a1 -= Math.PI / 2;
      a2 -= Math.PI / 2;
      final double s = Math.min(a1, a2);
      final double e = Math.max(a1, a2);
      context.arc(x, y, r1, s, e);
      context.arc(x, y, r2, e, s, true);
    } else {
      context.moveTo(x + r1 * Math.sin(a1), y - r1 * Math.cos(a1));
      context.lineTo(x + r1 * Math.sin(a2), y - r1 * Math.cos(a2));
      context.lineTo(x + r2 * Math.sin(a2), y - r2 * Math.cos(a2));
      context.lineTo(x + r2 * Math.sin(a1), y - r2 * Math.cos(a1));
    }
    context.closePath();
  }

  @Override
  public void setFont(ChartFont font) {
    if (font == null) {
      font = ChartFont.DEFAULT_FONT;
    }
    this.font = font;
    context.setFont(font.toString());
  }

  private void drawText(double x, double y, String text) {
    x += shift;
    y += shift;

    if (!text.contains("\n")) {
      context.fillText(text, x, y);
      return;
    }

    final String[] lines = StringHelper.splitString(text, "\n");
    for (int n = lines.length - 1; n >= 0; n--) {
      final String line = lines[n];
      context.fillText(line, x, y);
      y -= getTextHeight(line);
      y -= TEXTLINE_SPACING;
    }
  }

  @Override
  public void drawText(int x, int y, String text, double angle, int anchor) {
    if (text == null) {
      return;
    }

    final TextInfo i = getTextInfo(x, y, text, angle, anchor);
    context.save();

    context.translate(i.rx, i.ry);
    context.rotate(i.rad);
    context.translate((int) -i.rx, (int) -i.ry);

    drawText(i.tx, i.ty, text);

    // reset transform
    context.restore();
  }


  @Override
  public void setStroke(ChartStroke stroke) {
    if (stroke == null) {
      context.setLineWidth(1);
      dashLen1 = 1;
      dashLen2 = 0;
    } else {
      context.setLineWidth(stroke.getWidth());
      dashLen1 = stroke.getLen1();
      dashLen2 = stroke.getLen2();
    }
  }

  @Override
  public void clipRoundedRect(int x, int y, int w, int h, int arc) {
    context.save();
    createRoundedRecPath(x, y, w, h, arc);
    context.clip();
  }

  @Override
  public void resetClip() {
    context.restore();
  }

  @Override
  public void resetStroke() {
    setStroke(null);
  }

  private void setPolygon(int[] x, int[] y) {
    context.beginPath();
    context.moveTo(x[0] + shift, y[0] + shift);
    for (int n = 0; n < x.length; n++) {
      context.lineTo(x[n] + shift, y[n] + shift);
    }
    context.closePath();
  }

  @Override
  public String format(String format, double v) {
    if (format == null) {
      return null;
    }
    NumberFormat f = formats.computeIfAbsent(format, k -> NumberFormat.getFormat(format));
    return f.format(v);
  }

  @Override
  public String format(String format, Date v) {
    return DateTimeFormat.getFormat(format).format(v);
  }

  @Override
  public void animate(final HasAnimation animated, final long duration) throws ChartException {

    // instant display?
    if (duration <= 0) {
      animated.render(1);
      return;
    }

    final long start = System.currentTimeMillis();
    final Timer timer = new Timer() {

      @Override
      public void run() {
        try {
          final long d = System.currentTimeMillis() - start;
          final double progress = Math.min(1, (double) d / (duration));
          animated.render(progress);
          if (progress >= 1) {
            cancel();
          }
        }
        catch (Exception e) {
          cancel();
        }
      }
    };

    timer.scheduleRepeating(50);
  }

  @Override
  public boolean inPath(double x, double y, double[] path) {
    context.beginPath();
    context.moveTo(path[0], path[1]);
    for (int n = 2; n < path.length; n += 2) {
      context.lineTo(path[n], path[n + 1]);
    }
    context.closePath();
    return context.isPointInPath(x, y);
  }

  @Override
  public boolean isInDonut(
    int xx, int yy, int x, int y, int r1, int r2, double a1, double a2, boolean round)
  {
    createDonutPath(x, y, r1, r2, a1, a2, round);
    return context.isPointInPath(xx, yy);
  }

  @Override
  public void showClickPointer() {
    getCanvas().getElement().getStyle().setCursor(Cursor.POINTER);
  }

  @Override
  public void showNormalPointer() {
    getCanvas().getElement().getStyle().setCursor(Cursor.AUTO);
  }


  @Override
  public void openPopup(RendererContent chart) {
    try {
      final int width = 600;
      final int height = 400;

      final PopupPanel popup = new PopupPanel(true);
      setPopupPosition(width, height, popup);
      popup.setPixelSize(width, height);

      final GWTRenderer renderer = new GWTRenderer();
      renderer.setSize(width, height);
      chart.setRenderer(renderer);
      chart.setPopup(false);

      popup.add(renderer.getCanvas());
      popup.show();
      chart.render();

      Window.addWindowScrollHandler(event -> setPopupPosition(width, height, popup));
    }
    catch (Exception e) {
      GWT.log(e.getMessage(), e);
    }
  }

  private void setPopupPosition(int width, int height, PopupPanel popup) {
    final int screenWidth = Window.getClientWidth();
    final int screenHeight = Window.getClientHeight();
    final int scrollX = Window.getScrollLeft();
    final int scrollY = Window.getScrollTop();
    popup.setPopupPosition(
      scrollX + (screenWidth / 2) - (width / 2), scrollY + (screenHeight / 2) - (height / 2)
    );
  }

  @Override
  public void showError(Exception e) {
    GWT.log(e.getMessage(), e);
  }

  @Override
  public void drawBubble(int bx, int by, int bw, int bh, int x, int y, int arc) {
    createBubblePath(bx, by, bw, bh, x, y, arc);
    context.stroke();
  }

  @Override
  public void fillBubble(int bx, int by, int bw, int bh, int x, int y, int arc) {
    createBubblePath(bx, by, bw, bh, x, y, arc);
    context.fill();
  }

  private void createBubblePath(int xx, int yy, int w, int h, int ax, int ay, int rr) {
    final double x = xx + shift;
    final double y = yy + shift;
    final double r = rr / 2.0;
    final double aw = 10;
    context.beginPath();

    final int anchor = findAnchor(x, y, w, h, ax, ay);

    // upper
    context.moveTo(x + r, y);
    if (anchor == NORTH) {
      context.lineTo(x + (w - aw) / 2, y);
      context.lineTo(ax, ay);
      context.lineTo(x + (w + aw) / 2, y);
    }
    context.lineTo(x + w - r, y);

    // upper right
    if (r > 0) {
      context.quadraticCurveTo(x + w, y, x + w, y + r);
    }

    // right
    if (anchor == EAST) {
      context.lineTo(x + w, y + (h - aw) / 2);
      context.lineTo(ax, ay);
      context.lineTo(x + w, y + (h + aw) / 2);
    }
    context.lineTo(x + w, y + h - r);

    // lower right
    if (r > 0) {
      context.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    }

    // lower
    if (anchor == SOUTH) {
      context.lineTo(x + (w + aw) / 2, y + h);
      context.lineTo(ax, ay);
      context.lineTo(x + (w - aw) / 2, y + h);
    }
    context.lineTo(x + r, y + h);

    // lower left
    if (r > 0) {
      context.quadraticCurveTo(x, y + h, x, y + h - r);
    }

    // left
    if (anchor == WEST) {
      context.lineTo(x, y + (h + aw) / 2);
      context.lineTo(ax, ay);
      context.lineTo(x, y + (h - aw) / 2);
    }
    context.lineTo(x, y + r);

    // upper left
    if (r > 0) {
      context.quadraticCurveTo(x, y, x + r, y);
    }
  }
}