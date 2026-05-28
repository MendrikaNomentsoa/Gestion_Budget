package com.budget.dto;

import com.budget.entity.Budget;

/**
 * DTO pour les budgets
 * ex: { "categoryId": 1, "limite": 100000, "mois": 1, "annee": 2024 }
 */
public class BudgetDTO {

    private Long id;
    private Long categoryId;
    private String categoryNom;
    private double limite;
    private int mois;
    private int annee;

    // Convertit une entité Budget en DTO
    public BudgetDTO(Budget b) {
        this.id = b.getId();
        this.categoryId = b.getCategory().getId();
        this.categoryNom = b.getCategory().getNom();
        this.limite = b.getLimite().doubleValue();
        this.mois = b.getMois();
        this.annee = b.getAnnee();
    }

    public BudgetDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryNom() { return categoryNom; }
    public void setCategoryNom(String categoryNom) { this.categoryNom = categoryNom; }

    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }

    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
}