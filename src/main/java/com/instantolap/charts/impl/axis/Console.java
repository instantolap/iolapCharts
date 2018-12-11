package com.instantolap.charts.impl.axis;

public class Console {

  public static native void log(String text)
    /*-{
      console.log(text);
    }-*/;

}
