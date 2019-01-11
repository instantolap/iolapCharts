package com.instantolap.charts.impl.chart;

import com.instantolap.charts.PlainChart;
import com.instantolap.charts.impl.data.Theme;


public abstract class BasicPlainChartImpl extends BasicChartImpl implements PlainChart {
    public BasicPlainChartImpl(Theme theme) {
        super(theme);
    }
}
