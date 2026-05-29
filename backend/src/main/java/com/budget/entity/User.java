package com.budget.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Entity  → dit à JPA que cette classe correspond à une table en base de données
 * @Table   → spécifie le nom de la table (ici "users")
 * Sans @Table, JPA utiliserait le nom de la classe "User" comme nom de table
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * @Id → cette colonne est la clé primaire de la table
     * @GeneratedValue → la valeur est générée automatiquement
     * IDENTITY → PostgreSQL utilise SERIAL (auto-increment)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column → mappe ce champ à une colonne en base
     * nullable = false → la colonne ne peut pas être NULL (obligatoire)
     * unique = true → deux users ne peuvent pas avoir le même email
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Le mot de passe sera haché avec BCrypt avant d'être stocké
     * On ne stocke JAMAIS un mot de passe en clair
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nom;

    /**
     * name = "created_at" → le nom de la colonne en base sera "created_at"
     * Sans name, JPA utiliserait "createdAt" comme nom de colonne
     * On initialise directement à maintenant
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * @OneToMany → un User peut avoir plusieurs Transactions
     * mappedBy = "user" → dit à JPA que la relation est déjà gérée
     *   dans la classe Transaction par le champ "user"
     *   (évite de créer une table de jointure inutile)
     * CascadeType.ALL → si on supprime un User, ses transactions
     *   sont supprimées automatiquement
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Category> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Budget> budgets;

    // ===== GETTERS ET SETTERS =====
    // Nécessaires pour que JPA et JAX-RS puissent lire/écrire les champs
    // JPA utilise les setters pour reconstruire l'objet depuis la base
    // JAX-RS utilise les getters pour convertir l'objet en JSON

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}