package com.instantolap.charts.control;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;


public class ControlPanel {

  private final ChartColor backgroundColor = ChartColor.BLACK;
  private final List<ControlField> fields = new ArrayList<>();
  private Renderer renderer;

  public void setRenderer(Renderer renderer) {
    this.renderer = renderer;
  }

  public void add(ControlField field) {
    fields.add(field);
  }

  public void render() {
    renderer.setColor(backgroundColor);
    renderer.fillRect(0, 0, renderer.getWidth(), renderer.getHeight());

    int x = 0;
    final int y = 0;
    for (ControlField field : fields) {
      field.render(renderer, x, y);

      x += field.getWidth();
    }

    renderer.finish();
  }
}
