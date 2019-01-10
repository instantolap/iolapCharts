package com.instantolap.charts.impl.content;

import com.instantolap.charts.Cube;
import com.instantolap.charts.PieContent;
import com.instantolap.charts.impl.data.Palette;
import com.instantolap.charts.renderer.ChartColor;

import java.util.ArrayList;
import java.util.List;


public abstract class BasicPieContentImpl extends BasicSampleContentImpl implements PieContent {

  private final List<Integer> detachedSamples = new ArrayList<>();
  private double seriesSpace;
  private double detachedDistance = 0.2;
  private double startAngle;
  private boolean isRound = true;
  private String measure = Cube.MEASURE_VALUE;

  public BasicPieContentImpl(Palette palette) {
    super(palette);

    setOutline(ChartColor.BLACK);
  }

  public String[] getMeasures() {
    return new String[]{measure};
  }

  @Override
  public void setSeriesSpace(double space) {
    this.seriesSpace = space;
  }

  @Override
  public double getSeriesSpace() {
    return seriesSpace;
  }

  @Override
  public void addDetachedSample(int sample) {
    detachedSamples.add(sample);
  }

  @Override
  public List<Integer> getDetachedSamples() {
    return detachedSamples;
  }

  @Override
  public void setDetachedDistance(double distance) {
    this.detachedDistance = distance;
  }

  @Override
  public double getDetachedDistance() {
    return detachedDistance;
  }

  @Override
  public void setStartAngle(double angle) {
    this.startAngle = angle;
  }

  @Override
  public double getStartAngle() {
    return startAngle;
  }

  @Override
  public void setRound(boolean round) {
    this.isRound = round;
  }

  @Override
  public boolean isRound() {
    return this.isRound;
  }

  @Override
  public void setMeasure(String measure) {
    this.measure = measure;
  }

  @Override
  public String getMeasure() {
    return measure;
  }


}
