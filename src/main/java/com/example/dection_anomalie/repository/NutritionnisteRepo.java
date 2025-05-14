package com.example.dection_anomalie.repository;

import com.example.dection_anomalie.entity.Nutritionniste;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionnisteRepo extends MongoRepository<Nutritionniste, String> {
    boolean existsByEmail(String email);  // Vérifie si un nutritionniste avec cet email existe déjà
    Nutritionniste findByEmail(String email);
}
