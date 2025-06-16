package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class EBook extends Book {
  private String format;
  private double fileSizeMB;
  private String downloadUrl;
  private boolean isDrmProtected;

  public EBook() {
    super();
  }

  public EBook(String title, String author, String isbn, String publisher, LocalDate publishDate,
      String format, double fileSizeMB, String downloadUrl, boolean isDrmProtected) {
    super(title, author, isbn, publisher, publishDate);
    this.format = format;
    this.fileSizeMB = fileSizeMB;
    this.downloadUrl = downloadUrl;
    this.isDrmProtected = isDrmProtected;
  }

  @Override
  public String getType() {
    return "Электронная книга";
  }

  @Override
  public String getDescription() {
    return String.format("%s, автор: %s, формат: %s, размер: %.2f МБ, %s",
        getTitle(), getAuthor(), format, fileSizeMB,
        isDrmProtected ? "защищена DRM" : "без DRM защиты");
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public double getFileSizeMB() {
    return fileSizeMB;
  }

  public void setFileSizeMB(double fileSizeMB) {
    this.fileSizeMB = fileSizeMB;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public boolean isDrmProtected() {
    return isDrmProtected;
  }

  public void setDrmProtected(boolean drmProtected) {
    isDrmProtected = drmProtected;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    EBook eBook = (EBook) o;
    return Double.compare(eBook.fileSizeMB, fileSizeMB) == 0 &&
        Objects.equals(format, eBook.format) &&
        Objects.equals(downloadUrl, eBook.downloadUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), format, fileSizeMB, downloadUrl);
  }
}
