package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.PlainCanvas;


public abstract class BasicPlainCanvasImpl extends BasicCanvasImpl implements PlainCanvas {

  public BasicPlainCanvasImpl() {
    setBorder(null);
    setShadow(null);
  }
}
