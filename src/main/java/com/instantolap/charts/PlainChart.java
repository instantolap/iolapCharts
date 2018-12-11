package com.instantolap.charts;

public interface PlainChart extends Chart {

  PlainCanvas getCanvas();

  @Override
  void addContent(Content content);
}