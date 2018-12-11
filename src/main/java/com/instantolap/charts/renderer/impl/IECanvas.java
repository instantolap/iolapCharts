package com.instantolap.charts.renderer.impl;

import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.user.client.ui.FocusWidget;


@PartialSupport
public class IECanvas extends FocusWidget {

  private IECanvas(CanvasElement element) {
    setElement(element);
  }

  public static IECanvas createIfSupported() {
    if (!isFlashCanvasPluginInstalled()) {
      return null;
    }
    final CanvasElement element = createFlashCanvasElement();
    return new IECanvas(element);
  }

  private static native boolean isFlashCanvasPluginInstalled() /*-{
    return (typeof $wnd.FlashCanvas != 'undefined');
  }-*/;

  private static native CanvasElement createFlashCanvasElement() /*-{

    var canvas = $doc.createElement("canvas");
    $wnd.FlashCanvas.initElement(canvas);
    return canvas;

  }-*/;

  public Context getContext(String contextId) {
    return getCanvasElement().getContext(contextId);
  }

  public CanvasElement getCanvasElement() {
    return this.getElement().cast();
  }

  public Context2d getContext2d() {
    return getCanvasElement().getContext2d();
  }

  public int getCoordinateSpaceHeight() {
    return getCanvasElement().getHeight();
  }

  public void setCoordinateSpaceHeight(int height) {
    getCanvasElement().setHeight(height);
  }

  public int getCoordinateSpaceWidth() {
    return getCanvasElement().getWidth();
  }

  public void setCoordinateSpaceWidth(int width) {
    getCanvasElement().setWidth(width);
  }

  public String toDataUrl() {
    return getCanvasElement().toDataUrl();
  }

  public String toDataUrl(String type) {
    return getCanvasElement().toDataUrl(type);
  }
}
