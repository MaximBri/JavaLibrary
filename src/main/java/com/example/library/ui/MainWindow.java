package com.example.library.ui;

import com.example.library.service.BookService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
  private final BookService bookService;
  private final BookPanel bookPanel;

  public MainWindow() {
    super("Library Management");
    this.bookService = new BookService();
    this.bookPanel = new BookPanel(bookService);

    // Настройка окна
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        onExit();
      }
    });
    setLayout(new BorderLayout());
    add(createToolbar(), BorderLayout.NORTH);
    add(bookPanel, BorderLayout.CENTER);
    setSize(800, 600);
    setLocationRelativeTo(null);
  }

  private JToolBar createToolbar() {
    JToolBar toolbar = new JToolBar();
    JButton addBookBtn = new JButton("Add Book");
    addBookBtn.addActionListener(e -> bookPanel.showAddBookDialog());

    JButton reserveBtn = new JButton("Reserve Book");
    reserveBtn.addActionListener(e -> bookPanel.showReservationDialog());

    toolbar.add(addBookBtn);
    toolbar.add(reserveBtn);
    return toolbar;
  }

  private void onExit() {
    int option = JOptionPane.showConfirmDialog(
        this,
        "Do you really want to exit?",
        "Exit Confirmation",
        JOptionPane.YES_NO_OPTION);
    if (option == JOptionPane.YES_OPTION) {
      dispose();
      System.exit(0);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainWindow window = new MainWindow();
      window.setVisible(true);
    });
  }
}