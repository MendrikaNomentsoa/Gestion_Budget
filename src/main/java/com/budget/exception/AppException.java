package com.budget.exception;

/**
 * Exception personnalisée pour l'application
 * Permet de distinguer les erreurs métier des erreurs système
 *
 * ex: throw new AppException(404, "Utilisateur introuvable")
 *     throw new AppException(400, "Email déjà utilisé")
 */
public class AppException extends RuntimeException {

    private final int status;

    public AppException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}