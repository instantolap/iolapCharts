package com.instantolap.charts;

import com.instantolap.charts.impl.animation.ContentAnimation;


public interface Content extends HasSamples {

  ContentAnimation getAnimation();

  void setAnimation(ContentAnimation animation);

  boolean needsSampleLegend();

  void setCube(Cube cube);
}
