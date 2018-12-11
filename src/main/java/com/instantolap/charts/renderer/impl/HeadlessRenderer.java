package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.ChartStroke;
import com.instantolap.charts.renderer.HasAnimation;
import com.instantolap.charts.renderer.RendererContent;
import com.instantolap.charts.renderer.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HeadlessRenderer extends BasicRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessRenderer.class);

  private Graphics2D graphics;
  private ChartColor color;
  private BufferedImage image;

  @Override
  public void setSize(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    graphics = (Graphics2D) image.getGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    setFont(ChartFont.DEFAULT_FONT);
  }

  @Override
  public int getWidth() {
    return image.getWidth();
  }

  @Override
  public int getHeight() {
    return image.getHeight();
  }

  @Override
  public void drawText(int x, int y, String text, double angle, int anchor) {
    if (text == null) {
      return;
    }

    final TextInfo i = getTextInfo(x, y, text, angle, anchor);

    if (i.rad != 0) {
      final AffineTransform at1 = new AffineTransform();
      at1.setToRotation(i.rad, i.rx, i.ry);
      graphics.setTransform(at1);
    }

    drawText((int) i.tx, (int) i.ty, text);

    // reset transform
    graphics.setTransform(new AffineTransform());
  }

  private void drawText(int x, int y, String text) {
    final String[] lines = StringHelper.splitString(text, "\n");
    for (int n = lines.length - 1; n >= 0; n--) {
      final String line = lines[n];
      graphics.drawString(line, x, y);
      y -= getTextHeight(line);
      y -= TEXTLINE_SPACING;
    }
  }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2) {
    graphics.drawLine(x1, y1, x2, y2);
  }

  @Override
  public void setStroke(ChartStroke stroke) {
    if (stroke == null) {
      resetStroke();
      return;
    }

    final int len1 = stroke.getLen1();
    final int len2 = stroke.getLen2();
    if ((len1 == 0) || (len2 == 0)) {
      graphics.setStroke(new BasicStroke(stroke.getWidth(),
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
      ));
    } else {
      graphics.setStroke(new BasicStroke(stroke.getWidth(),
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
        new float[]{len1, len2}, 0f
      ));
    }
  }

  @Override
  public void resetStroke() {
    graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
      BasicStroke.JOIN_ROUND
    ));
  }

  @Override
  public void setFont(ChartFont font) {
    if (font == null) {
      font = ChartFont.DEFAULT_FONT;
    }
    int style = Font.PLAIN;
    if (font.isBold()) {
      style += Font.BOLD;
    }
    if (font.isItalic()) {
      style += Font.ITALIC;
    }
    graphics.setFont(new Font(font.getName(), style, font.getSize()));
  }

  @Override
  public void fillRect(int x, int y, int width, int height) {
    prepareFillRect(x, y, width, height);
    graphics.fillRect(x, y, width, height);
    graphics.setPaint(null);
  }

  @Override
  public void fillRoundedRect(int x, int y, int width, int height, int arc) {
    prepareFillRect(x, y, width, height);
    graphics.fillRoundRect(x, y, width, height, arc, arc);
    graphics.setPaint(null);
  }

  @Override
  public void clipRoundedRect(int x, int y, int width, int height, int arc) {
    graphics.clip(new RoundRectangle2D.Double(x, y, width, height, arc, arc));
  }

  @Override
  public void resetClip() {
    graphics.setClip(null);
  }

  @Override
  public void drawRect(int x, int y, int width, int height) {
    graphics.drawRect(x, y, width, height);
  }

  @Override
  public void drawRoundedRect(int x, int y, int width, int height, int arc) {
    graphics.drawRoundRect(x, y, width, height, arc, arc);
  }

  @Override
  public void drawPolyLine(int[] x, int[] y) {
    graphics.drawPolyline(x, y, Math.min(x.length, y.length));
  }

  @Override
  public void drawPolygon(int[] x, int[] y) {
    graphics.drawPolygon(x, y, Math.min(x.length, y.length));
  }

  @Override
  public void fillPolygon(int[] x, int[] y) {
    prepareFillPolygon(x, y);
    graphics.fillPolygon(x, y, Math.min(x.length, y.length));
  }

  @Override
  public void fillDonut(int x, int y, int r1, int r2, double a1, double a2,
    boolean round)
  {
    prepareFillDonut(x, y, r2, a1, a2);
    final GeneralPath path = getDonutPath(x, y, r1, r2, a1, a2, round);
    path.closePath();
    graphics.fill(path);
  }

  @Override
  public void drawDonut(int x, int y, int r1, int r2, double a1, double a2,
    boolean round)
  {
    final GeneralPath path = getDonutPath(x, y, r1, r2, a1, a2, round);
    graphics.draw(path);
  }

  @Override
  public void drawCircle(int x, int y, int size) {
    graphics.drawOval(x, y, size, size);
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
    throws ChartException
  {
    animated.render(1);
  }

  @Override
  public boolean inPath(double x, double y, double[] path) {
    final GeneralPath p = new GeneralPath();
    p.moveTo(path[0], path[1]);
    for (int n = 2; n < path.length; n += 2) {
      p.lineTo(path[n], path[n + 1]);
    }
    p.closePath();
    return p.contains(x, y);
  }

  @Override
  public boolean isInDonut(int xx, int yy, int x, int y, int r1, int r2,
    double a1, double a2, boolean round)
  {
    final GeneralPath p = getDonutPath(x, y, r1, r2, a1, a2, round);
    p.closePath();
    return p.contains(xx, yy);
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
    new Thread(() -> {
      try {
        final SwingRenderer swingRenderer = new SwingRenderer();
        swingRenderer.setSize(600, 400);
        chart.setRenderer(swingRenderer);
        chart.setPopup(false);
        chart.render();
      }
      catch (Exception e) {
        LOGGER.error("Unable to open popup", e);
      }
    }).start();
  }

  @Override
  public void showError(Exception e) {
    LOGGER.error("Chart error", e);
  }

  @Override
  public void drawBubble(int bx, int by, int bw, int bh, int x, int y, int arc) {
    final GeneralPath path = createBubblePath(bx, by, bw, bh, x, y, arc);
    graphics.draw(path);
  }

  @Override
  public void fillBubble(int bx, int by, int bw, int bh, int x, int y, int arc) {
    final GeneralPath path = createBubblePath(bx, by, bw, bh, x, y, arc);
    path.closePath();
    graphics.fill(path);
  }

  private GeneralPath createBubblePath(int x, int y, int w, int h, int ax,
    int ay, int rr)
  {
    final GeneralPath path = new GeneralPath();

    final double r = rr / 2.0;
    final double aw = 10;

    final int anchor = findAnchor(x, y, w, h, ax, ay);

    // upper
    path.moveTo(x + r, y);
    if (anchor == NORTH) {
      path.lineTo(x + (w - aw) / 2, y);
      path.lineTo(ax, ay);
      path.lineTo(x + (w + aw) / 2, y);
    }
    path.lineTo(x + w - r, y);

    // upper right
    if (r > 0) {
      path.quadTo(x + w, y, x + w, y + r);
    }

    // right
    if (anchor == EAST) {
      path.lineTo(x + w, y + (h - aw) / 2);
      path.lineTo(ax, ay);
      path.lineTo(x + w, y + (h + aw) / 2);
    }
    path.lineTo(x + w, y + h - r);

    // lower right
    if (r > 0) {
      path.quadTo(x + w, y + h, x + w - r, y + h);
    }

    // lower
    if (anchor == SOUTH) {
      path.lineTo(x + (w + aw) / 2, y + h);
      path.lineTo(ax, ay);
      path.lineTo(x + (w - aw) / 2, y + h);
    }
    path.lineTo(x + r, y + h);

    // lower left
    if (r > 0) {
      path.quadTo(x, y + h, x, y + h - r);
    }

    // left
    if (anchor == WEST) {
      path.lineTo(x, y + (h + aw) / 2);
      path.lineTo(ax, ay);
      path.lineTo(x, y + (h - aw) / 2);
    }
    path.lineTo(x, y + r);

    // upper left
    if (r > 0) {
      path.quadTo(x, y, x + r, y);
    }

    return path;
  }

  private GeneralPath getDonutPath(double x, double y, double r1, double r2,
    double a1, double a2, boolean round)
  {
    a1 = Math.toDegrees(-a1 + (Math.PI / 2.0));
    a2 = Math.toDegrees(-a2 + (Math.PI / 2.0));

    final double arc = a2 - a1;
    final GeneralPath path = new GeneralPath();

    // outer arc
    final Arc2D outer = new Arc2D.Double(x - r2, y - r2, 2 * r2, 2 * r2, a1, arc,
      Arc2D.OPEN
    );
    if (round) {
      path.append(outer, true);
    } else {
      final Point2D start = outer.getStartPoint();
      final Point2D end = outer.getEndPoint();
      path.moveTo(start.getX(), start.getY());
      path.lineTo(end.getX(), end.getY());
    }

    // inner arc
    if (r2 > 0) {
      final Arc2D inner = new Arc2D.Double(x - r1, y - r1, 2 * r1, 2 * r1, a2,
        -arc, Arc2D.CHORD
      );
      if (round) {
        if (Math.abs(a2 - a1) >= 360) {
          final Point2D start = inner.getStartPoint();
          path.moveTo(start.getX(), start.getY());
        }
        path.append(inner, true);
      } else {
        final Point2D start = inner.getStartPoint();
        final Point2D end = inner.getEndPoint();
        path.lineTo(start.getX(), start.getY());
        path.lineTo(end.getX(), end.getY());
      }
    } else {
      try {
        path.lineTo(x, y);
      }
      catch (Exception e) {
        path.moveTo(x, y);
      }
    }

    return path;
  }

  @Override
  protected int getTextLineWidth(String text) {
    final FontMetrics m = graphics.getFontMetrics();
    final Rectangle2D bounds = m.getStringBounds(text, graphics);
    return (int) (bounds.getWidth() - 1);
  }

  @Override
  protected int getTextLineHeight(String text) {
    final FontMetrics m = graphics.getFontMetrics();
    return m.getAscent() - 2;
  }

  @Override
  protected void setGradient(int x, int y, int width, int height) {
    if (color.isGradient()) {
      final GradientPaint paint = new GradientPaint(x, y, graphics.getColor(),
        x + 2 * width, y + 2 * height, Color.WHITE
      );
      graphics.setPaint(paint);
    }
  }

  @Override
  public void setColor(ChartColor color) {
    if (color == null) {
      color = ChartColor.BLACK;
    }
    this.color = color;
    final Color c = new Color(color.getR(), color.getG(), color.getB(),
      color.getA()
    );
    graphics.setColor(c);
  }

  public BufferedImage getImage() {
    return image;
  }
}
