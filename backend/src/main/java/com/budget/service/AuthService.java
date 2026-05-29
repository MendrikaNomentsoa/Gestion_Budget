package com.budget.service;

import java.util.HashMap;
import java.util.Map;

import com.budget.dto.LoginRequest;
import com.budget.dto.RegisterRequest;
import com.budget.entity.User;
import com.budget.repository.UserRepository;
import com.budget.security.JwtUtil;
import com.budget.security.PasswordUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service d'authentification — contient toute la logique métier
 * 
 * @ApplicationScoped → une seule instance CDI
 * @Inject → CDI injecte automatiquement UserRepository
 */
@ApplicationScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    /**
     * Inscrit un nouvel utilisateur
     * 
     * @Transactional → JPA ouvre une transaction automatiquement
     *   si une erreur survient, tout est annulé (rollback)
     * 
     * @param request les données d'inscription
     * @return un Map contenant le token JWT et les infos user
     */
    @Transactional
    public Map<String, Object> inscrire(RegisterRequest request) {

        // Vérifie si l'email existe déjà
        if (userRepository.trouverParEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Crée le nouvel utilisateur
        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        // Hache le mot de passe avant de le stocker
        user.setPassword(PasswordUtil.hacher(request.getPassword()));

        // Sauvegarde en base
        userRepository.sauvegarder(user);

        // Génère un token JWT
        String token = JwtUtil.generer(user.getId());

        // Retourne le token et les infos user
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("token", token);
        resultat.put("userId", user.getId());
        resultat.put("nom", user.getNom());
        resultat.put("email", user.getEmail());
        return resultat;
    }

    /**
     * Connecte un utilisateur existant
     * 
     * @param request les données de connexion
     * @return un Map contenant le token JWT et les infos user
     */
    public Map<String, Object> connecter(LoginRequest request) {

        // Cherche l'utilisateur par email
        User user = userRepository.trouverParEmail(request.getEmail());

        // Vérifie que l'utilisateur existe et que le mot de passe est correct
        if (user == null || !PasswordUtil.verifier(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // Génère un token JWT
        String token = JwtUtil.generer(user.getId());

        Map<String, Object> resultat = new HashMap<>();
        resultat.put("token", token);
        resultat.put("userId", user.getId());
        resultat.put("nom", user.getNom());
        resultat.put("email", user.getEmail());
        return resultat;
    }
}