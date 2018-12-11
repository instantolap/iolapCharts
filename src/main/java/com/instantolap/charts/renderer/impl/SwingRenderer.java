package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.renderer.ChartException;
import com.instantolap.charts.renderer.HasAnimation;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public class SwingRenderer extends HeadlessRenderer {

  private final JPanel panel;
  private final JFrame frame;
  private HasAnimation animated;

  public SwingRenderer() {
    frame = new JFrame();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    panel = new JPanel(new BorderLayout());
    frame.setContentPane(panel);

    frame.addComponentListener(new ComponentListener() {

      @Override
      public void componentResized(ComponentEvent event) {
        try {
          if (animated != null) {
            setImageSize(frame.getWidth() - 20,
              frame.getHeight() - 43
            );
            animated.render(1);
          }
        }
        catch (Exception e) {
          showError(e);
        }
      }

      @Override
      public void componentMoved(ComponentEvent arg0) {
      }

      @Override
      public void componentShown(ComponentEvent arg0) {
      }

      @Override
      public void componentHidden(ComponentEvent arg0) {
      }
    });

    panel.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent event) {
        mouseListeners.fireMouseMove(event.getX(), event.getY());
      }

      @Override
      public void mouseMoved(MouseEvent event) {
        try {
          mouseListeners.fireMouseMove(event.getX(), event.getY());
          fireMouseMove(event.getX(), event.getY());
        }
        catch (Exception e) {
          showError(e);
        }
      }
    });

    panel.addMouseListener(new java.awt.event.MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
        fireMouseClick(e.getX(), e.getY());
      }

      @Override
      public void mousePressed(MouseEvent event) {
        mouseListeners.fireMouseDown(event.getX(), event.getY());
      }

      @Override
      public void mouseReleased(MouseEvent event) {
        mouseListeners.fireMouseUp(event.getX(), event.getY());
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent event) {
        try {
          fireMouseOut(event.getX(), event.getY());
        }
        catch (Exception e) {
          showError(e);
        }
      }
    });

    panel.addMouseWheelListener(e -> mouseListeners.fireMouseWheel(e.getX(), e.getY(),
      e.getWheelRotation()
    ));
  }

  private void setImageSize(int width, int height) {
    super.setSize(width, height);

    final JLabel label = new JLabel(new ImageIcon(getImage()));
    panel.removeAll();
    panel.add(label, BorderLayout.CENTER);
  }

  @Override
  public void setSize(int width, int height) {
    frame.setSize(width + 20, height + 43);
    setImageSize(width, height);
  }

  @Override
  public void animate(final HasAnimation animated, final long duration)
    throws ChartException
  {
    this.animated = animated;
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
          }
          catch (Exception e) {
            showError(e);
          }
        });

        if (progress >= 1) {
          break;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void showNormalPointer() {
    panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  @Override
  public void showClickPointer() {
    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void finish() {
    super.finish();
    panel.updateUI();
  }
}
