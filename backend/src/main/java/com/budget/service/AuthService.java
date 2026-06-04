package com.budget.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.budget.dto.LoginRequest;
import com.budget.dto.RegisterRequest;
import com.budget.entity.Transaction;
import com.budget.entity.User;
import com.budget.repository.TransactionRepository;
import com.budget.repository.UserRepository;
import com.budget.security.JwtUtil;
import com.budget.security.PasswordUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private TransactionRepository transactionRepository;

    @Transactional
    public Map<String, Object> inscrire(RegisterRequest request) {

        // Validation email
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("L'email est obligatoire");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Format d'email invalide");
        }

        // Validation mot de passe
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Le mot de passe est obligatoire");
        }
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Validation nom
        if (request.getNom() == null || request.getNom().isBlank()) {
            throw new RuntimeException("Le nom est obligatoire");
        }

        // Email déjà utilisé
        if (userRepository.trouverParEmail(request.getEmail()) != null) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(PasswordUtil.hacher(request.getPassword()));
        userRepository.sauvegarder(user);

        if (request.getMontantInitial() > 0) {
            Transaction soldeInitial = new Transaction();
            soldeInitial.setMontant(BigDecimal.valueOf(request.getMontantInitial()));
            soldeInitial.setType(Transaction.Type.REVENU);
            soldeInitial.setDescription("Solde initial");
            soldeInitial.setDate(LocalDate.now());
            soldeInitial.setUser(user);
            transactionRepository.sauvegarder(soldeInitial);
        }

        String token = JwtUtil.generer(user.getId());
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("token", token);
        resultat.put("userId", user.getId());
        resultat.put("nom", user.getNom());
        resultat.put("email", user.getEmail());
        return resultat;
    }

    public Map<String, Object> connecter(LoginRequest request) {

        // Validation
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("L'email est obligatoire");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Le mot de passe est obligatoire");
        }

        User user = userRepository.trouverParEmail(request.getEmail().toLowerCase().trim());

        if (user == null || !PasswordUtil.verifier(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        String token = JwtUtil.generer(user.getId());
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("token", token);
        resultat.put("userId", user.getId());
        resultat.put("nom", user.getNom());
        resultat.put("email", user.getEmail());
        return resultat;
    }
}
