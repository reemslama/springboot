package com.example.dection_anomalie.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dection_anomalie.entity.User;
import com.example.dection_anomalie.repository.UserRepo;

@Service
public class UserServices {

    private static final Logger logger = LoggerFactory.getLogger(UserServices.class);

    @Autowired
    private UserRepo userRepo;

    // 1. Enregistrer ou mettre à jour un utilisateur
    public User saveOrUpdate(User user) {
        try {
            return userRepo.save(user);  // Utilisation de save pour créer ou mettre à jour un utilisateur
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'utilisateur", e);
            throw new RuntimeException("Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage());
        }
    }

    // 2. Récupérer tous les utilisateurs
    public List<User> listAll() {
        return userRepo.findAll();  // Récupérer tous les utilisateurs depuis MongoDB
    }

    // 3. Récupérer un utilisateur par ID
    public User getUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null; // Retourne null si l'ID est null ou vide
        }
        Optional<User> user = userRepo.findById(id);
        return user.orElse(null); // Simplifie le code avec Optional.orElse
    }

    // 4. Vérifier si un utilisateur existe avec un email
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Retourne false si l'email est null ou vide
        }
        return userRepo.existsByEmail(email.trim());
    }

    // 5. Vérifier si un utilisateur existe par ID
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false; // Retourne false si l'ID est null ou vide
        }
        return userRepo.existsById(id);
    }

    // 6. Rechercher un utilisateur par email (insensible à la casse)
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null; // Retourne null si l'email est null ou vide
        }
        return userRepo.findByEmailIgnoreCase(email.trim());
    }

    // 7. Supprimer un utilisateur par ID
    public void delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID ne peut pas être null ou vide.");
        }
        try {
            userRepo.deleteById(id);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur", e);
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
}