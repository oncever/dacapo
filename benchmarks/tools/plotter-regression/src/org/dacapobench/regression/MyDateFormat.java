package org.dacapobench.regression;

import java.text.DateFormatSymbols;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;


public class MyDateFormat extends SimpleDateFormat {
  public MyDateFormat() {
    super();
  }
  public MyDateFormat(String pattern) {
    super(pattern);
  }
  public MyDateFormat(String pattern, DateFormatSymbols formatSymbols) {
    super(pattern,formatSymbols);
  }
  public MyDateFormat(String pattern, Locale locale) {
    super(pattern,locale);
  }
  public Date parse(String text, ParsePosition pos) {
    return new MyDate(super.parse(text,pos).getTime());
  }
  private static final long serialVersionUID = 0;
}

