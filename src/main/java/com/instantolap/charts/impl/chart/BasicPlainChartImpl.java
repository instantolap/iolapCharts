package com.instantolap.charts.impl.chart;

import com.instantolap.charts.PlainChart;
import com.instantolap.charts.impl.data.Palette;


public abstract class BasicPlainChartImpl extends BasicChartImpl implements PlainChart {
    public BasicPlainChartImpl(Palette palette) {
        super(palette);
    }
}
