package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.Chart;
import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.HasAnimation;
import javafx.scene.canvas.Canvas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javafx.application.Platform.runLater;

public class FxChartPanel extends Canvas {

  private static final String DEBOUNCE_THREAD_NAME = "iolap-javafx";
  private static final int DEFAULT_DEBOUNCE_TIME = 75;
  private static final ScheduledExecutorService DEBOUNCE_EXECUTOR = newScheduledThreadPool(1, r -> {
    final Thread thread = new Thread(r, DEBOUNCE_THREAD_NAME);
    thread.setDaemon(true);
    thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
    return thread;
  });

  public static void dispose() {
    DEBOUNCE_EXECUTOR.shutdownNow();
  }

  private ScheduledFuture<?> debounce;
  private int debounceTime = DEFAULT_DEBOUNCE_TIME;

  private final FxRenderer renderer;
  private Chart chart;
  private boolean isAnimating;
  private boolean isRendered;

  public FxChartPanel(boolean interactive) {
    renderer = new FxRenderer(this) {
      @Override
      public void animate(final HasAnimation animated, final long duration) {
        if (duration <= 0) {
          render(1, null);
          return;
        }

        DEBOUNCE_EXECUTOR.submit(() -> {
          try {
            isAnimating = true;
            final long start = System.currentTimeMillis();

            while (true) {
              final double diff = System.currentTimeMillis() - start;
              final double progress = Math.min(1, diff / duration);

              final CountDownLatch latch = new CountDownLatch(1);
              render(progress, latch);
              latch.await(10, TimeUnit.SECONDS);

              if (progress >= 1) break;
            }

          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            isAnimating = false;
          }
        });
      }
    };

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

    if (interactive) {
      setOnScroll(event -> {
        renderer.mouseListeners.fireMouseWheel(
          (int) event.getX(), (int) event.getY(), (int) event.getDeltaY()
        );
      });
    }
  }

  public Chart getChart() {
    return chart;
  }

  public void setChart(Chart chart) {
    this.chart = chart;
    this.chart.setRenderer(renderer);
    try {
      this.chart.render();
    } catch (ChartException e) {
      e.printStackTrace();
    }
  }

  public int getDebounceTime() {
    return debounceTime;
  }

  public void setDebounceTime(int debounceTime) {
    this.debounceTime = debounceTime;
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
    setWidth(width);
    setHeight(height);
    if (!isAnimating && isRendered) {
      if (chart.isAnimationEnabled()) {
        render(1, null);
      } else {
        // Debounce rendering until resizing stops
        if (debounce != null) debounce.cancel(true);
        debounce = DEBOUNCE_EXECUTOR.schedule(() -> render(1, null), debounceTime, MILLISECONDS);
      }
    }
  }

  private void render(double progress, CountDownLatch latch) {
    if (chart != null) {
      runLater(() -> {
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
