package com.budget.dto;

import java.util.Map;

/**
 * DTO pour les statistiques du dashboard
 * Contient tout ce dont le frontend a besoin pour afficher :
 * - Le solde actuel
 * - Total des dépenses et revenus
 * - Dépenses par catégorie (pour le graphique camembert)
 * - Dépenses par mois (pour le graphique courbe)
 */
public class StatDTO {

    /**
     * Solde = total revenus - total dépenses
     */
    private double solde;

    private double totalDepenses;
    private double totalRevenus;

    /**
     * Map<nomCategorie, montantTotal>
     * ex: { "Nourriture": 50000, "Transport": 25000 }
     */
    private Map<String, Double> depensesParCategorie;

    /**
     * Map<"mois-annee", montantTotal>
     * ex: { "2024-01": 75000, "2024-02": 50000 }
     */
    private Map<String, Double> depensesParMois;

    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }

    public double getTotalDepenses() { return totalDepenses; }
    public void setTotalDepenses(double totalDepenses) { this.totalDepenses = totalDepenses; }

    public double getTotalRevenus() { return totalRevenus; }
    public void setTotalRevenus(double totalRevenus) { this.totalRevenus = totalRevenus; }

    public Map<String, Double> getDepensesParCategorie() { return depensesParCategorie; }
    public void setDepensesParCategorie(Map<String, Double> depensesParCategorie) { this.depensesParCategorie = depensesParCategorie; }

    public Map<String, Double> getDepensesParMois() { return depensesParMois; }
    public void setDepensesParMois(Map<String, Double> depensesParMois) { this.depensesParMois = depensesParMois; }
}