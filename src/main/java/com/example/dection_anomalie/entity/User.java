package com.example.dection_anomalie.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Integer age;
    private String sexe;
    private List<String> problemes;
    private String resetCode;
    private LocalDateTime resetCodeExpiry;  // Champ pour l'expiration du code

    // Constructeur vide
    public User() {
        this.resetCodeExpiry = null;
    }

    // Constructeur avec tous les champs
    public User(String nom, String prenom, String email, Integer age, String sexe, List<String> problemes) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.age = age;
        this.sexe = sexe;
        this.problemes = problemes;
        this.resetCodeExpiry = null;  // Initialisation explicite
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être null ou vide.");
        }
        this.nom = nom.trim();
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être null ou vide.");
        }
        this.prenom = prenom.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("L'email fourni est invalide.");
        }
        this.email = email.trim().toLowerCase();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        if (age != null && age <= 0) {
            throw new IllegalArgumentException("L'âge doit être un nombre positif.");
        }
        this.age = age;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public List<String> getProblemes() {
        return problemes;
    }

    public void setProblemes(List<String> problemes) {
        this.problemes = problemes;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        this.motDePasse = motDePasse;  // Le hachage doit être géré dans le service
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public LocalDateTime getResetCodeExpiry() {
        return resetCodeExpiry;
    }

    public void setResetCodeExpiry(LocalDateTime resetCodeExpiry) {
        this.resetCodeExpiry = resetCodeExpiry;
    }

    public void setPassword(String newPassword) {
        throw new UnsupportedOperationException("Unimplemented method 'setPassword'");
    }
}