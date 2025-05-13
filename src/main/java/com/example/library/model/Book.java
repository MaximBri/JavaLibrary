package com.example.library.model;

import java.util.Objects;

public class Book {
  private Long id;
  private String title;
  private String author;
  private String isbn;
  private boolean reserved;

  public Book() {
  }

  public Book(String title, String author, String isbn) {
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.reserved = false;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public boolean isReserved() {
    return reserved;
  }

  public void setReserved(boolean reserved) {
    this.reserved = reserved;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Book book = (Book) o;
    return Objects.equals(isbn, book.isbn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isbn);
  }

  @Override
  public String toString() {
    return String.format("Book[id=%d, title='%s', author='%s', isbn='%s', reserved=%b]",
        id, title, author, isbn, reserved);
  }
}