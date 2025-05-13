package com.example.library.ui;

import com.example.library.model.Book;
import com.example.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
  private final BookService bookService;
  private final JTable table;
  private final DefaultTableModel model;

  public BookPanel(BookService bookService) {
    this.bookService = bookService;
    setLayout(new BorderLayout());

    // Таблица книг
    model = new DefaultTableModel(new Object[] { "ID", "Title", "Author", "ISBN", "Status" }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    table = new JTable(model);
    refreshTable();

    add(new JScrollPane(table), BorderLayout.CENTER);
  }

  public void refreshTable() {
    model.setRowCount(0);
    List<Book> books = bookService.getAllBooks();
    for (Book book : books) {
      model.addRow(new Object[] {
          book.getId(),
          book.getTitle(),
          book.getAuthor(),
          book.getIsbn(),
          book.isReserved() ? "Reserved" : "Available"
      });
    }
  }

  public void showAddBookDialog() {
    JTextField titleField = new JTextField();
    JTextField authorField = new JTextField();
    JTextField isbnField = new JTextField();
    Object[] message = {
        "Title:", titleField,
        "Author:", authorField,
        "ISBN:", isbnField
    };
    int option = JOptionPane.showConfirmDialog(
        this, message, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      bookService.addBook(titleField.getText(), authorField.getText(), isbnField.getText());
      refreshTable();
    }
  }

  public void showReservationDialog() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Please select a book to reserve.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Long bookId = (Long) model.getValueAt(selectedRow, 0);
    ReservationDialog dialog = new ReservationDialog(SwingUtilities.getWindowAncestor(this), bookService, bookId);
    dialog.setVisible(true);
    if (dialog.isReserved()) {
      refreshTable();
    }
  }
}