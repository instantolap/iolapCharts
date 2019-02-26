package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.Chart;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.HasAnimation;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

import java.util.concurrent.CountDownLatch;

public class FxChartPanel extends Canvas {

  private Chart chart;
  private FxRenderer renderer;
  private boolean isAnimating;
  private boolean isRendered;

  public FxChartPanel() {
    this.renderer = new FxRenderer(this) {

      @Override
      public void animate(final HasAnimation animated, final long duration) {
        if (duration <= 0) {
          render(1, null);
          return;
        }

        new Thread(() -> {
          try {
            isAnimating = true;
            final long start = System.currentTimeMillis();
            while (true) {

              final long d = System.currentTimeMillis() - start;
              final double progress = Math.min(1, (double) d / duration);

              final CountDownLatch countDownLatch = new CountDownLatch(1);
              render(progress, countDownLatch);
              countDownLatch.await();

              if (progress >= 1) {
                break;
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            isAnimating = false;
          }
        }).start();

        setOnMouseMoved(event -> {
          try {
            renderer.mouseListeners.fireMouseMove((int) event.getX(), (int) event.getY());
            renderer.fireMouseMove((int) event.getX(), (int) event.getY());
          } catch (Exception e) {
            renderer.showError(e);
          }
        });

        setOnMouseDragged(event -> {
          renderer.mouseListeners.fireMouseMove((int) event.getX(), (int) event.getY());
        });

        setOnMouseClicked(event -> {
          renderer.fireMouseClick((int) event.getX(), (int) event.getY());
        });

        setOnMousePressed(event -> {
          renderer.mouseListeners.fireMouseDown((int) event.getX(), (int) event.getY());
        });

        setOnMouseReleased(event -> {
          renderer.mouseListeners.fireMouseUp((int) event.getX(), (int) event.getY());
        });

        setOnMouseExited(event -> {
          try {
            renderer.fireMouseOut((int) event.getX(), (int) event.getY());
          } catch (Exception e) {
            renderer.showError(e);
          }
        });

        setOnScroll(event -> {
            renderer.mouseListeners.fireMouseWheel(
              (int) event.getX(), (int) event.getY(), (int) event.getDeltaY()
            );
          }
        );
      }
    };
  }

  public void setChart(Chart chart) {
    this.chart = chart;
    this.chart.setRenderer(renderer);
    Platform.runLater(() -> {
      try {
        this.chart.render();
      } catch (ChartException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public double minHeight(double width) {
    return 10;
  }

  @Override
  public double maxHeight(double width) {
    return Double.MAX_VALUE;
  }

  @Override
  public double prefHeight(double width) {
    return minHeight(width);
  }

  @Override
  public double minWidth(double height) {
    return 10;
  }

  @Override
  public double maxWidth(double height) {
    return Double.MAX_VALUE;
  }

  @Override
  public boolean isResizable() {
    return true;
  }

  @Override
  public void resize(double width, double height) {
    super.setWidth(width);
    super.setHeight(height);
    if (!isAnimating && isRendered) {
      render(1, null);
    }
  }

  private void render(double progress, CountDownLatch latch) {
    if (chart != null) {
      Platform.runLater(() -> {
        try {
          chart.render(progress);
        } catch (ChartException e) {
          e.printStackTrace();
        } finally {
          if (latch != null) {
            latch.countDown();
          }
        }
        isRendered = true;
      });
    }
  }
}
