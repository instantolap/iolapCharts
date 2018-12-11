package com.instantolap.charts.impl.chart;

import com.instantolap.charts.Axis;
import com.instantolap.charts.Content;
import com.instantolap.charts.Cube;
import com.instantolap.charts.Data;
import com.instantolap.charts.HasValueAxis;
import com.instantolap.charts.SampleContent;
import com.instantolap.charts.XYCanvas;
import com.instantolap.charts.impl.axis.BasicAxisImpl;
import com.instantolap.charts.impl.canvas.XYCanvasImpl;
import com.instantolap.charts.impl.content.RendererWithSamples;
import com.instantolap.charts.impl.data.PartialCube;
import com.instantolap.charts.impl.util.ArrayHelper;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.ChartFont;
import com.instantolap.charts.renderer.Renderer;


public abstract class BasicMultiAxisChartImpl extends BasicSampleChartImpl {

  private final XYCanvasImpl canvas;
  private BasicAxisImpl xAxis, yAxis1, yAxis2;

  public BasicMultiAxisChartImpl() {
    this.canvas = new XYCanvasImpl();
  }

  protected void setAxes(BasicAxisImpl xAxis, BasicAxisImpl yAxis1,
    BasicAxisImpl yAxis2)
  {
    this.xAxis = xAxis;
    this.yAxis1 = yAxis1;
    this.yAxis2 = yAxis2;
  }

  public XYCanvas getCanvas() {
    return canvas;
  }

  @Override
  public void setRotated(boolean isRotated) {
    super.setRotated(isRotated);

    final int r1 = isRotated ? 0 : 270;
    final int r2 = !isRotated ? 0 : 270;

    if (yAxis1 != null) {
      yAxis1.setTitleRotation(r1);
    }

    if (yAxis2 != null) {
      yAxis2.setTitleRotation(r1);
    }

    if (xAxis != null) {
      xAxis.setTitleRotation(r2);
    }
  }

  @Override
  protected void buildCubes() {
    final Data data = getData();
    if (data == null) {
      return;
    }

    // build x-axis cube
    Cube cube = data.getCurrentCube();
    if (xAxis != null) {
      xAxis.setData(cube);
      cube = xAxis.buildSubCube(cube);
    }

    // collect samples from axis 2
    int[] axis2Samples = null;
    for (Content content : getContents()) {
      if (content instanceof SampleContent) {
        final SampleContent sampleContent = (SampleContent) content;
        if (content instanceof HasValueAxis) {
          final HasValueAxis hasValueAxis = (HasValueAxis) content;
          if (hasValueAxis.getUsedValueAxis() == 1) {
            final int[] samples = sampleContent.getDisplaySamples(1);
            axis2Samples = ArrayHelper.add(axis2Samples, samples);
          }
        }
      }
    }

    // set cube or partial cube to axes
    if (yAxis1 != null) {
      if (axis2Samples != null) {
        final PartialCube subCube = new PartialCube(cube);
        subCube.setVisible(1, axis2Samples, false);
        yAxis1.setData(subCube);
      } else {
        yAxis1.setData(cube);
      }
    }

    if (yAxis2 != null) {
      if (axis2Samples != null) {
        final PartialCube subCube = new PartialCube(cube);
        subCube.keepVisible(1, axis2Samples, false);
        yAxis2.setData(subCube);
      }
      yAxis2.setVisible(yAxis2.isVisible() && (axis2Samples != null));
    }

    // build content subcubes
    buildContentCubes(cube);
  }

  @Override
  protected void render(double progress, int x, int y, int width, int height)
    throws ChartException
  {

    // calc axis sizes
    final Data data = getData();
    final Renderer r = getRenderer();
    if (data == null || r == null) {
      return;
    }

    final boolean isStacked = isStacked();
    final boolean isRotated = isRotated();
    final boolean isCentered = needsCenteredSampleAxis();

    // find axis sizes (2 pass)
    int valueAxisSize1 = 0, valueAxisSize2 = 0, sampleAxisSize = 0;
    int canvasWidth = width, canvasHeight = height;

    for (int n = 0; n < 2; n++) {
      if (yAxis1 != null) {
        yAxis1.setData(
          isRotated ? canvasWidth : canvasHeight, r, 0, isStacked, isCentered, !isRotated, 0
        );
        if (yAxis1.isVisible()) {
          valueAxisSize1 = yAxis1.getNeededSize();
        }
      }

      if (yAxis2 != null) {
        yAxis2.setData(
          isRotated ? canvasWidth : canvasHeight, r, 1, isStacked, isCentered, !isRotated, 0
        );
        if (yAxis2.isVisible()) {
          valueAxisSize2 = yAxis2.getNeededSize();
        }
      }

      if (isRotated) {
        canvasHeight = height - (valueAxisSize1 + valueAxisSize2);
      } else {
        canvasWidth = width - (valueAxisSize1 + valueAxisSize2);
      }

      if (xAxis != null) {
        xAxis.setData(
          isRotated ? canvasHeight : canvasWidth, r, 0, isStacked, isCentered, isRotated, 0
        );
        if (xAxis.isVisible()) {
          sampleAxisSize = xAxis.getNeededSize();
        }
      }

      if (isRotated) {
        canvasWidth = width - sampleAxisSize;
      } else {
        canvasHeight = height - sampleAxisSize;
      }
    }

    final ChartFont font = getFont();

    // use user insets?
    if (isRotated) {
      y = coalesce(getContentInsetTop(), y);
      x = coalesce(getContentInsetLeft(), x + valueAxisSize1)
        - valueAxisSize1;
    } else {
      y = coalesce(getContentInsetTop(), y);
      x = coalesce(getContentInsetLeft(), x + sampleAxisSize)
        - sampleAxisSize;
    }

    // render canvas
    if (isRotated) {
      final int canvasX = x + sampleAxisSize;
      final int canvasY = y + valueAxisSize2;
      canvas.render(
        progress, r, canvasX, canvasY, canvasWidth, canvasHeight, xAxis, null, yAxis1, yAxis2
      );
    } else {
      final int canvasX = x + valueAxisSize1;
      canvas.render(
        progress, r, canvasX, y, canvasWidth, canvasHeight, yAxis1, yAxis2, xAxis, null
      );
    }

    // paint content
    final int xx = x + (isRotated ? sampleAxisSize : valueAxisSize1);
    final int yy = y + (isRotated ? valueAxisSize2 : 0);
    setCanvasArea(xx, yy, canvasWidth, canvasHeight);
    renderContent(progress, data, r, canvasWidth, canvasHeight, font, xx, yy);

    // reset clip set by canvas
    r.resetClip();

    // render axes
    if ((yAxis1 != null) && yAxis1.isVisible()) {
      if (isRotated) {
        yAxis1.render(
          r,
          x + sampleAxisSize, y + valueAxisSize2 + canvasHeight,
          canvasWidth, valueAxisSize1,
          isCentered,
          false,
          font
        );
      } else {
        yAxis1.render(r, x, y, valueAxisSize1, canvasHeight, isCentered, true, font);
      }
    }

    if ((yAxis2 != null) && yAxis2.isVisible()) {
      if (isRotated) {
        yAxis2.render(
          r,
          x + sampleAxisSize, y,
          canvasWidth, valueAxisSize2,
          isCentered,
          true,
          font
        );
      } else {
        yAxis2.render(
          r,
          x + valueAxisSize1 + canvasWidth, y,
          valueAxisSize2, canvasHeight,
          isCentered,
          false,
          font
        );
      }
    }

    if ((xAxis != null) && xAxis.isVisible()) {
      if (isRotated) {
        xAxis.render(
          r,
          x, y + valueAxisSize2,
          sampleAxisSize, canvasHeight,
          isCentered,
          true,
          font
        );
      } else {
        xAxis.render(
          r,
          x + valueAxisSize1, y + canvasHeight,
          canvasWidth, sampleAxisSize,
          isCentered,
          false,
          font
        );
      }
    }
  }

  protected boolean needsCenteredSampleAxis() {
    for (Content content : getContents()) {
      if (content instanceof RendererWithSamples) {
        final RendererWithSamples sampleContent = (RendererWithSamples) content;
        if (sampleContent.needsCenteredSampleAxis()) {
          return true;
        }
      }
    }
    return false;
  }

  protected abstract void renderContent(
    double progress,
    Data data,
    Renderer r,
    int canvasWidth, int canvasHeight,
    ChartFont font,
    int xx, int yy)
    throws ChartException;

  protected Axis getAxis(Content content) {
    Axis axis = yAxis1;
    if (content instanceof HasValueAxis) {
      final HasValueAxis hasAxis = (HasValueAxis) content;
      if (hasAxis.getUsedValueAxis() == 1) {
        axis = yAxis2;
      }
    }
    return axis;
  }
}
