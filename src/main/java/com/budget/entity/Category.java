package com.budget.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom; // ex: "Nourriture", "Transport", "Loyer"

    /**
     * Couleur pour l'affichage dans le dashboard React
     * ex: "#FF5733" — pas obligatoire donc pas de nullable = false
     */
    @Column
    private String couleur;

    /**
     * @ManyToOne → plusieurs categories peuvent appartenir à un seul User
     * @JoinColumn → crée la colonne "user_id" dans la table categories
     *   c'est la clé étrangère qui pointe vers la table users
     * C'est ici que la relation est "possédée" (pas dans User)
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Une catégorie peut avoir plusieurs transactions
     * mappedBy = "category" → la relation est gérée dans Transaction
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}