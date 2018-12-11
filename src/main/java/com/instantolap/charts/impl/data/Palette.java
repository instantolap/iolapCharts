package com.instantolap.charts.impl.data;

import com.instantolap.charts.renderer.ChartColor;


public class Palette {

  private static final String[] palette1 = new String[]{
    "1874CD", "CD0000", "2E8B57", "ECCA06", "FF7F00", "838B83", "5D478B", "CD1076", "B22222",
    "8B6508", "8B4513", "C0C0C0", "696969", "080808"
  };

  private static final String[] palette2 = new String[]{
    "fff8a3", "fae16b", "f8d753", "f3c01c", "f0b400", "a9cc8f", "82b16a", "5c9746", "3d8128",
    "1e6c0b", "b2c8d9", "779dbf", "3e75a7", "205f9a", "00488c", "bea37a", "907a52", "7a653e",
    "63522b", "332600", "f3aa79", "eb8953", "e1662a", "dc5313", "d84000", "b5b5a9", "8a8d82",
    "74796f", "5d645a", "434c43", "e6a5a4", "d6707b", "c4384f", "bc1c39", "b30023"
  };

  private static final int[] scheme1a = new int[]{
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
  };

  private static final int[] scheme2a = new int[]{
    0, 5, 10, 15, 20, 25, 30, 2, 7, 12, 17, 22, 27, 32, 4, 9, 14, 19, 24, 29, 34, 1, 6, 11, 16, 21,
    26, 31, 3, 8, 13, 18, 23, 28, 33
  };

  private static final int[] scheme2b = new int[]{
    4, 9, 14, 19, 24, 29, 34, 2, 7, 12, 17, 22, 27, 32, 0, 5, 10, 15, 20, 25, 30, 3, 8, 13, 18, 23,
    28, 33, 1, 6, 11, 16, 21, 26, 31
  };

  public static ChartColor[] getColors() {
    return getColors(palette2, scheme2b);
  }

  private static ChartColor[] getColors(String[] palette, int[] s) {
    final ChartColor[] sampleColors = new ChartColor[s.length];
    for (int n = 0; n < s.length; n++) {
      sampleColors[n] = new ChartColor(palette[s[n]], false);
    }
    return sampleColors;
  }

}
