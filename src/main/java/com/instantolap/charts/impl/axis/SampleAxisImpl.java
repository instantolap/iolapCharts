package com.instantolap.charts.impl.axis;

import com.instantolap.charts.Cube;
import com.instantolap.charts.SampleAxis;
import com.instantolap.charts.renderer.Renderer;


public class SampleAxisImpl extends BasicAxisImpl implements SampleAxis {

  private final int dimension;
  private int[] grid;
  private int[] centeredGrid;
  private int[] gridLines;
  private String[] texts;
  private int neededSize;
  private double sampleWidth;
  private boolean isVertical;
  private String[] labels;
  private double size;
  private double[] gridPositions;

  public SampleAxisImpl(int dimension) {
    this.dimension = dimension;
  }

  @Override
  public int[] getGrid() {
    return grid;
  }

  @Override
  public int[] getCenteredGrid() {
    return centeredGrid;
  }

  @Override
  public int[] getGridLines() {
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
  public int getNeededSize() {
    return neededSize;
  }

  @Override
  public int getSize() {
    return (int) size;
  }

  @Override
  public int getSamplePosition(Cube cube, int sample) {
    return (int) (sample * sampleWidth);
  }

  @Override
  public int getSampleWidth() {
    return (int) sampleWidth;
  }

  @Override
  public void setData(
    int width,
    Renderer r,
    int axisNum,
    boolean isStacked,
    boolean isCentered,
    boolean vertical,
    int index)
  {
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
    grid = new int[count + (isCentered ? 1 : 0)];
    centeredGrid = new int[count];
    texts = new String[count];
    double x = 0;
    neededSize = 0;
    r.setFont(getFont());
    for (int n = 0; n < grid.length; n++) {

      // set grid position
      grid[n] = (int) x;

      if (n >= count) {
        break;
      }

      // add text
      centeredGrid[n] = (int) (x + sampleWidth / 2);
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
        final double[] size = r.getTextSize(texts[n], getLabelRotation());
        final double needed = isVertical ? size[0] : size[1];
        neededSize = Math.max(neededSize, (int) needed);
      }
    }

    // calculate custom grid lines
    gridLines = null;
    if (gridPositions != null) {
      gridLines = new int[gridPositions.length];
      for (int n = 0; n < gridPositions.length; n++) {
        gridLines[n] = (int) (width * gridPositions[n] / 100.0);
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
