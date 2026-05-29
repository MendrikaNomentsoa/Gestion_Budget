package com.budget.dto;

import com.budget.entity.Category;

/**
 * DTO pour les catégories
 * ex: { "id": 1, "nom": "Nourriture", "couleur": "#FF5733" }
 */
public class CategoryDTO {

    private Long id;
    private String nom;
    private String couleur;

    // Convertit une entité Category en DTO
    public CategoryDTO(Category c) {
        this.id = c.getId();
        this.nom = c.getNom();
        this.couleur = c.getCouleur();
    }

    public CategoryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
}