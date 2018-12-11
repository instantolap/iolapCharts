package com.instantolap.charts.impl.data;

import com.instantolap.charts.WriteableCube;
import com.instantolap.charts.impl.util.ArrayHelper;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class CubeImpl extends BasicCube implements WriteableCube {

  private Map<String, double[]> measureValues = new HashMap<>();
  private Map<String, String[]> stringMeasureValues = new HashMap<>();
  private double[] values;
  private int[] indices;
  private String[][][] dimensions = new String[0][][];
  private boolean[][] visibilities;
  private int size;
  private boolean isInitialized;

  public CubeImpl() {
  }

  @Override
  public void setDimensionSize(int axis, int size) {
    String[][] samples = getDimension(axis);
    if (samples == null) {
      samples = new String[size][2];
      setDimension(axis, samples);
    } else if (samples.length < size) {
      final String[][] newSamples = new String[size][2];
      System.arraycopy(samples, 0, newSamples, 0, samples.length);
      setDimension(axis, newSamples);
    }
  }

  @Override
  public void addSample(int axis, String sample) {
    addSample(axis, sample, sample);
  }

  @Override
  public void addSample(int axis, String sample, String id) {
    int interval = 1;
    if (isInitialized) {
      for (int n = 0; n <= axis; n++) {
        interval *= getSampleCount(n);
      }
    }

    String[][] samples = getDimension(axis);
    if (samples == null) {
      samples = new String[][]{{sample, id}};
    } else {
      samples = ArrayHelper.add(samples, new String[]{sample, id});
    }
    setDimension(axis, samples);

    // reorganize?
    if (isInitialized) {
      final int newSize = calcSize();

      values = reorganize(values, newSize, interval);
      for (Map.Entry<String, double[]> entry : measureValues.entrySet()) {
        values = entry.getValue();
        values = reorganize(values, newSize, interval);
        measureValues.put(entry.getKey(), values);
      }

      size = newSize;
      buildIndices();
    }
  }

  @Override
  public void set(String measure, Double v, int pos1) {
    final double[] values = getDoubleMeasure(measure, true);
    final Integer ord = getOrdinal(pos1);
    if (ord != null) {
      if (v == null) {
        values[ord] = Double.NaN;
      } else {
        values[ord] = v;
      }
    }
  }

  @Override
  public void set(String measure, Double v, int pos1, int pos2) {
    final double[] values = getDoubleMeasure(measure, true);
    final Integer ord = getOrdinal(pos1, pos2);
    if (ord != null) {
      if (v == null) {
        values[ord] = Double.NaN;
      } else {
        values[ord] = v;
      }
    }
  }

  @Override
  public void set(String measure, Double v, int[] pos) {
    final double[] values = getDoubleMeasure(measure, true);
    final Integer ord = getOrdinal(pos);
    if (ord != null) {
      if (v == null) {
        values[ord] = Double.NaN;
      } else {
        values[ord] = v;
      }
    }
  }

  @Override
  public void set(String measure, String v, int... pos) {
    final String[] values = getStringMeasure(measure, true);
    final Integer ord = getOrdinal(pos);
    if (ord != null) {
      values[ord] = v;
    }
  }

  private String[][] getDimension(int axis) {
    if (dimensions.length > axis) {
      return dimensions[axis];
    }
    return null;
  }

  private void setDimension(int axis, String[][] samples) {
    if (axis < dimensions.length) {
      dimensions[axis] = samples;
    } else {
      final String[][][] newDimensions = new String[axis + 1][][];
      System.arraycopy(dimensions, 0, newDimensions, 0, dimensions.length);
      newDimensions[axis] = samples;
      dimensions = newDimensions;
    }
  }

  private void buildIndices() {
    int index = 1;
    final int dimensions = getAxisCount();
    indices = new int[dimensions];
    for (int n = 0; n < dimensions; n++) {
      indices[n] = index;
      index *= getSampleCount(n);
    }
  }

  private double[] reorganize(double[] values, int newSize, int interval) {
    final double[] newValues = new double[newSize];
    int target = 0;
    for (int n = 0; n < size; n++) {
      newValues[target++] = values[n];
      if (n % interval == interval - 1) {
        newValues[target++] = Double.NaN;
      }
    }
    return newValues;
  }

  @Override
  public int getDimensionCount() {
    return dimensions.length;
  }

  @Override
  public int getSampleCount(int axis) {
    final String[][] samples = getDimension(axis);
    if (samples == null) {
      return 0;
    }
    return samples.length;
  }

  @Override
  public String getSample(int axis, int pos) {
    final String[][] samples = getDimension(axis);
    if (samples == null) {
      return null;
    }
    return samples[pos][0];
  }

  @Override
  public String getSampleID(int axis, int pos) {
    final String[][] samples = getDimension(axis);
    if (samples == null) {
      return null;
    }
    return samples[pos][1];
  }

  @Override
  public Double get(String measure, int pos1) {
    final double[] values = getDoubleMeasure(measure, false);
    if (values == null) {
      return null;
    } else if (dimensions.length == 0) {
      return null;
    } else if (pos1 < 0 || pos1 >= dimensions[0].length) {
      return null;
    }
    final double v = values[pos1];
    if (Double.isNaN(v)) {
      return null;
    }
    return v;
  }

  @Override
  public Double get(String measure, int pos1, int pos2) {
    if (dimensions.length <= 1) {
      return get(measure, pos1);
    }

    final double[] values = getDoubleMeasure(measure, false);
    if (values == null) {
      return null;
    } else if (dimensions.length < 2) {
      return null;
    } else if (pos1 < 0 || pos1 >= dimensions[0].length) {
      return null;
    } else if (pos2 < 0 || pos2 >= dimensions[1].length) {
      return null;
    }

    final double v = values[pos1 + indices[1] * pos2];
    if (Double.isNaN(v)) {
      return null;
    }
    return v;
  }

  @Override
  public Double get(String measure, int[] pos) {
    final double[] values = getDoubleMeasure(measure, false);
    if (values == null) {
      return null;
    }
    final Integer ord = getOrdinal(pos);
    if (ord == null) {
      return null;
    }
    final double v = values[ord];
    if (Double.isNaN(v)) {
      return null;
    }
    return v;
  }

  @Override
  public String getString(String measure, int... pos) {
    final String[] values = getStringMeasure(measure, false);
    if (values == null) {
      return null;
    }
    final Integer ord = getOrdinal(pos);
    if (ord == null) {
      return null;
    }
    return values[ord];
  }

  @Override
  public Double getMin(String... measures) {
    Double min = null;
    for (String measure : measures) {
      final double[] values = getDoubleMeasure(measure, false);
      if (values != null) {
        for (double v : values) {
          if (!Double.isNaN(v)) {
            if (min == null) {
              min = v;
            } else {
              min = Math.min(min, v);
            }
          }
        }
      }
    }
    return min;
  }

  @Override
  public Double getMax(String... measures) {
    Double max = null;
    for (String measure : measures) {
      final double[] values = getDoubleMeasure(measure, false);
      if (values != null) {
        for (double v : values) {
          if (!Double.isNaN(v)) {
            if (max == null) {
              max = v;
            } else {
              max = Math.max(max, v);
            }
          }
        }
      }
    }
    return max;
  }

  @Override
  public void setVisible(int dimension, int sample, boolean visible) {
    if (visibilities == null) {
      visibilities = new boolean[dimensions.length][];
    }
    boolean[] dimensionVisibilities = visibilities[dimension];
    if (dimensionVisibilities == null) {
      dimensionVisibilities = new boolean[getSampleCount(dimension)];
      for (int n = 0; n < dimensionVisibilities.length; n++) {
        dimensionVisibilities[n] = true;
      }
      visibilities[dimension] = dimensionVisibilities;
    }
    dimensionVisibilities[sample] = visible;
  }

  @Override
  public boolean isVisible(int dimension, int sample) {
    if (visibilities == null) {
      return true;
    }
    final boolean[] dimensionVisibilities = visibilities[dimension];
    if (dimensionVisibilities == null) {
      return true;
    }
    return dimensionVisibilities[sample];
  }

  public int getAxisCount() {
    return dimensions.length;
  }

  public void init() {
    if (isInitialized) {
      return;
    }
    isInitialized = true;

    size = calcSize();

    buildIndices();

    // basic values
    values = new double[size];
  }

  private int calcSize() {
    int size = 1;
    for (int n = 0; n < getAxisCount(); n++) {
      size *= getSampleCount(n);
    }
    return size;
  }

  private Integer getOrdinal(int pos) {
    if (pos >= dimensions[0].length) {
      return null;
    }

    return pos;
  }

  private Integer getOrdinal(int pos1, int pos2) {
    if (indices.length < 2) {
      return getOrdinal(pos1);
    } else if (pos1 >= dimensions[0].length) {
      return null;
    } else if (pos2 >= dimensions[1].length) {
      return null;
    }

    return pos1 + indices[1] * pos2;
  }

  private Integer getOrdinal(int[] pos) {
    int ord = 0;
    final int len = Math.min(pos.length, indices.length);
    for (int n = 0; n < len; n++) {
      if (pos[n] < 0 || pos[n] >= dimensions[n].length) {
        return null;
      }
      ord += indices[n] * pos[n];
    }
    return ord;
  }

  private double[] getDoubleMeasure(String measure, boolean create) {
    init();

    if (MEASURE_VALUE.equals(measure)) {
      return values;
    }

    double[] values = measureValues.get(measure);
    if (values != null || !create) {
      return values;
    }

    values = new double[size];
    for (int n = 0; n < size; n++) {
      values[n] = Double.NaN;
    }
    measureValues.put(measure, values);
    return values;
  }

  private String[] getStringMeasure(String measure, boolean create) {
    init();

    String[] values = stringMeasureValues.get(measure);
    if (values != null || !create) {
      return values;
    }

    values = new String[size];
    stringMeasureValues.put(measure, values);
    return values;
  }
}
