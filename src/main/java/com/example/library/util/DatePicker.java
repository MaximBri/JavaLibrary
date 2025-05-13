package com.example.library.util;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import com.example.library.ui.DateLabelFormatter;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class DatePicker {
  private final SqlDateModel model;
  private final JDatePickerImpl datePicker;

  public DatePicker() {
    model = new SqlDateModel();
    Properties p = new Properties();
    p.put("text.today", "Today");
    p.put("text.month", "Month");
    p.put("text.year", "Year");
    JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
    datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
  }

  public JComponent getComponent() {
    return datePicker;
  }

  public Calendar getSelectedDate() {
    return (Calendar) datePicker.getModel().getValue();
  }

  public void setDateFormat(String format) {
    datePicker.getJFormattedTextField().setFormatterFactory(
        new DefaultFormatterFactory(new DateLabelFormatter(format)));
  }
}