package com.budget.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Un Budget = une limite de dépense définie par l'utilisateur
 * pour une catégorie donnée, pour un mois donné
 * ex: "Je ne veux pas dépenser plus de 100 000 Ar en nourriture en janvier 2024"
 * C'est cette entité qui permet de générer les ALERTES
 */
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La limite maximale de dépense
     * ex: 100000.00 (100 000 Ar)
     */
    @Column(nullable = false)
    private BigDecimal limite;

    /**
     * Le mois concerné (1 = janvier, 12 = décembre)
     * Combiné avec annee, ça cible un mois précis
     */
    @Column(nullable = false)
    private int mois;

    @Column(nullable = false)
    private int annee;

    /**
     * Ce budget appartient à un User spécifique
     * Chaque user gère ses propres budgets
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Ce budget concerne une catégorie spécifique
     * ex: budget pour "Nourriture", budget pour "Transport"
     * C'est la combinaison (user + category + mois + annee)
     * qui définit un budget unique
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getLimite() { return limite; }
    public void setLimite(BigDecimal limite) { this.limite = limite; }

    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}