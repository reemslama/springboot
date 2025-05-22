package com.example.dection_anomalie.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;

@RestController
@RequestMapping("/api/analyze")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowCredentials = "true")
public class DetectionController {

    private static final String FLASK_API_URL = "http://127.0.0.1:5000/analyze";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode nutritionData;

    public DetectionController() {
        try {
            nutritionData = objectMapper.readTree(new ClassPathResource("nutrition_data.json").getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de nutrition_data.json", e);
        }
    }

    @PostMapping
    public ResponseEntity<String> analyzeImages(@RequestParam("profile") String profile,
                                               @RequestParam("images") MultipartFile[] images) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(FLASK_API_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            builder.addTextBody("profile", profile, ContentType.TEXT_PLAIN);

            for (MultipartFile image : images) {
                builder.addBinaryBody("images", image.getInputStream(),
                        ContentType.create(image.getContentType()), image.getOriginalFilename());
            }

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                String result = EntityUtils.toString(responseEntity);

                JsonNode responseJson = objectMapper.readTree(result);
                JsonNode resultsArray = responseJson.get("results");

                if (resultsArray.isArray()) {
                    for (JsonNode resultNode : resultsArray) {
                        JsonNode counts = resultNode.get("counts");
                        JsonNode foodCounts = counts.get("food");
                        if (foodCounts != null && foodCounts.isObject()) {
                            ObjectNode foodCountsWithNutrition = objectMapper.createObjectNode();
                            Iterator<String> foodNames = foodCounts.fieldNames();
                            while (foodNames.hasNext()) {
                                String foodName = foodNames.next();
                                int quantity = foodCounts.get(foodName).asInt();
                                JsonNode nutrition = nutritionData.get(foodName);
                                if (nutrition != null) {
                                    ObjectNode foodData = objectMapper.createObjectNode();
                                    foodData.put("quantity", quantity);
                                    foodData.set("nutrition", nutrition); // Include full nutrition data
                                    foodCountsWithNutrition.set(foodName, foodData);
                                } else {
                                    foodCountsWithNutrition.put(foodName, quantity);
                                }
                            }
                            ((ObjectNode) counts).set("food", foodCountsWithNutrition);
                        }
                    }
                }

                return ResponseEntity.ok(objectMapper.writeValueAsString(responseJson));
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de la communication avec l'API Flask : " + e.getMessage());
        }
    }
}