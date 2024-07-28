package com.kareem.project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;

@Service
public class SearchService {
  private OpenSearchClient client;
  private Boolean indexCreated = false;
  final String indexName = "books";
  private final StanfordCoreNLP pipeline = initPipeline();
  // private final String model_id = NeuralSearchFunctions.setupNeuralSearch();

  @Autowired
  private BookRepository bookRepository;

  ObjectMapper objectMapper = new ObjectMapper();

  public List<Book> searchBooks(String title) throws Exception {
    String url = "https://gutendex.com/books" + makeSearchQuery(title);

    String response = sampleApiRequest(url);
    SearchResult results = objectMapper.readValue(response, SearchResult.class);
    return results.getResults();
  }

  public Book searchBookById(String id) throws Exception {
    String url = "https://gutendex.com/books?ids=" + id;

    String response = sampleApiRequest(url);
    SearchResult results = objectMapper.readValue(response, SearchResult.class);
    return results.getFirstResult();
  }

  public List<Book> searchBooksById(String ids) throws Exception {
    String url = "https://gutendex.com/books?ids=" + ids;

    String response = sampleApiRequest(url);
    SearchResult results = objectMapper.readValue(response, SearchResult.class);
    return results.getResults();
  }

  private String makeSearchQuery(String title) {
    StringBuilder str = new StringBuilder();
    str.append("?search=");

    String formattedTitle = title.replace(" ", "%20");
    str.append(formattedTitle);

    return str.toString();
  }

  String sampleApiRequest(String title) throws Exception {
    HttpClient client = HttpClient.newBuilder()
        .followRedirects(Redirect.NORMAL) // Enable automatic redirect following
        .build();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(title))
        .build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new RuntimeException("Failed: HTTP error code : " + response.statusCode());
    }

    return response.body();
  }

  public Book addBook(Book book) throws Exception {
    System.out.println("add book: " + book.getTitle());
    return bookRepository.save(book);
  }

  public void deleteBook(String bookId) throws Exception {
    System.out.println("delete book id: " + bookId);
    bookRepository.deleteById(bookId);
    if (indexCreated) {
      deleteDoc(bookId);
    }
  }

  public List<Book> getMyBooks() {
    return bookRepository.findAll();
  }

  public Boolean idInMyBookIds(String id) {
    return bookRepository.findById(id).isPresent();
  }

  public String getBookText(Book book) throws Exception {
    String url = book.getFormats().getOrDefault("text/plain; charset=us-ascii", "");
    Document doc = Jsoup.connect(url).get();
    String bodyText = doc.select("body").text();
    String[] result = bodyText.split("\\*{3}.*?\\*{3}");
    return result[1];
  }

  public void createIndex() {
    if (client == null) {
      client = OpenSearchFunctions.createClient();
    }
    if (client == null) {
      System.out.println("Client is null");
      return;
    }
    OpenSearchFunctions.createIndex(client, indexName); // name
    indexCreated = true;
    System.out.println("Index created with name: " + indexName);
  }

  public String addDoc(String id) throws Exception {
    System.out.println("Index created: " + indexCreated);
    if (!indexCreated) {
      createIndex();
    }
    Book book = bookRepository.findById(id).get();
    System.out.println("Adding document for book: " + book.getTitle());
    String text = getBookText(book);
    System.out.println("Start of text: " + text.substring(0, 100));
    OpenSearchFunctions.createDocument(client, id, text, indexName);
    // OpenSearchFunctions.createDocument(client, id, text, "my-nlp-index");
    return "Index added for book with id: " + id + ", title: " + book.getTitle();
  }

  public void deleteDoc(String id) throws Exception {
    OpenSearchFunctions.deleteDocument(client, id);
    System.out.println("Document deleted for book with id: " + id);
  }

  public List<String> searchByPhrase(String id, String phrase, Integer numResults, SearchType searchType) throws OpenSearchException, IOException {
    if (!indexCreated) {
      createIndex();
    }
    List<String> matches = OpenSearchFunctions.searchByPhrase(client, id, phrase,
    numResults, searchType, indexName, null);
    // List<String> matches = OpenSearchFunctions.searchByPhrase(client, id, phrase, numResults, searchType,
    //     "my-nlp-index", model_id);
    System.out.println("Len matches: " + matches.size());
    return matches;
  }

  public String formatPhrase(String phrase) {
    try {
      // Decode the URL-encoded string
      String decodedString = URLDecoder.decode(phrase, "UTF-8");
      if (decodedString.endsWith("=")) {
        // Remove the last character (equals sign)
        decodedString = decodedString.substring(0, decodedString.length() - 1);
      }
      // Print the decoded string
      System.out.println(decodedString);

      return decodedString;
    } catch (UnsupportedEncodingException e) {
      // Handle the exception
      System.err.println("Error: " + e.getMessage());
      return "--------";
    }
  }

  public String getNLPInfo(String unformatted) {
    // Create an Annotation object with the input text
    String text = formatPhrase(unformatted);
    unformatted = unformatted.replace("<em>", "");
    unformatted = unformatted.replace("</em>", "");
    System.out.println("Started analysis: " + text);

    int sentimentInt;
    int totalScore = 0;
    String sentimentName;
    Annotation annotation = pipeline.process(text);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    List<Integer> sentimentScores = new ArrayList<>();
    for (CoreMap sentence : sentences) {
      Tree tree = sentence.get(SentimentAnnotatedTree.class);
      sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
      totalScore += sentimentInt;
      sentimentScores.add(sentimentInt);
      sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
      System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
    }

    String modalSentiment = findMode(sentimentScores);
    System.out.println("Modal sentiment: " + modalSentiment);

    double averageScore = totalScore / sentences.size();
    String totalSentiment = getSentimentLabel(averageScore);
    System.out.println("Total sentiment: " + totalSentiment);

    return modalSentiment;
  }

  public StanfordCoreNLP initPipeline() {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
    return new StanfordCoreNLP(props);
  }

  // Method to convert average sentiment score to a sentiment label
  private String getSentimentLabel(double score) {
    if (score < 0.5) {
      return "Very Negative";
    } else if (score < 1.5) {
      return "Negative";
    } else if (score < 2.5) {
      return "Neutral";
    } else if (score < 3.5) {
      return "Positive";
    } else {
      return "Very Positive";
    }
  }

  private String findMode(List<Integer> numbers) {
    // Create a map to count the occurrences of each number
    Map<Integer, Integer> countMap = new HashMap<>();

    // Count the occurrences of each number
    for (int number : numbers) {
      countMap.put(number, countMap.getOrDefault(number, 0) + 1);
    }

    // Find the maximum count
    int maxCount = Collections.max(countMap.values());

    // Find the number(s) with the maximum count
    List<Integer> modes = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
      if (entry.getValue() == maxCount) {
        modes.add(entry.getKey());
      }
    }

    double avg = modes.stream().mapToInt(Integer::intValue).sum() / modes.size();

    String modalSentiment = "Neutral";
    if (modes.size() == 1) {
      modalSentiment = getSentimentLabel(modes.get(0));
    } else if (avg > 3) {
      modalSentiment = "Positive";
    } else if (avg < 2) {
      modalSentiment = "Negative";
    } else {
      modalSentiment = "Mixed";
    }

    return modalSentiment;
  }
}
