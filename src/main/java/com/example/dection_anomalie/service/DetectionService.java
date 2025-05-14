package com.example.dection_anomalie.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DetectionService {

    private final RestTemplate restTemplate;
    private static final String FLASK_API_URL = "http://localhost:5000/predict"; // Assure-toi que Flask fonctionne à cette adresse

    public DetectionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String detectFoodImage(MultipartFile file) throws IOException {
        // Crée un fichier temporaire localement
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        // Préparation de la requête multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFile.toFile()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Envoi de la requête à l'API Flask
            ResponseEntity<String> response = restTemplate.exchange(
                    FLASK_API_URL, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody(); // Retourner la réponse de Flask
            } else {
                throw new IOException("Réponse non valide de l'API Flask : " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de la communication avec l'API Flask : " + e.getMessage());
        } finally {
            Files.deleteIfExists(tempFile); // Nettoyage du fichier temporaire
        }
    }
}
