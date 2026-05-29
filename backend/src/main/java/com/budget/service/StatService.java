package com.budget.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.budget.dto.StatDTO;
import com.budget.entity.Transaction;
import com.budget.repository.TransactionRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service de calcul des statistiques
 * Toute la logique métier du dashboard est ici
 */
@ApplicationScoped
public class StatService {

    @Inject
    private TransactionRepository transactionRepository;

    /**
     * Calcule toutes les statistiques d'un utilisateur
     *
     * @param userId l'id de l'utilisateur connecté
     * @return StatDTO avec toutes les statistiques
     */
    public StatDTO calculer(Long userId) {

        // Récupère toutes les transactions de l'utilisateur
        List<Transaction> transactions = transactionRepository.trouverParUserId(userId);

        StatDTO stat = new StatDTO();

        // ===== CALCUL TOTAL DEPENSES ET REVENUS =====
        double totalDepenses = 0;
        double totalRevenus = 0;

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.DEPENSE) {
                totalDepenses += t.getMontant().doubleValue();
            } else {
                totalRevenus += t.getMontant().doubleValue();
            }
        }

        stat.setTotalDepenses(totalDepenses);
        stat.setTotalRevenus(totalRevenus);

        // Solde = revenus - dépenses
        stat.setSolde(totalRevenus - totalDepenses);

        // ===== DEPENSES PAR CATEGORIE =====
        // Map<nomCategorie, montantTotal>
        Map<String, Double> parCategorie = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.DEPENSE && t.getCategory() != null) {
                String nomCategorie = t.getCategory().getNom();
                // getOrDefault → si la catégorie n'existe pas encore, commence à 0
                double actuel = parCategorie.getOrDefault(nomCategorie, 0.0);
                parCategorie.put(nomCategorie, actuel + t.getMontant().doubleValue());
            }
        }

        stat.setDepensesParCategorie(parCategorie);

        // ===== DEPENSES PAR MOIS =====
        // Map<"annee-mois", montantTotal>
        // ex: { "2024-01": 75000, "2024-02": 50000 }
        Map<String, Double> parMois = new LinkedHashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.DEPENSE) {
                // Format: "2024-01", "2024-02"...
                String moisAnnee = t.getDate().getYear() + "-"
                        + String.format("%02d", t.getDate().getMonthValue());
                double actuel = parMois.getOrDefault(moisAnnee, 0.0);
                parMois.put(moisAnnee, actuel + t.getMontant().doubleValue());
            }
        }

        stat.setDepensesParMois(parMois);

        return stat;
    }
}