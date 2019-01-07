package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.renderer.*;
import com.instantolap.charts.renderer.popup.AreaListener;
import com.instantolap.charts.renderer.popup.RectAreaListener;

import java.util.ArrayList;
import java.util.List;


public class MouseListeners {

  private final List<AreaListener> areas = new ArrayList<>();
  private boolean isDragging;
  private int startX, startY;

  public void addMouseListener(int x, int y, int width, int height, ChartMouseListener listener) {
    areas.add(new RectAreaListener(x, y, width, height, listener));
  }

  public void clear() {
    areas.clear();
  }

  public boolean fireMouseWheel(int x, int y, int delta) {
    for (AreaListener a : areas) {
      if (a.isInside(x, y)) {
        final ChartMouseListener l = a.getListener();
        if (l instanceof ChartMouseWheelListener) {
          final ChartMouseWheelListener w = (ChartMouseWheelListener) l;
          w.onMouseWheel(x, y, delta);
          return true;
        }
      }
    }

    return false;
  }

  public void fireMouseDown(int x, int y) {
    isDragging = true;
    startX = x;
    startY = y;

    for (AreaListener a : areas) {
      if (a.isInside(x, y)) {
        final ChartMouseListener l = a.getListener();
        if (l instanceof ChartMouseDownListener) {
          final ChartMouseDownListener w = (ChartMouseDownListener) l;
          w.onMouseDown(x, y);
          break;
        }
      }
    }
  }

  public void fireMouseUp(int x, int y) {
    isDragging = false;

    for (AreaListener a : areas) {
      if (a.isInside(x, y)) {
        final ChartMouseListener l = a.getListener();
        if (l instanceof ChartMouseUpListener) {
          final ChartMouseUpListener w = (ChartMouseUpListener) l;
          w.onMouseUp(x, y);
          break;
        }
      }
    }
  }

  public void fireMouseMove(int x, int y) {
    if (isDragging) {
      final int dX = startX - x;
      final int dY = startY - y;

      for (AreaListener a : areas) {
        if (a.isInside(x, y)) {
          final ChartMouseListener l = a.getListener();
          if (l instanceof ChartMouseDragListener) {
            final ChartMouseDragListener w = (ChartMouseDragListener) l;
            w.onMouseDrag(dX, dY);
            break;
          }
        }
      }

      startX = x;
      startY = y;
    }

    for (AreaListener a : areas) {
      if (a.isInside(x, y)) {
        final ChartMouseListener l = a.getListener();
        if (l instanceof ChartMouseMoveListener) {
          final ChartMouseMoveListener w = (ChartMouseMoveListener) l;
          w.onMouseMove(x, y);
          break;
        }
      }
    }
  }
}
