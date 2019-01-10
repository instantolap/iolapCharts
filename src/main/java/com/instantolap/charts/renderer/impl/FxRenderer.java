package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.*;
import com.instantolap.charts.renderer.util.StringHelper;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FxRenderer extends BasicRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(FxRenderer.class);

  private GraphicsContext graphics;
  private ChartColor color;
  private final Canvas image;

  public FxRenderer(Canvas image) {
    this.image = image;
    graphics = image.getGraphicsContext2D();
    setFont(new Palette().getDefaultFont());
  }

  @Override
  public void setSize(int width, int height) {
  }

  @Override
  public int getWidth() {
    return (int) image.getWidth();
  }

  @Override
  public int getHeight() {
    return (int) image.getHeight();
  }

  @Override
  public void drawText(int x, int y, String text, double angle, int anchor) {
    if (text == null) {
      return;
    }

    final TextInfo i = getTextInfo(x, y, text, angle, anchor);

    if (i.rad != 0) {
      final Affine at1 = new Affine();
      at1.appendRotation(Math.toDegrees(i.rad), i.rx, i.ry);
      graphics.setTransform(at1);
    }

    drawText((int) i.tx, (int) i.ty, text);

    // reset transform
    graphics.setTransform(new Affine());
  }

  private void drawText(int x, int y, String text) {
    final String[] lines = StringHelper.splitString(text, "\n");
    for (int n = lines.length - 1; n >= 0; n--) {
      final String line = lines[n];
      graphics.fillText(line, x, y);
      y -= getTextHeight(line);
      y -= TEXTLINE_SPACING;
    }
  }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2) {
    graphics.strokeLine(x1, y1, x2, y2);
  }

  @Override
  public void setStroke(ChartStroke stroke) {
    if (stroke == null) {
      resetStroke();
      return;
    }

    graphics.setLineWidth(stroke.getWidth());
    graphics.setLineCap(StrokeLineCap.ROUND);
    graphics.setLineJoin(StrokeLineJoin.ROUND);

    final int len1 = stroke.getLen1();
    final int len2 = stroke.getLen2();
    if ((len1 == 0) || (len2 == 0)) {
      graphics.setLineDashes();
    } else {
      graphics.setLineDashes(len1, len2);
    }
  }

  @Override
  public void resetStroke() {
    graphics.setLineWidth(1);
    graphics.setLineCap(StrokeLineCap.ROUND);
    graphics.setLineJoin(StrokeLineJoin.ROUND);
    graphics.setLineDashes();
  }

  @Override
  public void setFont(ChartFont font) {
    if (font == null) {
      font = new Palette().getDefaultFont();
    }
    FontWeight style = FontWeight.NORMAL;
    if (font.isBold()) {
      style = FontWeight.BOLD;
    }

    FontPosture posture = FontPosture.REGULAR;
    if (font.isItalic()) {
      posture = FontPosture.ITALIC;
    }

    graphics.setFont(Font.font(font.getName(), style, posture, font.getSize()));
  }

  @Override
  public void fillRect(int x, int y, int width, int height) {
    prepareFillRect(x, y, width, height);
    graphics.fillRect(x, y, width, height);
//    graphics.setPaint(null);
  }

  @Override
  public void fillRoundedRect(int x, int y, int width, int height, int arc) {
    prepareFillRect(x, y, width, height);
    graphics.fillRoundRect(x, y, width, height, arc, arc);
//    graphics.setPaint(null);
  }

  @Override
  public void clipRoundedRect(int x, int y, int width, int height, int arc) {
//    graphics.clip(new RoundRectangle2D.Double(x, y, width, height, arc, arc));
  }

  @Override
  public void resetClip() {
    // graphics.setClip(null);
  }

  @Override
  public void drawRect(int x, int y, int width, int height) {
    graphics.strokeRect(x, y, width, height);
  }

  @Override
  public void drawRoundedRect(int x, int y, int width, int height, int arc) {
    graphics.strokeRoundRect(x, y, width, height, arc, arc);
  }

  @Override
  public void drawPolyLine(int[] x, int[] y) {
    graphics.strokePolyline(convert(x), convert(y), Math.min(x.length, y.length));
  }

  @Override
  public void drawPolygon(int[] x, int[] y) {
    graphics.strokePolygon(convert(x), convert(y), Math.min(x.length, y.length));
  }

  @Override
  public void fillPolygon(int[] x, int[] y) {
    prepareFillPolygon(x, y);
    graphics.fillPolygon(convert(x), convert(y), Math.min(x.length, y.length));
  }

  private double[] convert(int[] a) {
    final double[] result = new double[a.length];
    for (int n = 0; n < result.length; n++) {
      result[n] = a[n];
    }
    return result;
  }

  @Override
  public void fillDonut(int x, int y, int r1, int r2, double a1, double a2,
                        boolean round) {
    prepareFillDonut(x, y, r2, a1, a2);
    getDonutPath(x, y, r1, r2, a1, a2, round);
    graphics.fill();
  }

  @Override
  public void drawDonut(int x, int y, int r1, int r2, double a1, double a2,
                        boolean round) {
    getDonutPath(x, y, r1, r2, a1, a2, round);
    graphics.stroke();
  }

  @Override
  public void drawCircle(int x, int y, int size) {
    graphics.strokeOval(x, y, size, size);
  }

  @Override
  public void fillCircle(int x, int y, int size) {
    prepareFillRect(x, y, size, size);
    graphics.fillOval(x, y, size, size);
  }

  @Override
  public String format(String format, double v) {
    if (format == null) {
      return null;
    }
    return new DecimalFormat(format).format(v);
  }

  @Override
  public String format(String format, Date v) {
    return new SimpleDateFormat(format).format(v);
  }

  @Override
  public void animate(final HasAnimation animated, final long duration)
    throws ChartException {
    animated.render(1);
  }

  @Override
  public boolean inPath(double x, double y, double[] path) {
    graphics.beginPath();
    graphics.moveTo(path[0], path[1]);
    for (int n = 2; n < path.length; n += 2) {
      graphics.lineTo(path[n], path[n + 1]);
    }
    graphics.closePath();
    return graphics.isPointInPath(x, y);
  }

  @Override
  public boolean isInDonut(int xx, int yy, int x, int y, int r1, int r2,
                           double a1, double a2, boolean round) {
    getDonutPath(x, y, r1, r2, a1, a2, round);
    return graphics.isPointInPath(xx, yy);
  }

  @Override
  public void showClickPointer() {
    // not needed in headless mode
  }

  @Override
  public void showNormalPointer() {
    // not needed in headless mode
  }

  @Override
  public void openPopup(final RendererContent chart) throws ChartException {
  }

  @Override
  public void showError(Exception e) {
    LOGGER.error("Chart error", e);
  }

  @Override
  public void drawBubble(int bx, int by, int bw, int bh, int x, int y, int arc) {
    createBubblePath(bx, by, bw, bh, x, y, arc);
    graphics.stroke();
  }

  @Override
  public void fillBubble(int bx, int by, int bw, int bh, int x, int y, int arc) {
    createBubblePath(bx, by, bw, bh, x, y, arc);
    graphics.closePath();
    graphics.fill();
  }

  private void createBubblePath(int x, int y, int w, int h, int ax,
                                int ay, int rr) {
    graphics.beginPath();

    final double r = rr / 2.0;
    final double aw = 10;

    final int anchor = findAnchor(x, y, w, h, ax, ay);

    // upper
    graphics.moveTo(x + r, y);
    if (anchor == NORTH) {
      graphics.lineTo(x + (w - aw) / 2, y);
      graphics.lineTo(ax, ay);
      graphics.lineTo(x + (w + aw) / 2, y);
    }
    graphics.lineTo(x + w - r, y);

    // upper right
    if (r > 0) {
      graphics.quadraticCurveTo(x + w, y, x + w, y + r);
    }

    // right
    if (anchor == EAST) {
      graphics.lineTo(x + w, y + (h - aw) / 2);
      graphics.lineTo(ax, ay);
      graphics.lineTo(x + w, y + (h + aw) / 2);
    }
    graphics.lineTo(x + w, y + h - r);

    // lower right
    if (r > 0) {
      graphics.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    }

    // lower
    if (anchor == SOUTH) {
      graphics.lineTo(x + (w + aw) / 2, y + h);
      graphics.lineTo(ax, ay);
      graphics.lineTo(x + (w - aw) / 2, y + h);
    }
    graphics.lineTo(x + r, y + h);

    // lower left
    if (r > 0) {
      graphics.quadraticCurveTo(x, y + h, x, y + h - r);
    }

    // left
    if (anchor == WEST) {
      graphics.lineTo(x, y + (h + aw) / 2);
      graphics.lineTo(ax, ay);
      graphics.lineTo(x, y + (h - aw) / 2);
    }
    graphics.lineTo(x, y + r);

    // upper left
    if (r > 0) {
      graphics.quadraticCurveTo(x, y, x + r, y);
    }

    graphics.closePath();
  }

  private void getDonutPath(double x, double y, double r1, double r2,
                            double a1, double a2, boolean round) {
    graphics.beginPath();

    a1 = Math.toDegrees(-a1 + (Math.PI / 2.0));
    a2 = Math.toDegrees(-a2 + (Math.PI / 2.0));

    final double arc = a2 - a1;

    // outer arc
    if (round) {
      graphics.arc(x, y, r2, r2, a1, arc);
    } else {
      double startX = x + Math.sin(Math.toRadians(a1)) * r2;
      double startY = y + Math.cos(Math.toRadians(a1)) * r2;
      graphics.moveTo(startX, startY);

      double endX = x + Math.sin(Math.toRadians(a2)) * r2;
      double endY = y + Math.cos(Math.toRadians(a2)) * r2;
      graphics.lineTo(endX, endY);
    }

    // inner arc
    if (r1 > 0) {
      double startX = x + Math.sin(Math.toRadians(a2)) * r1;
      double startY = y + Math.cos(Math.toRadians(a2)) * r1;
      if (round) {
        if (Math.abs(a2 - a1) >= 360) {
        }
        graphics.arc(x, y, r1, r1, a2, -arc);
      } else {
        double endX = x + Math.sin(Math.toRadians(a1)) * r1;
        double endY = y + Math.cos(Math.toRadians(a1)) * r1;
        graphics.lineTo(startX, startY);
        graphics.lineTo(endX, endY);
      }
    } else {
      try {
        graphics.lineTo(x, y);
      } catch (Exception e) {
        graphics.moveTo(x, y);
      }
    }

    graphics.closePath();
  }

  @Override
  protected int getTextLineWidth(String text) {
    final Text fxText = new Text(text);
    fxText.setFont(graphics.getFont());
    return (int) (fxText.getLayoutBounds().getWidth() - 1);
  }

  @Override
  protected int getTextLineHeight(String text) {
    final Text fxText = new Text(text);
    fxText.setFont(graphics.getFont());
    return (int) (fxText.getLayoutBounds().getHeight() - 2);
  }

  @Override
  protected void setGradient(int x, int y, int width, int height) {
    if (color.isGradient()) {
      setColor(color);
      graphics.setStroke(
        new LinearGradient(x, y, x + 2 * width, y + 2 * height, true, null)
      );
    }
  }

  @Override
  public void setColor(ChartColor color) {
    if (color == null) {
      color = ChartColor.BLACK;
    }
    this.color = color;
    final Color c = new Color(
      color.getR() / 255.0,
      color.getG() / 255.0,
      color.getB() / 255.0,
      color.getA() / 255.0
    );
    graphics.setStroke(c);
    graphics.setFill(c);
  }

  public Canvas getImage() {
    return image;
  }
}
