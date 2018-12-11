package com.instantolap.charts;

public interface MouseListener {
  void onMouseIn(int x, int y);

  void onMouseOut(int x, int y);

  void onClick(int x, int y);
}
