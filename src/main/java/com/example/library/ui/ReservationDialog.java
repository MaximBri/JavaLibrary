package com.example.library.ui;

import com.example.library.service.BookService;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class ReservationDialog extends JDialog {
  private final BookService bookService;
  private final Long bookId;
  private boolean reserved = false;

  public ReservationDialog(Window owner, BookService bookService, Long bookId) {
    super(owner, "Reserve Book", ModalityType.APPLICATION_MODAL);
    this.bookService = bookService;
    this.bookId = bookId;
    setLayout(new GridLayout(4, 2, 10, 10));

    JTextField nameField = new JTextField();
    SqlDateModel dateModel = new SqlDateModel();
    Properties p = new Properties();
    p.put("text.today", "Today");
    JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

    add(new JLabel("Customer Name:"));
    add(nameField);
    add(new JLabel("Due Date:"));
    add(datePicker);

    JButton okBtn = new JButton("Reserve");
    okBtn.addActionListener(e -> {
      if (bookService.reserveBook(bookId, nameField.getText(), dateModel.getValue().toLocalDate())) {
        reserved = true;
        dispose();
      } else {
        JOptionPane.showMessageDialog(this, "Reservation failed.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(e -> dispose());

    add(okBtn);
    add(cancelBtn);

    pack();
    setLocationRelativeTo(owner);
  }

  public boolean isReserved() {
    return reserved;
  }
}