package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Magazine extends Publication {
  private String issn;
  private int issueNumber;
  private String category;
  private boolean isMonthly;

  public Magazine() {
    super();
  }

  public Magazine(String title, String publisher, LocalDate publishDate,
      String issn, int issueNumber, String category, boolean isMonthly) {
    super(title, publisher, publishDate);
    this.issn = issn;
    this.issueNumber = issueNumber;
    this.category = category;
    this.isMonthly = isMonthly;
  }

  @Override
  public String getType() {
    return "Журнал";
  }

  @Override
  public String getDescription() {
    return String.format("%s, выпуск №%d, ISSN: %s, категория: %s, %s",
        getTitle(), issueNumber, issn, category,
        isMonthly ? "ежемесячный" : "периодический");
  }

  public String getIssn() {
    return issn;
  }

  public void setIssn(String issn) {
    this.issn = issn;
  }

  public int getIssueNumber() {
    return issueNumber;
  }

  public void setIssueNumber(int issueNumber) {
    this.issueNumber = issueNumber;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public boolean isMonthly() {
    return isMonthly;
  }

  public void setMonthly(boolean monthly) {
    isMonthly = monthly;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    Magazine magazine = (Magazine) o;
    return issueNumber == magazine.issueNumber &&
        Objects.equals(issn, magazine.issn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), issn, issueNumber);
  }
}
