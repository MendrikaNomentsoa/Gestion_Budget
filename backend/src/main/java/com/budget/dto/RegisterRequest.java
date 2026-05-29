package com.budget.dto;

/**
 * Objet reçu quand un utilisateur s'inscrit
 * Le frontend envoie ce JSON :
 * {
 *   "nom": "Damh",
 *   "email": "damh@gmail.com",
 *   "password": "monMotDePasse"
 * }
 */
public class RegisterRequest {
    private String nom;
    private String email;
    private String password;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}