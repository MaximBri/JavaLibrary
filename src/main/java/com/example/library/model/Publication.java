package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Publication {
  private Long id;
  private String title;
  private boolean reserved;
  private LocalDate publishDate;
  private String publisher;

  public abstract String getType();

  public abstract String getDescription();

  public Publication() {
  }

  public Publication(String title, String publisher, LocalDate publishDate) {
    this.title = title;
    this.publisher = publisher;
    this.publishDate = publishDate;
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

  public boolean isReserved() {
    return reserved;
  }

  public void setReserved(boolean reserved) {
    this.reserved = reserved;
  }

  public LocalDate getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(LocalDate publishDate) {
    this.publishDate = publishDate;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Publication that = (Publication) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(title, that.title) &&
        Objects.equals(publisher, that.publisher);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, publisher);
  }

  @Override
  public String toString() {
    return String.format("%s[id=%d, title='%s', publisher='%s', publishDate=%s]",
        getType(), id, title, publisher, publishDate);
  }
}
