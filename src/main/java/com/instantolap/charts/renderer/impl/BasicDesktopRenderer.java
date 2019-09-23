package com.instantolap.charts.renderer.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;


public abstract class BasicDesktopRenderer extends BasicRenderer {

  private Locale locale = Locale.getDefault();
  private TimeZone timezone = TimeZone.getDefault();
  private final Map<String, DecimalFormat> decimalFormats = new ConcurrentHashMap<>();
  private final Map<String, SimpleDateFormat> dateFormats = new ConcurrentHashMap<>();

  @Override
  public void setLocale(String locale) {
    this.locale = Locale.forLanguageTag(locale);
    decimalFormats.clear();
  }

  @Override
  public void setTimezoneId(String timezoneId) {
    this.timezone = TimeZone.getTimeZone(ZoneId.of(timezoneId));
    dateFormats.clear();
  }

  @Override
  public String format(String pattern, double value) {
    if (pattern == null) {
      return null;
    }
    DecimalFormat format = decimalFormats.computeIfAbsent(pattern, p -> new DecimalFormat(p, DecimalFormatSymbols.getInstance(locale)));
    return format.format(value);
  }

  @Override
  public String format(String pattern, Date v) {
    if (pattern == null) {
      return null;
    }

    SimpleDateFormat dateFormat = dateFormats.computeIfAbsent(pattern, p -> {
      SimpleDateFormat formatter = new SimpleDateFormat(p, locale);
      formatter.setTimeZone(timezone);
      return formatter;
    });
    return dateFormat.format(v);
  }
}
