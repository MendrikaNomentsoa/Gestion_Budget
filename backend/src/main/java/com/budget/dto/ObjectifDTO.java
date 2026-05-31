package com.budget.dto;

import java.time.LocalDate;

import com.budget.entity.Objectif;

/**
 * DTO pour les objectifs financiers
 * Inclut le pourcentage de progression calculé automatiquement
 */
public class ObjectifDTO {

    private Long id;
    private String nom;
    private double montantCible;
    private double montantActuel;
    private LocalDate dateEcheance;
    private String description;
    private String statut;

    /**
     * Pourcentage de progression
     * ex: montantActuel=250000, montantCible=500000 → progression=50.0
     */
    private double progression;

    public ObjectifDTO(Objectif o) {
        this.id = o.getId();
        this.nom = o.getNom();
        this.montantCible = o.getMontantCible().doubleValue();
        this.montantActuel = o.getMontantActuel().doubleValue();
        this.dateEcheance = o.getDateEcheance();
        this.description = o.getDescription();
        this.statut = o.getStatut().name();

        // Calcule le pourcentage — max 100%
        if (o.getMontantCible().doubleValue() > 0) {
            this.progression = Math.min(100.0,
                (o.getMontantActuel().doubleValue() / o.getMontantCible().doubleValue()) * 100);
        } else {
            this.progression = 0;
        }
    }

    public ObjectifDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getMontantCible() { return montantCible; }
    public void setMontantCible(double montantCible) { this.montantCible = montantCible; }

    public double getMontantActuel() { return montantActuel; }
    public void setMontantActuel(double montantActuel) { this.montantActuel = montantActuel; }

    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public double getProgression() { return progression; }
    public void setProgression(double progression) { this.progression = progression; }
}