package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.PlainCanvas;
import com.instantolap.charts.impl.data.Palette;


public abstract class BasicPlainCanvasImpl extends BasicCanvasImpl implements PlainCanvas {

  public BasicPlainCanvasImpl(Palette palette) {
    super(palette);

    setBorder(null);
    setShadow(null);
  }
}
