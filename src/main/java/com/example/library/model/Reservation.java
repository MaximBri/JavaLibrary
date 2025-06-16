package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Reservation {
  private Long id;
  private Long bookId;
  private String customerName;
  private LocalDate dueDate;

  public Reservation(Long id, Long bookId, String customerName, LocalDate dueDate) {
    this.id = id;
    this.bookId = bookId;
    this.customerName = customerName;
    this.dueDate = dueDate;
  }

  public Reservation() {
  }

  public Reservation(Long bookId, String customerName, LocalDate dueDate) {
    this.bookId = bookId;
    this.customerName = customerName;
    this.dueDate = dueDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Reservation that = (Reservation) o;
    return Objects.equals(bookId, that.bookId) &&
        Objects.equals(customerName, that.customerName) &&
        Objects.equals(dueDate, that.dueDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookId, customerName, dueDate);
  }

  @Override
  public String toString() {
    return String.format("Reservation[id=%d, bookId=%d, customerName='%s', dueDate=%s]",
        id, bookId, customerName, dueDate);
  }
}