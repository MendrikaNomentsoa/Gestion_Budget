package com.budget.dto;

/**
 * DTO pour les alertes de dépassement de budget
 * ex: "Tu as dépassé ton budget Nourriture de 25 000 Ar"
 */
public class AlertDTO {

    private String categorie;
    private double limite;
    private double depenseActuelle;
    private double depassement;
    private String message;

    public AlertDTO(String categorie, double limite, double depenseActuelle) {
        this.categorie = categorie;
        this.limite = limite;
        this.depenseActuelle = depenseActuelle;
        // Calcule le montant dépassé
        this.depassement = depenseActuelle - limite;
        // Génère le message automatiquement
        this.message = "Tu as dépassé ton budget " + categorie
                + " de " + String.format("%.0f", depassement) + " Ar";
    }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }

    public double getDepenseActuelle() { return depenseActuelle; }
    public void setDepenseActuelle(double depenseActuelle) { this.depenseActuelle = depenseActuelle; }

    public double getDepassement() { return depassement; }
    public void setDepassement(double depassement) { this.depassement = depassement; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}