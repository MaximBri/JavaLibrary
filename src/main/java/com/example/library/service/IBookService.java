package com.example.library.service;

import com.example.library.model.Book;
import java.util.List;

public interface IBookService {
  void addBook(String title, String author, String isbn);

  List<Book> getAllBooks();

  boolean reserveBook(Long bookId, String customerName, java.time.LocalDate dueDate);

  void deleteBook(Long bookId);
}