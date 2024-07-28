package com.kareem.project;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class Book {

  @Id
  private String id;
  private String title;
  private List<String> subjects;
  private List<Person> authors;
  private List<Person> translators;
  private List<String> bookshelves;
  private List<String> languages;
  private Boolean copyright;
  private String media_type;
  // private Format formats;
  private Map<String, String> formats;
  private Integer download_count;

  public Book() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getSubjects() {
    return subjects;
  }

  public void setSubjects(List<String> subjects) {
    this.subjects = subjects;
  }

  public List<Person> getAuthors() {
    return authors;
  }

  public void setAuthors(List<Person> authors) {
    this.authors = authors;
  }

  public List<Person> getTranslators() {
    return translators;
  }

  public void setTranslators(List<Person> translators) {
    this.translators = translators;
  }

  public List<String> getBookshelves() {
    return bookshelves;
  }

  public void setBookshelves(List<String> bookshelves) {
    this.bookshelves = bookshelves;
  }

  public List<String> getLanguages() {
    return languages;
  }

  public void setLanguages(List<String> languages) {
    this.languages = languages;
  }

  public Boolean getCopyright() {
    return copyright;
  }

  public void setCopyright(Boolean copyright) {
    this.copyright = copyright;
  }

  public String getMedia_type() {
    return media_type;
  }

  public void setMedia_type(String media_type) {
    this.media_type = media_type;
  }

  public Map<String, String> getFormats() {
    return formats;
  }

  public void setFormats(Map<String, String> formats) {
    this.formats = formats;
  }

  public Integer getDownload_count() {
    return download_count;
  }

  public void setDownload_count(Integer download_count) {
    this.download_count = download_count;
  }

}

// "id": <number of Project Gutenberg ID>,
// "title": <string>,
// "subjects": <array of strings>,
// "authors": <array of Persons>,
// "translators": <array of Persons>,
// "bookshelves": <array of strings>,
// "languages": <array of strings>,
// "copyright": <boolean or null>,
// "media_type": <string>,
// "formats": <Format>,
// "download_count": <number>