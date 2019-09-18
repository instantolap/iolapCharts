package com.instantolap.charts.renderer.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

public abstract class BasicDesktopRenderer extends BasicRenderer {

  private Locale locale = Locale.getDefault();
  private TimeZone timezone = TimeZone.getDefault();
  private final Map<String, DecimalFormat> decimalFormats = new HashMap<>();
  private final Map<String, SimpleDateFormat> dateFormats = new HashMap<>();

  @Override
  public void setLocale(String locale) {
    this.locale = Locale.forLanguageTag(locale);
  }

  @Override
  public void setTimezoneId(String timezoneId) {
    this.timezone = TimeZone.getTimeZone(ZoneId.of(timezoneId));
  }

  @Override
  public String format(String pattern, double v) {
    if (pattern == null) {
      return null;
    }
    DecimalFormat format = decimalFormats.get(pattern);
    if (format == null) {
      format = new DecimalFormat(pattern);
      decimalFormats.put(pattern, format);
    }
    return format.format(v);
  }

  @Override
  public String format(String pattern, Date v) {
    if (pattern == null) {
      return null;
    }

    SimpleDateFormat dateFormat = dateFormats.get(pattern);
    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat(pattern, locale);
      dateFormat.setTimeZone(timezone);
      dateFormats.put(pattern, dateFormat);
    }
    return dateFormat.format(v);
  }
}
