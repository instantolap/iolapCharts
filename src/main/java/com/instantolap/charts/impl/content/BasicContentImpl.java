package com.instantolap.charts.impl.content;

import com.instantolap.charts.Content;
import com.instantolap.charts.Cube;
import com.instantolap.charts.impl.animation.ContentAnimation;
import com.instantolap.charts.impl.animation.ContentAnimationAdapter;
import com.instantolap.charts.impl.util.ArrayHelper;


public abstract class BasicContentImpl implements Content {

  private ContentAnimation animation;
  private Cube cube;
  private int[] samples;
  private int colorRange;

  @Override
  public ContentAnimation getAnimation() {
    return animation;
  }

  @Override
  public void setAnimation(ContentAnimation animation) {
    if (animation == null) {
      animation = new ContentAnimationAdapter();
    }
    this.animation = animation;
  }

  protected Cube getCube() {
    return cube;
  }

  @Override
  public void setCube(Cube cube) {
    this.cube = cube;
  }

  @Override
  public void addDisplaySample(int dimension, int sample) {
    samples = ArrayHelper.add(samples, sample);
  }

  @Override
  public int[] getDisplaySamples(int dimension) {
    return samples;
  }

  public int getColorRange() {
    return colorRange;
  }

  public void setColorRange(int colorRange) {
    this.colorRange = colorRange;
  }

  protected boolean isValid(Double v) {
    if (v == null) {
      return false;
    } else if (Double.isNaN(v)) {
      return false;
    } else if (Double.isInfinite(v)) {
      return false;
    } else {
      return true;
    }
  }
}
