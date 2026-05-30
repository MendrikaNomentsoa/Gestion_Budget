package com.budget.dto;

/**
 * Objet reçu quand un utilisateur s'inscrit
 * montantInitial → solde de départ optionnel
 */
public class RegisterRequest {
    private String nom;
    private String email;
    private String password;
    private double montantInitial;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getMontantInitial() { return montantInitial; }
    public void setMontantInitial(double montantInitial) { this.montantInitial = montantInitial; }
}
