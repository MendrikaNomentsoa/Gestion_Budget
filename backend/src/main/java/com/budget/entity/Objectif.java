package com.budget.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Un Objectif = un but financier que l'utilisateur veut atteindre
 * ex: "Économiser 500 000 Ar pour les vacances avant le 31/12/2026"
 *
 * montantCible  → combien il veut économiser
 * montantActuel → combien il a déjà mis de côté
 * dateEcheance  → date limite pour atteindre l'objectif
 */
@Entity
@Table(name = "objectifs")
public class Objectif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private BigDecimal montantCible;

    /**
     * Montant déjà économisé — commence à 0
     * L'utilisateur peut l'augmenter manuellement
     */
    @Column(nullable = false)
    private BigDecimal montantActuel = BigDecimal.ZERO;

    @Column
    private LocalDate dateEcheance;

    @Column
    private String description;

    /**
     * Statut de l'objectif
     * EN_COURS → pas encore atteint
     * ATTEINT  → montantActuel >= montantCible
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.EN_COURS;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum Statut {
        EN_COURS, ATTEINT
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public BigDecimal getMontantCible() { return montantCible; }
    public void setMontantCible(BigDecimal montantCible) { this.montantCible = montantCible; }

    public BigDecimal getMontantActuel() { return montantActuel; }
    public void setMontantActuel(BigDecimal montantActuel) { this.montantActuel = montantActuel; }

    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}