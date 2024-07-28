package com.kareem.project;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.opensearch.client.opensearch.OpenSearchClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NeuralSearchFunctions {
    private final static String OPENSEARCH_URL = System.getenv("OPENSEARCH_URL");
    private final static Integer OPENSEARCH_PORT = Integer.parseInt(System.getenv("OPENSEARCH_PORT"));

    public static String setupNeuralSearch() {
        System.out.println("Setting up neural search");
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        OpenSearchClient client = OpenSearchFunctions.createClient();
        setClusterSettings(client);
        String model_group_id = registerModelGroup();
        String task_id = registerModel(model_group_id);
        String model_id = waitOnRegister(task_id);
        task_id = deployModel(model_id);
        model_id = waitOnRegister(task_id);
        createIngestPipeline(model_id);
        createKnnIndex();
        System.out.println("Neural search setup complete");
        return model_id;
    }

    private static void setClusterSettings(OpenSearchClient client) {
        String url1 = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/_cluster/settings";
        String json1 = "{\"persistent\": {\"plugins\": { \"ml_commons\": {\"only_run_on_ml_node\": \"false\", \"native_memory_threshold\": \"99\"}}}}"; // \"model_access_control_enabled\":
                                                                                                                                                        // \"true\",
        sendRequest(url1, json1, "PUT");

        json1 = "{\"persistent\": {\"indices.breaker.total.limit\": \"800mb\"}}";
        sendRequest(url1, json1, "PUT");
        System.out.println("Cluster settings updated");
    }

    private static String registerModelGroup() {
        String url2 = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/_plugins/_ml/model_groups/_register";
        String json2 = "{\"name\": \"NLP_model_group\", \"description\": \"A model group for NLP models\"}"; // ,\"access_mode\":\"public\"
        Map<String, String> response2 = sendRequest(url2, json2, "POST");
        String model_group_id = response2.get("model_group_id");
        System.out.println("Registered model group");
        System.out.println("Model group ID: " + model_group_id);
        return model_group_id;
    }

    private static String registerModel(String model_group_id) {
        String url3 = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/_plugins/_ml/models/_register";
        String json3 = "{\"name\": \"huggingface/sentence-transformers/msmarco-distilbert-base-tas-b\", \"version\": \"1.0.1\", \"model_group_id\": \""
                + model_group_id + "\", \"model_format\": \"TORCH_SCRIPT\"}";
        Map<String, String> response3 = sendRequest(url3, json3, "POST");
        System.out.println("Response 3: " + response3);
        String task_id = response3.get("task_id");
        System.out.println("Registered model to model group");
        System.out.println("Task ID: " + task_id);
        return task_id;
    }

    private static String waitOnRegister(String task_id) {
        String url4 = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/_plugins/_ml/tasks/" + task_id;
        Map<String, String> response4 = sendRequest(url4, "", "GET");
        String task_status = response4.get("state");
        System.out.println("Task status: " + task_status);
        while (!task_status.equals("COMPLETED")) {
            response4 = sendRequest(url4, "", "GET");
            if (task_status.equals("COMPLETED_WITH_ERROR")) {
                System.out.println("Task failed");
                return "Task failed";
            }
            task_status = response4.get("state");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String model_id = response4.get("model_id");
        System.out.println("Model ID: " + model_id);
        return model_id;
    }

    private static String deployModel(String model_id) {
        String url5 = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/_plugins/_ml/models/" +
                model_id + "/_deploy";
        Map<String, String> response5 = sendRequest(url5, null,
                "POST");
        System.out.println("Deploying model");
        String task_id = response5.get("task_id");
        System.out.println("Task ID: " + task_id);
        return task_id;
    }

    private static void createIngestPipeline(String model_id) {
        String url7 = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/_ingest/pipeline/nlp-ingest-pipeline";
        String json7 = "{\"description\": \"An NLP ingest pipeline\", \"processors\": "
                + "[{\"text_embedding\": {\"model_id\": \""
                + model_id + "\", \"field_map\": {\"text\": \"passage_embedding\"}}}]}";
        sendRequest(url7, json7, "PUT");
        System.out.println("Created ingest pipeline");

    }

    private static void createKnnIndex() {
        String url = "http://" + OPENSEARCH_URL + ":" + OPENSEARCH_PORT + "/my-nlp-index";
        String json = "{"
                + "\"settings\": {"
                + "\"index.knn\": true, "
                + "\"default_pipeline\": \"nlp-ingest-pipeline\""
                + "}, "
                + "\"mappings\": {"
                + "\"properties\": {"
                + "\"text\": {\"type\": \"text\"}, "
                + "\"passage_embedding\": {"
                + "\"type\": \"knn_vector\", "
                + "\"dimension\": 768, "
                + "\"method\": {"
                + "\"engine\": \"lucene\", "
                + "\"space_type\": \"l2\", "
                + "\"name\": \"hnsw\", "
                + "\"parameters\": {}"
                + "}"
                + "}"
                + "}"
                + "}"
                + "}";
        sendRequest(url, json, "PUT");
        System.out.println("Created k-NN index");
    }

    private static Map<String, String> sendRequest(String url, String jsonRequest, String requestType) {
        try {
            String jsonResponse = sendRequestHelper(url, jsonRequest, requestType);

            // Convert JSON response to a Map
            Map<String, String> responseMap = jsonToMap(jsonResponse);
            return responseMap;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String sendRequestHelper(String url, String json, String requestType)
            throws IOException, InterruptedException {
        // Create HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Create HttpRequest
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");

        if (requestType.equals("GET")) {
            requestBuilder.GET();
        } else if (requestType.equals("POST")) {
            if (json == null) {
                requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
            } else {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json));
            }
        } else if (requestType.equals("PUT")) {
            requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json));
        } else if (requestType.equals("DELETE")) {
            requestBuilder.DELETE();
        }
        HttpRequest request = requestBuilder.build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Return the response body as a string
        return response.body();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> jsonToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Map.class);
    }

    @SuppressWarnings("unused")
    private static <T> T jsonToClass(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

}
