package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.HasAnimation;
import com.instantolap.charts.renderer.RendererContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ChartPanel extends JPanel {

  private HasAnimation animated;
  private HeadlessRenderer renderer = new HeadlessRenderer() {

    private void setImageSize(double width, double height) {
      super.setSize(width, height);

      final JLabel label = new JLabel(new ImageIcon(getImage()));
      ChartPanel.this.removeAll();
      ChartPanel.this.add(label, BorderLayout.CENTER);
    }

    @Override
    public void setSize(double width, double height) {
      setImageSize(width, height);
    }

    @Override
    public void animate(final HasAnimation animated, final long duration)
      throws ChartException {
      ChartPanel.this.animated = animated;
      if (duration <= 0) {
        animated.render(1);
        return;
      }

      try {
        final long start = System.currentTimeMillis();
        while (true) {

          final long d = System.currentTimeMillis() - start;
          final double progress = Math.min(1, (double) d / duration);
          SwingUtilities.invokeAndWait(() -> {
            try {
              animated.render(progress);
            } catch (Exception e) {
              showError(e);
            }
          });

          if (progress >= 1) {
            break;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Override
    public void showNormalPointer() {
      ChartPanel.this.setCursor(
        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
      );
    }

    @Override
    public void showClickPointer() {
      ChartPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void finish() {
      super.finish();
      ChartPanel.this.updateUI();
    }
  };

  public ChartPanel() {
    super(new BorderLayout());

    addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent event) {
        renderer.mouseListeners.fireMouseMove(event.getX(), event.getY());
      }

      @Override
      public void mouseMoved(MouseEvent event) {
        try {
          renderer.mouseListeners.fireMouseMove(event.getX(), event.getY());
          renderer.fireMouseMove(event.getX(), event.getY());
        } catch (Exception e) {
          renderer.showError(e);
        }
      }
    });

    addMouseListener(new java.awt.event.MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        renderer.fireMouseClick(e.getX(), e.getY());
      }

      @Override
      public void mousePressed(MouseEvent event) {
        renderer.mouseListeners.fireMouseDown(event.getX(), event.getY());
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        renderer.mouseListeners.fireMouseUp(event.getX(), event.getY());
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent event) {
        try {
          renderer.fireMouseOut(event.getX(), event.getY());
        } catch (Exception e) {
          renderer.showError(e);
        }
      }
    });

    addMouseWheelListener(e -> renderer.mouseListeners.fireMouseWheel(
      e.getX(), e.getY(), e.getWheelRotation()
    ));
  }

  public void setChart(RendererContent chart) {
    try {
      renderer.setSize(getWidth(), getHeight());
      chart.setRenderer(renderer);
      chart.render();
    } catch (ChartException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setSize(int width, int height) {
    super.setSize(width, height);

    renderer.setSize(width, height);

    if (animated != null) {
      try {
        animated.render(1);
      } catch (ChartException e) {
        e.printStackTrace();
      }
    }
  }
}
