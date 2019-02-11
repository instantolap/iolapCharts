package com.instantolap.charts.impl.util;

import com.instantolap.charts.HasValueLabels.ValueLabelType;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.Renderer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;


public class RoundLabelDrawer {

  private final static LabelComparator COMPARATOR = new LabelComparator(false);
  private final static LabelComparator INVESRE_COMPARATOR = new LabelComparator(true);
  private final List<Label> labels = new ArrayList<>();
  private final Renderer r;
  private final double x;
  private final double y;
  private final double distance;

  public RoundLabelDrawer(Renderer r, double x, double y, double distance) {
    this.r = r;
    this.x = x;
    this.y = y;
    this.distance = distance;
  }

  public void add(double len, double a, ChartColor c1, ChartColor c2, String text, ValueLabelType type) {
    a = correct(a);
    add(len, len, a, a, c1, c2, text, type);
  }

  private double correct(double a) {
    while (a < 0) {
      a += Math.PI * 2;
    }
    while (a > Math.PI * 2) {
      a -= Math.PI * 2;
    }
    return a;
  }

  public void add(
    double len1, double len2, double a1, double a2, ChartColor c1, ChartColor c2, String text, ValueLabelType type) {
    while (a1 < 0) {
      a1 += Math.PI * 2;
      a2 += Math.PI * 2;
    }
    while (a1 > Math.PI * 2) {
      a1 -= Math.PI * 2;
      a2 -= Math.PI * 2;
    }

    switch (type) {
      case INSIDE:
        r.setColor(c2);
        insideLabel(len2, a1, a2, text);
        break;
      case OUTSIDE:
        r.setColor(c1);
        outsideLabel(len2, a1, a2, text);
        break;
      case POINTER:
        pointerLabel(len2, a1, a2, c1, text);
        break;
      case AUTO:
        autoLabel(len1, len2, a1, a2, c1, c2, text);
        break;
    }
  }

  private void insideLabel(double len, double a1, double a2, String text) {
    // calc position
    final double a = (a1 + a2) / 2.0;
    final double x = this.x + Math.sin(a) * len;
    final double y = this.y - Math.cos(a) * len;

    r.drawText(x, y, text, 0, Renderer.CENTER, false);
  }

  private void outsideLabel(double len, double a1, double a2, String text) {

    // calc anchor
    final double a = (a1 + a2) / 2.0;
    final int anchor = getAnchor(a);

    // calc position
    final double x = this.x + Math.sin(a) * len;
    final double y = this.y - Math.cos(a) * len;

    r.drawText(x, y, text, 0, anchor, false);
  }

  private void pointerLabel(double len, double a1, double a2, ChartColor c, String text) {
    final Label label = new Label();
    label.len = len;
    label.text = text;
    label.a = correct((a1 + a2) / 2.0);
    label.color = c;
    labels.add(label);
  }

  public void autoLabel(
    double len1, double len2, double a1, double a2, ChartColor c1, ChartColor c2, String text) {
    while (a1 < 0) {
      a1 += Math.PI * 2;
      a2 += Math.PI * 2;
    }
    while (a1 > Math.PI * 2) {
      a1 -= Math.PI * 2;
      a2 -= Math.PI * 2;
    }

    final double[] size = r.getTextSize(text, 0);
    final double mRad = (a1 + a2) / 2;

    // build pie polygon
    final double x1 = Math.sin(a1) * len1;
    final double y1 = -Math.cos(a1) * len1;
    final double x2 = Math.sin(a2) * len1;
    final double y2 = -Math.cos(a2) * len1;
    final double x3 = Math.sin(a2) * len2;
    final double y3 = -Math.cos(a2) * len2;
    final double x4 = Math.sin(mRad) * len2;
    final double y4 = -Math.cos(mRad) * len2;
    final double x5 = Math.sin(a1) * len2;
    final double y5 = -Math.cos(a1) * len2;
    final double[] path = new double[]{x1, y1, x2, y2, x3, y3, x4, y4, x5, y5};

    r.setColor(ChartColor.BLACK);

    final double textWidth = r.getTextWidth(text);
    final double mLen = len2 - textWidth / 2 - distance;
    final double cx = Math.sin(mRad) * mLen;
    final double cy = -Math.cos(mRad) * mLen;
    final double cx1 = cx - size[0] / 2;
    final double cx2 = cx + size[0] / 2;
    final double cy1 = cy - size[1] / 2;
    final double cy2 = cy + size[1] / 2;

    if (r.inPath(cx1, cy1, path) && r.inPath(cx1, cy2, path)
      && r.inPath(cx2, cy1, path) && r.inPath(cx2, cy2, path)) {
      r.setColor(c2);
      insideLabel(mLen, a1, a2, text);
    } else {
      pointerLabel(len2, a1, a2, c1, text);
    }
  }

  private static int getAnchor(double a) {
    final int anchor;
    if (a <= Math.PI) {
      anchor = Renderer.WEST;
    } else {
      anchor = Renderer.EAST;
    }
    return anchor;
  }

  public void render() {
    final TreeSet<Label> ur = new TreeSet<>(INVESRE_COMPARATOR);
    final TreeSet<Label> lr = new TreeSet<>(COMPARATOR);
    final TreeSet<Label> ll = new TreeSet<>(INVESRE_COMPARATOR);
    final TreeSet<Label> ul = new TreeSet<>(COMPARATOR);

    for (Label label : labels) {
      final double a = label.a;
      if ((a >= 0) && (a < Math.PI / 2)) {
        ur.add(label);
      } else if ((a >= Math.PI / 2) && (a < Math.PI)) {
        lr.add(label);
      } else if ((a >= Math.PI) && (a < Math.PI * 1.5)) {
        ll.add(label);
      } else {
        ul.add(label);
      }
    }

    renderPass(ur, false);
    renderPass(lr, true);
    renderPass(ll, true);
    renderPass(ul, false);
  }

  private void renderPass(TreeSet<Label> labels, boolean dir) {
    double lastY = dir ? Double.MIN_VALUE : Double.MAX_VALUE;
    for (Label label : labels) {
      final double a = label.a;
      final double len = label.len;
      final double len2 = len + distance;
      final String text = label.text;
      r.setColor(label.color);

      final double x1 = x + Math.sin(a) * len;
      final double y1 = y - Math.cos(a) * len;

      final double x2 = x + Math.sin(a) * len2;
      double y2 = y - Math.cos(a) * len2;

      if (dir) {
        if (y2 <= lastY) {
          y2 = lastY;
        }
        lastY = y2 + r.getTextHeight(text);
      } else {
        if (y2 >= lastY) {
          y2 = lastY;
        }
        lastY = y2 - r.getTextHeight(text);
      }

      final int anchor = getAnchor(a);
      final double ex = (len - Math.abs(Math.sin(a)) * len) / 2;

      switch (anchor) {
        case Renderer.SOUTH:
        case Renderer.WEST:
          r.drawLine(x1, y1, x2, y2);
          r.drawLine(x2, y2, x2 + ex, y2);
          r.drawText(x2 + ex + 5, y2, text, 0, anchor, false);
          break;
        case Renderer.NORTH:
        case Renderer.EAST:
          r.drawLine(x1, y1, x2, y2);
          r.drawLine(x2, y2, x2 - ex, y2);
          r.drawText(x2 - ex - 5, y2, text, 0, anchor, false);
          break;
      }
    }
  }


  private static final class Label {
    public double len;
    public double a;
    public ChartColor color;
    public String text;
  }


  private static final class LabelComparator implements Comparator<Label>, Serializable {

    private boolean inverse;

    public LabelComparator() {
    }

    public LabelComparator(boolean inverse) {
      this.inverse = inverse;
    }

    @Override
    public int compare(Label l1, Label l2) {
      final int c = (l1.a < l2.a) ? -1 : 1;
      return c * (inverse ? -1 : +1);
    }
  }
}
