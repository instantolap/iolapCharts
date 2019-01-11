package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.PlainCanvas;
import com.instantolap.charts.impl.data.Theme;


public abstract class BasicPlainCanvasImpl extends BasicCanvasImpl implements PlainCanvas {

  public BasicPlainCanvasImpl(Theme theme) {
    super(theme);

    setBorder(null);
    setShadow(null);
  }
}
