package com.instantolap.charts.impl.util;

import com.instantolap.charts.renderer.ChartColor;
import com.instantolap.charts.renderer.ChartStroke;


public class ArrayHelper {

  public static String[] add(String[] array, String category) {
    final String[] array2 = new String[array.length + 1];
    System.arraycopy(array, 0, array2, 0, array.length);
    array2[array.length] = category;
    return array2;
  }

  public static String[][] add(String[][] array, String[] category) {
    final String[][] array2 = new String[array.length + 1][];
    System.arraycopy(array, 0, array2, 0, array.length);
    array2[array.length] = category;
    return array2;
  }

  public static int[] add(int[] array, int value) {
    if (array == null) {
      return new int[]{value};
    } else {
      return add(array, array.length, value);
    }
  }

  public static int[] add(int[] array, int series, int symbol) {
    if (array == null) {
      array = new int[series + 1];
    } else if (series >= array.length) {
      final int[] array2 = new int[series + 1];
      System.arraycopy(array, 0, array2, 0, array.length);
      array = array2;
    }
    array[series] = symbol;
    return array;
  }

  public static int[] add(int[] array, int[] values) {
    if (array == null) {
      return values;
    } else {
      final int[] newArray = new int[array.length + values.length];
      System.arraycopy(array, 0, newArray, 0, array.length);
      System.arraycopy(values, 0, newArray, array.length, values.length);
      return newArray;
    }
  }

  public static Integer[] add(Integer[] array, int series, int symbol) {
    if (array == null) {
      array = new Integer[series + 1];
    } else if (series >= array.length) {
      final Integer[] array2 = new Integer[series + 1];
      System.arraycopy(array, 0, array2, 0, array.length);
      array = array2;
    }
    array[series] = symbol;
    return array;
  }

  public static ChartStroke[] add(ChartStroke[] array, int series, ChartStroke symbol) {
    if (series >= array.length) {
      final ChartStroke[] array2 = new ChartStroke[series + 1];
      System.arraycopy(array, 0, array2, 0, array.length);
      array = array2;
    }
    array[series] = symbol;
    return array;
  }

  public static ChartColor[] add(ChartColor[] array, int series, ChartColor symbol) {
    if (series >= array.length) {
      final ChartColor[] array2 = new ChartColor[series + 1];
      System.arraycopy(array, 0, array2, 0, array.length);
      array = array2;
    }
    array[series] = symbol;
    return array;
  }

  public static boolean contains(int[] a, int n) {
    for (int v : a) {
      if (v == n) {
        return true;
      }
    }
    return false;
  }
}
