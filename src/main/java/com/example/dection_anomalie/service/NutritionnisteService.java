package com.example.dection_anomalie.service;

import com.example.dection_anomalie.entity.Nutritionniste;
import com.example.dection_anomalie.repository.NutritionnisteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class NutritionnisteService {

    @Autowired
    private NutritionnisteRepo nutritionnisteRepo;

    // Récupérer tous les nutritionnistes
    public List<Nutritionniste> getAllNutritionnistes() {
        return nutritionnisteRepo.findAll();
    }

    // Créer un nutritionniste
    public Nutritionniste createNutritionniste(Nutritionniste nutritionniste) {
        if (nutritionnisteRepo.existsByEmail(nutritionniste.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email déjà utilisé");
        }
        return nutritionnisteRepo.save(nutritionniste);  // L'ID est généré automatiquement
    }

    // Supprimer un nutritionniste
    public void deleteNutritionniste(String id) {
        Optional<Nutritionniste> existingNutritionniste = nutritionnisteRepo.findById(id);
        if (!existingNutritionniste.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nutritionniste non trouvé");
        }
        nutritionnisteRepo.deleteById(id);  // Suppression
    }

    // Mettre à jour un nutritionniste
    public Nutritionniste updateNutritionniste(String id, Nutritionniste updated) {
        Nutritionniste existing = nutritionnisteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nutritionniste non trouvé"));

        existing.setName(updated.getName());
        existing.setSpecialty(updated.getSpecialty());
        existing.setEmail(updated.getEmail());

        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            existing.setPassword(updated.getPassword());
        }

        return nutritionnisteRepo.save(existing);  // Sauvegarde du nutritionniste mis à jour
    }

    // Méthode pour récupérer un nutritionniste par son email
    public Nutritionniste getByEmail(String email) {
        return nutritionnisteRepo.findByEmail(email);  // Utilise la méthode du repository pour trouver par email
    }
}
