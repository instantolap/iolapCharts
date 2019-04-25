package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.*;
import com.instantolap.charts.renderer.util.StringHelper;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FxRenderer extends BasicRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(FxRenderer.class);

  private final Map<String, SimpleDateFormat> dateFormats = new HashMap<>();
  private final Canvas image;

  private GraphicsContext graphics;
  private ChartColor color;
  private ChartFont font;
  private ChartStroke stroke;
  private Rectangle clip;

  public FxRenderer(Canvas image) {
    this.image = image;
    graphics = image.getGraphicsContext2D();
    setFont(new Theme().getDefaultFont());
  }

  @Override
  public void setSize(double width, double height) {
  }

  @Override
  public double getWidth() {
    return image.getWidth();
  }

  @Override
  public double getHeight() {
    return image.getHeight();
  }

  @Override
  protected void drawText(TextInfo i, String text) {
    if (i.rad != 0) {
      final Affine at1 = new Affine();
      at1.appendRotation(Math.toDegrees(i.rad), i.rx, i.ry);
      graphics.setTransform(at1);
    }

    drawText(i.tx, i.ty, text);

    // reset transform
    graphics.setTransform(new Affine());
  }

  private void drawText(double x, double y, String text) {
    graphics.setTextBaseline(VPos.BOTTOM);
    final String[] lines = StringHelper.splitString(text, "\n");
    for (int n = lines.length - 1; n >= 0; n--) {
      final String line = lines[n];
      graphics.fillText(line, x, y);
      y -= getTextHeight(line);
      y -= TEXTLINE_SPACING;
    }
  }

  @Override
  public void drawLine(double x1, double y1, double x2, double y2) {
    if (clip != null && !(clip.contains(x1, y1) && clip.contains(x2, y2))) {
      final Line line = new Line(x1, y1, x2, y2);
      final Bounds b = Shape.intersect(line, clip).getBoundsInLocal();
      graphics.strokeLine(b.getMinX(), b.getMaxY(), b.getMaxX(), b.getMinY());
    } else {
      graphics.strokeLine(x1, y1, x2, y2);
    }
  }

  @Override
  public void setStroke(ChartStroke stroke) {
    if (stroke == null) {
      resetStroke();
      return;
    }
    if (!stroke.equals(this.stroke)) {
      this.stroke = stroke;

      graphics.setLineWidth(stroke.getWidth());
      graphics.setLineCap(StrokeLineCap.ROUND);
      graphics.setLineJoin(StrokeLineJoin.ROUND);

      final double len1 = stroke.getLen1();
      final double len2 = stroke.getLen2();
      if ((len1 == 0) || (len2 == 0)) {
        graphics.setLineDashes();
      } else {
        graphics.setLineDashes(len1, len2);
      }
    }
  }

  @Override
  public void resetStroke() {
    graphics.setLineWidth(1);
    graphics.setLineCap(StrokeLineCap.ROUND);
    graphics.setLineJoin(StrokeLineJoin.ROUND);
    graphics.setLineDashes();
    stroke = null;
  }

  @Override
  public void setFont(ChartFont font) {
    if (font == null) {
      font = new Theme().getDefaultFont();
    }
    if (!font.equals(this.font)) {
      this.font = font;

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
  }

  @Override
  public void fillRect(double x, double y, double width, double height) {
    prepareFillRect(x, y, width, height);
    graphics.fillRect(x, y, width, height);
  }

  @Override
  public void fillRoundedRect(double x, double y, double width, double height, double arc) {
    prepareFillRect(x, y, width, height);
    graphics.fillRoundRect(x, y, width, height, arc, arc);
  }

  @Override
  public void clipRoundedRect(double x, double y, double width, double height, double arc) {
    if (width > 0 && height > 0) {
      clip = new Rectangle(x, y, width, height);
      clip.setStrokeWidth(0);
    }
  }

  @Override
  public void resetClip() {
    clip = null;
  }

  @Override
  public void drawRect(double x, double y, double width, double height) {
    graphics.strokeRect(x, y, width, height);
  }

  @Override
  public void drawRoundedRect(double x, double y, double width, double height, double arc) {
    graphics.strokeRoundRect(x, y, width, height, arc, arc);
  }

  @Override
  public void drawPolyLine(double[] x, double[] y) {
    graphics.strokePolyline(x, y, Math.min(x.length, y.length));
  }

  @Override
  public void drawPolygon(double[] x, double[] y) {
    graphics.strokePolygon(x, y, Math.min(x.length, y.length));
  }

  @Override
  public void fillPolygon(double[] x, double[] y) {
    prepareFillPolygon(x, y);
    graphics.fillPolygon(x, y, Math.min(x.length, y.length));
  }

  @Override
  public void fillDonut(double x, double y, double r1, double r2, double a1, double a2,
                        boolean round) {
    prepareFillDonut(x, y, r2, a1, a2);
    getDonutPath(x, y, r1, r2, a1, a2, round);
    graphics.fill();
  }

  @Override
  public void drawDonut(double x, double y, double r1, double r2, double a1, double a2,
                        boolean round) {
    getDonutPath(x, y, r1, r2, a1, a2, round);
    graphics.stroke();
  }

  @Override
  public void drawCircle(double x, double y, double size) {
    graphics.strokeOval(x, y, size, size);
  }

  @Override
  public void fillCircle(double x, double y, double size) {
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
    SimpleDateFormat dateFormat = dateFormats.get(format);
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat(format);
      dateFormats.put(format, dateFormat);
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    return dateFormat.format(v);
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
  public boolean isInDonut(double xx, double yy, double x, double y, double r1, double r2,
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
  public void openPopup(final RendererContent chart) {
  }

  @Override
  public void showError(Exception e) {
    LOGGER.error("Chart error", e);
  }

  @Override
  public void drawBubble(double bx, double by, double bw, double bh, double x, double y, double arc) {
    createBubblePath(bx, by, bw, bh, x, y, arc);
    graphics.stroke();
  }

  @Override
  public void fillBubble(double bx, double by, double bw, double bh, double x, double y, double arc) {
    createBubblePath(bx, by, bw, bh, x, y, arc);
    graphics.closePath();
    graphics.fill();
  }

  private void createBubblePath(double x, double y, double w, double h, double ax,
                                double ay, double rr) {
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
  protected double getTextLineWidth(String text) {
    final Text label = new Text(text);
    label.setFont(graphics.getFont());
    return label.getLayoutBounds().getWidth() - 1;
  }

  @Override
  protected double getTextLineHeight(String text) {
    final Text label = new Text(text);
    label.setFont(graphics.getFont());
    return label.getLayoutBounds().getHeight() - 1;
  }

  @Override
  protected void setGradient(double x, double y, double width, double height) {
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
    if (!color.equals(this.color)) {
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
  }

}
