package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Book extends Publication {
  private String author;
  private String isbn;
  private int pageCount;
  private String genre;

  public Book() {
    super();
  }

  public Book(String title, String author, String isbn) {
    super(title, null, null);
    this.author = author;
    this.isbn = isbn;
  }

  public Book(String title, String author, String isbn, String publisher, LocalDate publishDate) {
    super(title, publisher, publishDate);
    this.author = author;
    this.isbn = isbn;
  }

  public Book(String title, String author, String isbn, String publisher, LocalDate publishDate,
      int pageCount, String genre) {
    super(title, publisher, publishDate);
    this.author = author;
    this.isbn = isbn;
    this.pageCount = pageCount;
    this.genre = genre;
  }

  @Override
  public String getType() {
    return "Книга";
  }

  @Override
  public String getDescription() {
    return String.format("%s, автор: %s, ISBN: %s, страниц: %d, жанр: %s",
        getTitle(), author, isbn, pageCount, genre);
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

  public int getPageCount() {
    return pageCount;
  }

  public void setPageCount(int pageCount) {
    this.pageCount = pageCount;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    Book book = (Book) o;
    return Objects.equals(author, book.author) && Objects.equals(isbn, book.isbn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), author, isbn);
  }
}
