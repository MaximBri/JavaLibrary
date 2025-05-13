package com.example.library.ui;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateLabelFormatter extends DefaultFormatter {
  private String datePattern;
  private SimpleDateFormat dateFormatter;

  public DateLabelFormatter() {
    this("yyyy-MM-dd");
  }

  /**
   * 
   * @param pattern строка формата, например "dd.MM.yyyy"
   */
  public DateLabelFormatter(String pattern) {
    this.datePattern = pattern;
    this.dateFormatter = new SimpleDateFormat(this.datePattern);
  }

  @Override
  public Object stringToValue(String text) throws ParseException {
    return dateFormatter.parse(text);
  }

  @Override
  public String valueToString(Object value) throws ParseException {
    if (value instanceof Calendar) {
      Calendar cal = (Calendar) value;
      return dateFormatter.format(cal.getTime());
    }
    return "";
  }
}