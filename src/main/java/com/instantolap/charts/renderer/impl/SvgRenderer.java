package com.instantolap.charts.renderer.impl;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;

import static org.apache.batik.util.SVGConstants.SVG_NAMESPACE_URI;

public class SvgRenderer extends BasicGraphics2dRenderer {

  private SVGGraphics2D svgGraphics2D;


  @Override
  public void setSize(double width, double height) {
    final DOMImplementation theDomImpl = GenericDOMImplementation.getDOMImplementation();
    final Document theDocument = theDomImpl.createDocument(SVG_NAMESPACE_URI, "svg", null);
    svgGraphics2D = new SVGGraphics2D(theDocument);
    svgGraphics2D.setSVGCanvasSize(new Dimension((int) width, (int) height));
    setGraphics(svgGraphics2D);
  }

  @Override
  public double getWidth() {
    return svgGraphics2D.getSVGCanvasSize().width;
  }


  @Override
  public double getHeight() {
    return svgGraphics2D.getSVGCanvasSize().height;
  }

  public String getSVG() {
    try (StringWriter writer = new StringWriter()) {
      svgGraphics2D.stream(writer);
      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}