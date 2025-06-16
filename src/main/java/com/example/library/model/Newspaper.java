package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Newspaper extends Publication {
  private String editorName;
  private String frequency;
  private boolean isNational;
  private String language;

  public Newspaper() {
    super();
  }

  public Newspaper(String title, String publisher, LocalDate publishDate,
      String editorName, String frequency, boolean isNational, String language) {
    super(title, publisher, publishDate);
    this.editorName = editorName;
    this.frequency = frequency;
    this.isNational = isNational;
    this.language = language;
  }

  @Override
  public String getType() {
    return "Газета";
  }

  @Override
  public String getDescription() {
    return String.format("%s, редактор: %s, периодичность: %s, %s, язык: %s",
        getTitle(), editorName, frequency,
        isNational ? "национальная" : "региональная", language);
  }

  public String getEditorName() {
    return editorName;
  }

  public void setEditorName(String editorName) {
    this.editorName = editorName;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public boolean isNational() {
    return isNational;
  }

  public void setNational(boolean national) {
    isNational = national;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    Newspaper newspaper = (Newspaper) o;
    return isNational == newspaper.isNational &&
        Objects.equals(editorName, newspaper.editorName) &&
        Objects.equals(frequency, newspaper.frequency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), editorName, frequency, isNational);
  }
}
