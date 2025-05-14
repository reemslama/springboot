package com.example.dection_anomalie.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dection_anomalie.entity.User;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    
    // Recherche d'un utilisateur par email (insensible à la casse)
    User findByEmail(String email);

    // Vérifie si un utilisateur existe avec l'email spécifié
    boolean existsByEmail(String email);

    // Recherche d'un utilisateur par email, insensible à la casse
    @Query("{ 'email' : { $regex: ?0, $options: 'i' } }")
    User findByEmailIgnoreCase(String email);
}