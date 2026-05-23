package com.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.budget.entity.Transaction;

/**
 * DTO = Data Transfer Object
 * On n'envoie jamais l'entité directement au frontend
 * car elle contient des relations JPA qui causent des boucles infinies
 * ex: User → Transaction → User → Transaction → ...
 *
 * Ce DTO contient uniquement ce dont le frontend a besoin
 */
public class TransactionDTO {

    private Long id;
    private BigDecimal montant;

    /**
     * On envoie le type en String "DEPENSE" ou "REVENU"
     * plus lisible que l'enum Java
     */
    private String type;
    private String description;
    private LocalDate date;

    /**
     * On envoie juste l'id et le nom de la catégorie
     * pas toute l'entité Category
     */
    private Long categoryId;
    private String categoryNom;

    /**
     * Constructeur qui convertit une entité Transaction en DTO
     * Utilisé dans le service pour retourner les données au frontend
     */
    public TransactionDTO(Transaction t) {
        this.id = t.getId();
        this.montant = t.getMontant();
        this.type = t.getType().name();
        this.description = t.getDescription();
        this.date = t.getDate();
        if (t.getCategory() != null) {
            this.categoryId = t.getCategory().getId();
            this.categoryNom = t.getCategory().getNom();
        }
    }

    // Constructeur vide nécessaire pour Jackson
    public TransactionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryNom() { return categoryNom; }
    public void setCategoryNom(String categoryNom) { this.categoryNom = categoryNom; }
}