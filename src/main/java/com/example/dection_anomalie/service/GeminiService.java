package com.example.dection_anomalie.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String askGemini(String question) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Corps de la requête
        Map<String, Object> part = Map.of("text", question);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Log de l'URL et du corps de la requête
            System.out.println("URL: " + url);
            System.out.println("Request Body: " + body);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Log de la réponse pour voir sa structure
            Map<String, Object> responseBody = response.getBody();
            System.out.println("Response Body: " + responseBody);

            if (responseBody != null) {
                Object candidatesObj = responseBody.get("candidates");

                // Vérifier si candidates est une liste
                if (candidatesObj instanceof List<?>) {
                    List<?> candidates = (List<?>) candidatesObj;
                    if (!candidates.isEmpty()) {
                        // Récupérer le premier candidat et traiter son contenu
                        Object firstCandidateObj = candidates.get(0);
                        if (firstCandidateObj instanceof Map<?, ?>) {
                            Map<?, ?> firstCandidate = (Map<?, ?>) firstCandidateObj;

                            Object contentResponseObj = firstCandidate.get("content");
                            if (contentResponseObj instanceof Map<?, ?>) {
                                Map<?, ?> contentResponse = (Map<?, ?>) contentResponseObj;

                                Object partsObj = contentResponse.get("parts");
                                if (partsObj instanceof List<?>) {
                                    List<?> parts = (List<?>) partsObj;

                                    if (!parts.isEmpty()) {
                                        Object firstPartObj = parts.get(0);
                                        if (firstPartObj instanceof Map<?, ?>) {
                                            Map<?, ?> firstPart = (Map<?, ?>) firstPartObj;
                                            Object textObj = firstPart.get("text");
                                            if (textObj instanceof String) {
                                                return (String) textObj;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'appel à Gemini : " + e.getMessage();
        }

        return "Aucune réponse.";
    }
}
