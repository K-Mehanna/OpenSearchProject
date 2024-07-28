package com.kareem.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.NeuralQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.opensearch.client.opensearch.core.search.HighlighterOrder;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.SourceConfig;
import org.opensearch.client.opensearch.core.search.SourceFilter;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;

public class OpenSearchFunctions {

  public static OpenSearchClient createClient() {
    try {
      String OPENSEARCH_URL = System.getenv("OPENSEARCH_URL");
      Integer OPENSEARCH_PORT = Integer.parseInt(System.getenv("OPENSEARCH_PORT"));
      final HttpHost host = new HttpHost("http", OPENSEARCH_URL, OPENSEARCH_PORT); // "docker.for.mac.localhost", 9200
      System.out.println("Creating client: " + host.toURI());
      final OpenSearchTransport transport = ApacheHttpClient5TransportBuilder
          .builder(host)
          .setMapper(new JacksonJsonpMapper())
          .build();

      OpenSearchClient client = new OpenSearchClient(transport);
      System.out.println("Client in func: " + client.toString());
      var version = client.info().version();
      System.out.println(version.distribution() + "@" + version.number());
      return client;
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
      return null;
    } catch (OpenSearchException e) {
      System.out.println("OpenSearchException: " + e.getMessage());
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  static List<Map> testSearchByPhrase(OpenSearchClient client, Boolean isNeural, String modelId) {
    SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder().index("my-nlp-index");
    if (isNeural) {
      searchRequestBuilder
          .source(SourceConfig.of(src -> src
              .filter(SourceFilter.of(f -> f
                  .excludes("passage_embedding")))))
          .query(Query.of(q -> q
              .neural(NeuralQuery.of(n -> n
                  .field("passage_embedding").modelId(modelId).queryText("wild west").k(5)))));
    } else {
      searchRequestBuilder
          .source(SourceConfig.of(src -> src
              .filter(SourceFilter.of(f -> f
                  .excludes("passage_embedding")))))
          .query(Query.of(q -> q
              .match(MatchQuery.of(m -> m
                  .field("text")
                  .query(FieldValue.of("wild west"))))));
    }

    SearchRequest searchRequest = searchRequestBuilder
        .highlight(h -> h.fields("text", new HighlightField.Builder()
            .numberOfFragments(32)
            .fragmentSize(20)
            .order(HighlighterOrder.Score)
            .build()))
        .source(new SourceConfig.Builder().filter(f -> f.includes("text")).build())
        .build();

    List<Map> results = new ArrayList<>();
    // Execute the search request
    try {
      SearchResponse<Map> searchResponse = client.search(searchRequest, Map.class);
      // Process the search response
      System.out.println("Total hits: " + searchResponse.hits().total().value());
      System.out.println("Hits: ");
      for (Hit<Map> hit : searchResponse.hits().hits()) {
        System.out.println(hit.highlight());
        results.add(hit.source());
      }
      return results;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  static void createIndex(OpenSearchClient client, String indexName) {
    try {
      if (!client.indices().exists(r -> r.index(indexName)).value()) {
        System.out.println("Creating index: " + indexName);
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(indexName).build();
        client.indices().create(createIndexRequest);
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  static void createDocument(OpenSearchClient client, String docId, String docText, String indexName) {
    try {
      System.out.println("Creating document with id: " + docId);
      IndexData indexData = new IndexData(docText);
      IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(indexName).id(docId)
          .document(indexData).build();
      client.index(indexRequest);
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  static List<String> searchByPhrase(OpenSearchClient client, String id, String phrase, Integer numResults,
      SearchType searchType, String indexName, String modelId) {
    try {
      SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder().index(indexName);
      switch (searchType) {
        case NORMAL:
          System.out.println("Normal search by phrase: " + phrase);
          searchRequestBuilder
              .query(q -> q.bool(b -> b
                  .must(m -> m.term(t -> t.field("_id").value(FieldValue.of(id))))
                  .must(m -> m.match(mt -> mt.field("text").query(FieldValue.of(phrase))))));
          break;
        case WILDCARD:
          System.out.println("Wildcard search by phrase: " + phrase);
          searchRequestBuilder.query(q -> q.bool(b -> b
              .must(m -> m.term(t -> t.field("_id").value(FieldValue.of(id))))
              .filter(f -> f.wildcard(w -> w.field("text").value(phrase).caseInsensitive(true)))));
          break;
        case REGEX:
          System.out.println("Regex search by phrase: " + phrase);
          searchRequestBuilder.query(q -> q.bool(b -> b
              .must(m -> m.term(t -> t.field("_id").value(FieldValue.of(id))))
              .filter(f -> f.regexp(w -> w.field("text").value(phrase)))));
          break;
        // case NEURAL:
        //   System.out.println("Neural search by phrase: " + phrase);
        //   searchRequestBuilder.query(q -> q.bool(b -> b
        //       .must(m -> m.term(t -> t.field("_id").value(FieldValue.of(id))))
        //       .must(m -> m.neural(n -> n
        //           .field("passage_embedding").modelId(modelId).queryText(phrase).k(5)))));
        //   break;
        default:
          break;
      }

      SearchRequest searchRequest = searchRequestBuilder
          .highlight(h -> h.fields("text", new HighlightField.Builder()
              .numberOfFragments(numResults)
              .fragmentSize(500)
              .order(HighlighterOrder.Score)
              .build()))
          .source(new SourceConfig.Builder().filter(f -> f.includes("text")).build())
          .build();

      SearchResponse<IndexData> searchResponse = client.search(searchRequest, IndexData.class);
      List<String> results = new ArrayList<>();
      System.out.println("Total hits: " + searchResponse.hits().total().value());
      for (Hit<IndexData> hit : searchResponse.hits().hits()) {
        results.addAll(hit.highlight().get("text")); //.getOrDefault("text", List.of("##############")
      }
      return results;
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return new ArrayList<>();
    }
  }

  static void deleteDocument(OpenSearchClient client, String docId) {
    String indexName = "books";
    System.out.println("Deleting document with id: " + docId);
    try {
      client.delete(d -> d.index(indexName).id(docId));
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  static void deleteIndex(OpenSearchClient client, String indexName) {
    System.out.println("Deleting index: " + indexName);
    try {
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexName).build();
      DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
      System.out.println(deleteIndexResponse.toString());
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}
