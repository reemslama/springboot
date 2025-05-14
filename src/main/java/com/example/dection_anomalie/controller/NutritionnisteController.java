package com.example.dection_anomalie.controller;

import com.example.dection_anomalie.entity.Nutritionniste;
import com.example.dection_anomalie.service.NutritionnisteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nutritionists")
@CrossOrigin(origins = "http://localhost:4200")  // Permet à Angular d'accéder à l'API
public class NutritionnisteController {

    @Autowired
    private NutritionnisteService nutritionnisteService;

    // Créer un nutritionniste
    @PostMapping
    public ResponseEntity<Nutritionniste> createNutritionniste(@RequestBody Nutritionniste nutritionniste) {
        try {
            Nutritionniste created = nutritionnisteService.createNutritionniste(nutritionniste);
            return new ResponseEntity<>(created, HttpStatus.CREATED);  // Code 201 pour la création
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Code 400 pour une erreur
        }
    }

    // Récupérer tous les nutritionnistes
    @GetMapping("/all")
    public ResponseEntity<List<Nutritionniste>> getAllNutritionnistes() {
        try {
            List<Nutritionniste> nutritionnistes = nutritionnisteService.getAllNutritionnistes();
            if (nutritionnistes.isEmpty()) {
                return new ResponseEntity<>(nutritionnistes, HttpStatus.OK);  // Code 200 avec tableau vide
            }
            return new ResponseEntity<>(nutritionnistes, HttpStatus.OK);  // Code 200 pour succès
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Code 500 pour erreur serveur
        }
    }

    // Supprimer un nutritionniste
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNutritionniste(@PathVariable String id) {
        try {
            nutritionnisteService.deleteNutritionniste(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Code 204 si suppression réussie
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Code 404 si nutritionniste non trouvé
        }
    }

    // Connexion d'un nutritionniste
    @PostMapping("/login")
    public ResponseEntity<Nutritionniste> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Nutritionniste nutritionniste = nutritionnisteService.getByEmail(email);
        if (nutritionniste != null && password.equals(nutritionniste.getPassword())) {
            return ResponseEntity.ok(nutritionniste);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // Code 401 pour échec de l'authentification
    }
    // Ajouter une méthode pour obtenir le nombre de nutritionnistes
    

}
