package com.budget.dto;

/**
 * Objet reçu quand un utilisateur se connecte
 * Le frontend envoie ce JSON :
 * {
 *   "email": "damh@gmail.com",
 *   "password": "monMotDePasse"
 * }
 */
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}