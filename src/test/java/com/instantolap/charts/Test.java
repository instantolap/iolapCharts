package com.instantolap.charts;

import com.google.gwt.core.shared.GWT;
import com.instantolap.charts.factories.JSONChartFactory;
import com.instantolap.charts.factories.JSONDataFactory;
import com.instantolap.charts.json.JSONObject;
import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.Renderer;
import com.instantolap.charts.renderer.impl.SwingRenderer;

import java.io.File;
import java.io.FileReader;


public class Test {

  public static void main(String[] args) {

    try {
      Data data = null;
      if (true) {
        final File f = new File("f");
        System.out.println(f.getAbsoluteFile());
        final File dataFile = new File("src/test/resource/data1.json");
        System.out.println(dataFile.getAbsolutePath());
        final FileReader reader = new FileReader(dataFile);
        final char[] buffer = new char[(int) dataFile.length()];
        reader.read(buffer);
        reader.close();
        final String jsonText = new String(buffer);
        final JSONObject jsonData = new JSONObject(jsonText);
        data = JSONDataFactory.parseData(jsonData);
      }

      final File configFile = new File("src/test/resource/bar1.json");
      final FileReader reader = new FileReader(configFile);
      final char[] buffer = new char[(int) configFile.length()];
      reader.read(buffer);
      reader.close();
      final String configText = new String(buffer);
      final JSONObject jsonConfig = new JSONObject(configText);

      final Renderer renderer = new SwingRenderer();
      renderer.setSize(500, 500);
      final Chart chart = JSONChartFactory.create(jsonConfig, data);
      chart.setRenderer(renderer);

      final SampleChart sampleChart = (SampleChart) chart;
      sampleChart.getCanvas().setVerticalGrid(ChartColor.BLACK);
      sampleChart.getCanvas().setHorizontalGrid(ChartColor.BLACK);
      chart.render();

    }
    catch (Exception e) {
      GWT.log("Unable to display chart", e);
      e.printStackTrace();
    }
  }
}
