package com.kareem.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/search")
public class SearchController {

  @Autowired
  private SearchService searchService;

  @GetMapping("/{title}")
  public ResponseEntity<List<Book>> searchBooks(@PathVariable String title) throws Exception {
    System.out.println("/title: " + title);
    return new ResponseEntity<List<Book>>(searchService.searchBooks(title), HttpStatus.OK);
  }

  @GetMapping("/details-{id}")
  public ResponseEntity<Book> bookDetails(@PathVariable String id) throws Exception {
    System.out.println("/details-id: " + id);
    return new ResponseEntity<Book>(searchService.searchBookById(id), HttpStatus.OK);
  }

  @GetMapping("/multi-details-{ids}")
  public ResponseEntity<List<Book>> getBooksByIds(@PathVariable String ids) throws Exception {
    System.out.println("/multi-details-id: " + ids);
    return new ResponseEntity<List<Book>>(searchService.searchBooksById(ids), HttpStatus.OK);
  }

  @PostMapping("/add-book")
  public ResponseEntity<Book> saveBook(@RequestBody Book book) throws Exception {
    System.out.println("/add-book: " + book.getId());
    return ResponseEntity.ok(searchService.addBook(book));
  }

  @DeleteMapping("/delete-book-{id}")
  public ResponseEntity<String> deleteBook(@PathVariable String id) throws Exception {
    System.out.println("/delete-book: " + id);
    searchService.deleteBook(id);
    return ResponseEntity.ok("Deleted book with id " + id);
  }

  @GetMapping("/inMyBooks-{id}")
  public ResponseEntity<Boolean> idInMyBooks(@PathVariable String id) {
    System.out.println("/inMyBooks: " + id);
    return ResponseEntity.ok(searchService.idInMyBookIds(id));
  }

  @GetMapping("/my-books")
  public ResponseEntity<List<Book>> getMyBooks() {
    System.out.println("/my-books");
    return new ResponseEntity<List<Book>>(searchService.getMyBooks(), HttpStatus.OK);
  }

  @PutMapping("/add-doc-{id}")
  public ResponseEntity<String> addNewIndex(@PathVariable String id) throws Exception {
    System.out.println("/add-doc: " + id);
    return new ResponseEntity<String>(searchService.addDoc(id), HttpStatus.OK);
  }

  @PostMapping("/search-in-doc-{id}-{numResults}-{typeInt}")
  public ResponseEntity<List<String>> searchInDoc(@PathVariable String id, @PathVariable Integer numResults,
      @PathVariable Integer typeInt,
      @RequestBody String phrase)
      throws Exception {
    System.out.println("/search-in-doc: " + id);
    SearchType searchType = SearchType.fromInt(typeInt);
    System.out.println("Search type: " + searchType);
    String newPhrase = searchService.formatPhrase(phrase);
    System.out.println("Phrase: " + newPhrase);
    return new ResponseEntity<List<String>>(searchService.searchByPhrase(id, newPhrase, numResults, searchType),
        HttpStatus.OK);
  }

  @PostMapping("/get-nlp-info")
  public ResponseEntity<String> getNLPInfo(@RequestBody String phrase) {
    System.out.println("/get-nlp-info");
    String newPhrase = searchService.formatPhrase(phrase);
    System.out.println("Phrase: " + newPhrase);
    return new ResponseEntity<String>(searchService.getNLPInfo(newPhrase), HttpStatus.OK);
  }

}
