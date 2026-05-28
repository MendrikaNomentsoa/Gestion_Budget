package com.budget.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.budget.dto.BudgetDTO;
import com.budget.entity.Budget;
import com.budget.entity.Category;
import com.budget.entity.User;
import com.budget.repository.BudgetRepository;
import com.budget.repository.CategoryRepository;
import com.budget.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BudgetService {

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private CategoryRepository categoryRepository;

    /**
     * Crée un nouveau budget pour une catégorie et un mois
     * ex: 100 000 Ar pour Nourriture en janvier 2024
     */
    @Transactional
    public BudgetDTO creer(Long userId, BudgetDTO dto) {
        User user = userRepository.trouverParId(userId);
        if (user == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        Category category = categoryRepository.trouverParId(dto.getCategoryId());
        if (category == null) {
            throw new RuntimeException("Catégorie introuvable");
        }

        // Vérifie qu'un budget n'existe pas déjà pour ce mois/catégorie
        Budget existant = budgetRepository.trouverParUserCategoryMois(
            userId, dto.getCategoryId(), dto.getMois(), dto.getAnnee()
        );
        if (existant != null) {
            throw new RuntimeException("Un budget existe déjà pour cette catégorie ce mois-ci");
        }

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimite(BigDecimal.valueOf(dto.getLimite()));
        budget.setMois(dto.getMois());
        budget.setAnnee(dto.getAnnee());

        budgetRepository.sauvegarder(budget);
        return new BudgetDTO(budget);
    }

    /**
     * Modifie la limite d'un budget existant
     */
    @Transactional
    public BudgetDTO modifier(Long userId, Long budgetId, BudgetDTO dto) {
        Budget budget = budgetRepository.trouverParId(budgetId);

        if (budget == null || !budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Budget introuvable");
        }

        budget.setLimite(BigDecimal.valueOf(dto.getLimite()));
        return new BudgetDTO(budgetRepository.modifier(budget));
    }

    /**
     * Supprime un budget
     */
    @Transactional
    public void supprimer(Long userId, Long budgetId) {
        Budget budget = budgetRepository.trouverParId(budgetId);

        if (budget == null || !budget.getUser().getId().equals(userId)) {
            throw new RuntimeException("Budget introuvable");
        }

        budgetRepository.supprimer(budgetId);
    }

    /**
     * Liste tous les budgets de l'utilisateur
     */
    public List<BudgetDTO> listerParUser(Long userId) {
        return budgetRepository.trouverParUserId(userId)
                .stream()
                .map(BudgetDTO::new)
                .collect(Collectors.toList());
    }
}