package com.instantolap.charts.renderer.util;

import java.util.ArrayList;
import java.util.List;


public class StringHelper {

  public static String[] splitString(String in) {
    return splitString(in, ",");
  }

  public static String[] splitString(String in, String delimiter) {
    if (in == null) {
      return null;
    } else if (delimiter == null) {
      return null;
    }

    if (in.length() == 0) {
      return new String[0];
    }

    final int dl = delimiter.length();

    final List<String> result = new ArrayList<>();
    int pos1 = 0;
    int pos2 = in.indexOf(delimiter);
    while (pos2 != -1) {
      result.add(in.substring(pos1, pos2));
      pos1 = pos2 + dl;
      pos2 = in.indexOf(delimiter, pos1);
    }
    result.add(in.substring(pos1));
    return result.toArray(new String[0]);
  }

  public static String trim(String text) {
    if (text == null) {
      return null;
    }

    for (int n = 0; n < text.length(); n++) {
      if (text.charAt(n) != ' ') {
        return text.substring(n).trim();
      }
    }
    return "";
  }
}
