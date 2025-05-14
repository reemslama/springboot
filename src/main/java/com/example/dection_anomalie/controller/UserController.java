package com.example.dection_anomalie.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dection_anomalie.entity.User;
import com.example.dection_anomalie.repository.UserRepo;
import com.example.dection_anomalie.service.EmailService;
import com.example.dection_anomalie.service.GeminiService;
import com.example.dection_anomalie.service.UserServices;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private EmailService emailService;

    /**
     * Création d’un utilisateur
     */
    @PostMapping("/save")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "L'email est requis"));
            }

            if (userServices.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Email déjà utilisé"));
            }

            User createdUser = userServices.saveOrUpdate(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("status", "success", "message", "Utilisateur créé avec succès", "data", createdUser)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "error", "message", "Erreur lors de la création : " + e.getMessage())
            );
        }
    }

    /**
     * Connexion d’un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String motDePasse = credentials.get("motDePasse");

        User user = userServices.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email non trouvé");
        }

        if (!user.getMotDePasse().equals(motDePasse)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        }

        return ResponseEntity.ok(user);
    }

    /**
     * Mise à jour d’un utilisateur via email
     */
    @PutMapping("/email/{email}")
    public ResponseEntity<?> updateUserByEmail(@PathVariable String email, @RequestBody User newUser) {
        try {
            User existingUser = userServices.findByEmail(email);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé"));
            }

            if (!existingUser.getEmail().equals(newUser.getEmail())
                    && userServices.existsByEmail(newUser.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email déjà utilisé"));
            }

            updateUserDetails(existingUser, newUser);
            User updatedUser = userServices.saveOrUpdate(existingUser);

            return ResponseEntity.ok(Map.of("message", "Utilisateur mis à jour", "data", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la mise à jour : " + e.getMessage()));
        }
    }

    /**
     * Suppression d’un utilisateur via email
     */
    @DeleteMapping("/email/{email}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable String email) {
        User user = userServices.findByEmail(email);
        if (user != null) {
            userServices.delete(user.getId());
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé"));
    }

    /**
     * Récupération d’un utilisateur via email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        User user = userServices.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé"));
    }

    /**
     * Envoi du code de réinitialisation de mot de passe par email
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> sendResetCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email est requis"));
            }

            email = email.trim().toLowerCase();
            if (!isValidEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email invalide"));
            }

            User user = userServices.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé"));
            }

            String code = String.format("%06d", new Random().nextInt(999999));
            user.setResetCode(code);
            user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(15));
            userServices.saveOrUpdate(user);

            emailService.sendResetCode(email, code);

            return ResponseEntity.ok(Map.of("message", "Code de réinitialisation envoyé"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne : " + e.getMessage()));
        }
    }

    /**
     * Vérification du code de réinitialisation
     */
    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String code = request.get("code");

            if (email == null || code == null || email.isBlank() || code.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email et code sont requis"));
            }

            email = email.trim().toLowerCase();
            code = code.trim();

            User user = userServices.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé"));
            }

            if (!code.equals(user.getResetCode())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Code incorrect"));
            }

            if (user.getResetCodeExpiry() == null || LocalDateTime.now().isAfter(user.getResetCodeExpiry())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Code expiré"));
            }

            return ResponseEntity.ok(Map.of("message", "Code valide"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne : " + e.getMessage()));
        }
    }

    /**
     * Mise à jour du mot de passe après vérification du code
     */
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
    
        // Vérification des champs requis
        if (email == null || newPassword == null || email.isBlank() || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tous les champs sont requis"));
        }
        
    
        // Vérification que le mot de passe et la confirmation sont identiques
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Les mots de passe ne correspondent pas"));
        }
    
        User user = userServices.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé"));
        }
    
        user.setMotDePasse(newPassword); // Mot de passe mis à jour
        userServices.saveOrUpdate(user);
    
        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));
    }
    

    /**
     * Méthode utilitaire pour mettre à jour un utilisateur
     */
    private void updateUserDetails(User user, User newUser) {
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) user.setEmail(newUser.getEmail());
        if (newUser.getNom() != null) user.setNom(newUser.getNom());
        if (newUser.getPrenom() != null) user.setPrenom(newUser.getPrenom());
        if (newUser.getMotDePasse() != null) user.setMotDePasse(newUser.getMotDePasse());
        if (newUser.getAge() != null) user.setAge(newUser.getAge());
        if (newUser.getProblemes() != null) user.setProblemes(newUser.getProblemes());
    }

    /**
     * Vérifie si l'email est valide
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
    @RequestMapping("/api/gemini")
    @CrossOrigin(origins = "*") // ou spécifie l'origine Angular uniquement
    public class GeminiController {

        @Autowired
        private GeminiService geminiService;

        @PostMapping("/ask")
        public String askGemini(@RequestBody Map<String, String> request) {
            String question = request.get("question");
            return geminiService.askGemini(question);
        }
    }

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/count")
    public long countUsers() {
        return userRepo.count();
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userServices.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la récupération des utilisateurs", "error", e.getMessage()));
        }
    }



}