package com.instantolap.charts.impl.canvas;

import com.instantolap.charts.Data;
import com.instantolap.charts.impl.animation.CanvasAnimation;
import com.instantolap.charts.impl.animation.FadeInCanvasAnim;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.Renderer;


public class PlainCanvasImpl extends BasicPlainCanvasImpl {

  private final CanvasAnimation anim;

  public PlainCanvasImpl(Theme theme) {
    super(theme);

    anim = new FadeInCanvasAnim();
  }

  public void render(double progress, Renderer r, int x, int y, int width, int height, Data data) {
    super.render(anim, progress, r, x, y, width, height);
    super.postRender(anim, progress, r, x, y, width, height);
  }
}
