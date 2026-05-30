package com.budget.service;

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private TransactionRepository transactionRepository;

    @Transactional
    public Map<String, Object> inscrire(RegisterRequest request) {

        if (userRepository.trouverParEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email déjà utilisé");
        }

        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtil.hacher(request.getPassword()));
        userRepository.sauvegarder(user);

        /**
         * Si l'utilisateur a saisi un montant initial
         * on crée automatiquement une transaction REVENU
         * avec la description "Solde initial"
         */
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

        User user = userRepository.trouverParEmail(request.getEmail());

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
