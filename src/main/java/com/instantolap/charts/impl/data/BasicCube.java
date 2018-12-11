package com.instantolap.charts.impl.data;

import com.instantolap.charts.Cube;
import com.instantolap.charts.CubeListener;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public abstract class BasicCube implements Cube {

  private List<CubeListener> listeners = new ArrayList<>();

  @Override
  public int getVisibleSampleCount(int dimension) {
    final int sampleCount = getSampleCount(dimension);
    int result = sampleCount;
    for (int n = 0; n < sampleCount; n++) {
      if (!isVisible(dimension, n)) {
        result--;
      }
    }
    return result;
  }

  @Override
  public void addListener(CubeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(CubeListener listener) {
    listeners.remove(listener);
  }

}
