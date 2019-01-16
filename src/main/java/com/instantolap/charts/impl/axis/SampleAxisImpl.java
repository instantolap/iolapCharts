package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Cube;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.impl.data.Theme;
import com.instantolap.charts.renderer.Renderer;


public class SampleAxisImpl extends BasicAxisImpl implements SampleAxis {

  private final int dimension;
  private double[] grid;
  private double[] centeredGrid;
  private double[] gridLines;
  private String[] texts;
  private double neededSize;
  private double sampleWidth;
  private boolean isVertical;
  private String[] labels;
  private double size;
  private double[] gridPositions;

  public SampleAxisImpl(Theme theme, int dimension) {
    super(theme);
    this.dimension = dimension;
  }

  @Override
  public double[] getGrid() {
    return grid;
  }

  @Override
  public double[] getCenteredGrid() {
    return centeredGrid;
  }

  @Override
  public double[] getGridLines() {
    if (gridLines != null) {
      return gridLines;
    } else {
      return grid;
    }
  }

  @Override
  public String[] getTexts() {
    return texts;
  }

  @Override
  public double getNeededSize() {
    return neededSize;
  }

  @Override
  public double getSize() {
    return size;
  }

  @Override
  public double getSamplePosition(Cube cube, int sample) {
    return sample * sampleWidth;
  }

  @Override
  public double getSampleWidth() {
    return sampleWidth;
  }

  @Override
  public void setData(
    double width,
    Renderer r,
    int axisNum,
    boolean isStacked,
    boolean isCentered,
    boolean vertical,
    int index) {
    this.isVertical = vertical;

    final Cube cube = getCube();
    final int count = cube.getSampleCount(dimension);
    final String[] labels = getLabels();

    String prefix = getPrefix();
    if (prefix == null) {
      prefix = "";
    }

    String postfix = getPostfix();
    if (postfix == null) {
      postfix = "";
    }

    sampleWidth = (double) width / (double) (count - (isCentered ? 0 : 1));
    grid = new double[count + (isCentered ? 1 : 0)];
    centeredGrid = new double[count];
    texts = new String[count];
    double x = 0;
    neededSize = 0;
    r.setFont(getFont());
    for (int n = 0; n < grid.length; n++) {

      // set grid position
      grid[n] = x;

      if (n >= count) {
        break;
      }

      // add text
      centeredGrid[n] = x + sampleWidth / 2;
      x += sampleWidth;

      // max text height
      if (labels != null) {
        if (n < labels.length) {
          texts[n] = labels[n];
        } else {
          texts[n] = "";
        }
      } else {
        texts[n] = cube.getSample(dimension, n);
      }

      texts[n] = prefix + texts[n] + postfix;

      if (isVisible() && isShowLabels()) {
        r.setFont(getFont());
        final double[] size = r.getTextSize(texts[n], getLabelRotation());
        final double needed = isVertical ? size[0] : size[1];
        neededSize = Math.max(neededSize, needed);
      }
    }

    // calculate custom grid lines
    gridLines = null;
    if (gridPositions != null) {
      gridLines = new double[gridPositions.length];
      for (int n = 0; n < gridPositions.length; n++) {
        gridLines[n] = width * gridPositions[n] / 100.0;
      }
    }

    // set size (for round axes it must be enlarged if the start and end overlap
    size = width;
    if (isExtended() && !isCentered) {
      size += sampleWidth;
    }

    // text padding
    neededSize += getTickWidth();
    if (isShowLabels()) {
      neededSize += getLabelSpacing();
    }

    // title + padding
    neededSize += getNeededTitleWidth(r, vertical);
  }

  @Override
  public String[] getLabels() {
    return labels;
  }

  @Override
  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  @Override
  public void setGridPositions(double[] gridPositions) {
    this.gridPositions = gridPositions;
  }

  protected boolean isExtended() {
    return false;
  }

  @Override
  public boolean isVertical() {
    return isVertical;
  }
}
