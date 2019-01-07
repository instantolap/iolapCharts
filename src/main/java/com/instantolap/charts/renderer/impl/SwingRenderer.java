package com.instantolap.charts.renderer.impl;

import com.instantolap.charts.renderer.RendererContent;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class SwingRenderer {

  private final ChartPanel panel;
  private final JFrame frame;

  public SwingRenderer() {
    frame = new JFrame();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    panel = new ChartPanel();
    frame.setContentPane(panel);

    frame.addComponentListener(new ComponentListener() {

      @Override
      public void componentResized(ComponentEvent event) {
        panel.setSize(
          frame.getWidth() - 20,
          frame.getHeight() - 43
        );
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
  }

  public void setSize(int w, int h) {
    panel.setSize(w, h);
  }

  public void setChart(RendererContent chart) {
    chart.setPopup(false);
    panel.setChart(chart);
  }
}
