package com.budget.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.budget.dto.AlertDTO;
import com.budget.entity.Budget;
import com.budget.entity.Transaction;
import com.budget.repository.BudgetRepository;
import com.budget.repository.TransactionRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service de détection des dépassements de budget
 *
 * Logique :
 * 1. Récupère tous les budgets de l'utilisateur
 * 2. Pour chaque budget, calcule le total des dépenses
 *    de la catégorie pour le mois concerné
 * 3. Si total > limite → génère une alerte
 */
@ApplicationScoped
public class AlertService {

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private TransactionRepository transactionRepository;

    /**
     * Vérifie tous les budgets et retourne les alertes de dépassement
     *
     * @param userId l'id de l'utilisateur connecté
     * @return liste des alertes (vide si aucun dépassement)
     */
    public List<AlertDTO> verifier(Long userId) {

        List<AlertDTO> alertes = new ArrayList<>();

        // Récupère tous les budgets de l'utilisateur
        List<Budget> budgets = budgetRepository.trouverParUserId(userId);

        // Récupère toutes les transactions de l'utilisateur
        List<Transaction> transactions = transactionRepository.trouverParUserId(userId);

        // Pour chaque budget défini
        for (Budget budget : budgets) {

            Long categoryId = budget.getCategory().getId();
            String categoryNom = budget.getCategory().getNom();
            int mois = budget.getMois();
            int annee = budget.getAnnee();
            double limite = budget.getLimite().doubleValue();

            // Calcule le total des dépenses pour cette catégorie et ce mois
            double totalDepenses = 0;

            for (Transaction t : transactions) {
                if (t.getType() == Transaction.Type.DEPENSE
                        && t.getCategory() != null
                        && t.getCategory().getId().equals(categoryId)
                        && t.getDate().getMonthValue() == mois
                        && t.getDate().getYear() == annee) {

                    totalDepenses += t.getMontant().doubleValue();
                }
            }

            // Si le total dépasse la limite → alerte
            if (totalDepenses > limite) {
                alertes.add(new AlertDTO(categoryNom, limite, totalDepenses));
            }
        }

        return alertes;
    }

    /**
     * Vérifie les alertes pour un mois précis
     *
     * @param userId l'id de l'utilisateur
     * @param mois le mois à vérifier (1-12)
     * @param annee l'année à vérifier
     */
    public List<AlertDTO> verifierParMois(Long userId, int mois, int annee) {

        List<AlertDTO> alertes = new ArrayList<>();
        List<Budget> budgets = budgetRepository.trouverParUserId(userId);
        List<Transaction> transactions = transactionRepository.trouverParUserId(userId);

        // Calcule les dépenses par catégorie pour ce mois
        Map<Long, Double> depensesParCategory = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.DEPENSE
                    && t.getCategory() != null
                    && t.getDate().getMonthValue() == mois
                    && t.getDate().getYear() == annee) {

                Long catId = t.getCategory().getId();
                double actuel = depensesParCategory.getOrDefault(catId, 0.0);
                depensesParCategory.put(catId, actuel + t.getMontant().doubleValue());
            }
        }

        // Compare avec les budgets du mois
        for (Budget budget : budgets) {
            if (budget.getMois() == mois && budget.getAnnee() == annee) {
                Long catId = budget.getCategory().getId();
                double depense = depensesParCategory.getOrDefault(catId, 0.0);
                double limite = budget.getLimite().doubleValue();

                if (depense > limite) {
                    alertes.add(new AlertDTO(
                        budget.getCategory().getNom(),
                        limite,
                        depense
                    ));
                }
            }
        }

        return alertes;
    }
}