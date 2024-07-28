package com.kareem.project;

import java.util.List;

public class SearchResult {
  private Integer count;
  private String next;
  private String previous;
  private List<Book> results;

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public String getPrevious() {
    return previous;
  }

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  public List<Book> getResults() {
    return results;
  }

  public Book getFirstResult() {
    return results.get(0);
  }

  public void setResults(List<Book> results) {
    this.results = results;
  }
}
