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

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * BigDecimal pour les montants d'argent
     * Ne jamais utiliser float ou double pour l'argent
     * car ils ont des erreurs d'arrondi (ex: 0.1 + 0.2 = 0.30000000000000004)
     * BigDecimal est exact
     */
    @Column(nullable = false)
    private BigDecimal montant;

    /**
     * @Enumerated(EnumType.STRING) → stocke "DEPENSE" ou "REVENU" en base
     * Sans cette annotation, JPA stockerait 0 ou 1 (moins lisible)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column
    private String description; // optionnel, ex: "Courses Shoprite"

    /**
     * LocalDate → date sans heure (on a juste besoin du jour)
     * ex: 2024-01-15
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Chaque transaction appartient à un seul User
     * nullable = false → une transaction sans user est impossible
     * C'est ce qui isole les données entre utilisateurs
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Une transaction appartient à une catégorie (optionnel)
     * ex: "Nourriture", "Transport"
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Enum interne à la classe Transaction
     * Deux types possibles : dépense ou revenu
     */
    public enum Type {
        DEPENSE, REVENU
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}