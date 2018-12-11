package com.instantolap.charts.renderer.impl.annotation;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;
import com.instantolap.charts.renderer.impl.TextInfo;


public class AnnotationDrawer {

  public static void draw(Renderer r, ChartFont font, ChartColor foreground,
    ChartColor background, Integer pointerX, Integer pointerY,
    Integer boxX, Integer boxY, int anchor, int rotation, String text)
  {
    // find bubble size

    final int x;
    final int y;
    if (pointerX != null) {
      x = pointerX;
      y = pointerY;
    } else if (boxX != null) {
      x = boxX;
      y = boxY;
    } else {
      return;
    }

    r.setFont(font);
    final TextInfo i = r.getTextInfo(x, y, text, rotation, anchor);

    final double padding = 5;
    final double distance = 20;

    int bx = (int) (i.x - padding);
    int by = (int) (i.y - padding);
    final int bw = (int) (i.w + 2 * padding);
    final int bh = (int) (i.h + 2 * padding);

    if (pointerX != null && pointerY != null) {
      switch (anchor) {
        case Renderer.NORTH:
          by = (int) (pointerY - distance - bh);
          if (by < 0) {
            by = (int) (pointerY + distance);
          }
          break;
        case Renderer.SOUTH:
          by = (int) (pointerY + distance);
          if (by + bh > r.getHeight()) {
            by = (int) (pointerY - distance - bh);
          }
          break;
        case Renderer.WEST:
          bx = (int) (pointerX - distance - bw);
          if (bx < 0) {
            bx = (int) (pointerX + distance);
          }
          break;
        case Renderer.EAST:
          bx = (int) (pointerX + distance);
          if (bx + bw > r.getWidth()) {
            bx = (int) (pointerX - distance - bw);
          }
          break;
      }
    }

    // move into area
    if (boxX != null) {
      bx = boxX;
    }
    bx = Math.max(bx, 0);
    bx = Math.min(bx, r.getWidth() - bw);

    if (boxY != null) {
      by = boxY;
    }
    by = Math.max(by, 0);
    by = Math.min(by, r.getHeight() - bh);

    final int arc = 5;

    r.setColor(ChartColor.SHADOW);
    if (pointerX != null) {
      r.fillBubble(bx + 3, by + 3, bw, bh, pointerX + 3, pointerY + 3, arc);
    } else {
      r.fillRoundedRect(bx + 3, by + 3, bw, bh, arc);
    }

    r.setColor(background);
    if (pointerX != null) {
      r.fillBubble(bx, by, bw, bh, pointerX, pointerY, arc);
    } else {
      r.fillRoundedRect(bx, by, bw, bh, arc);
    }

    r.setColor(ChartColor.BLACK);
    if (pointerX != null) {
      r.drawBubble(bx, by, bw, bh, pointerX, pointerY, arc);
    } else {
      r.drawRoundedRect(bx, by, bw, bh, arc);
    }

    r.setColor(foreground);
    r.drawText(bx + bw / 2, by + bh / 2, text, rotation, Renderer.CENTER);
  }
}
