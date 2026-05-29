package com.budget.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour gérer les mots de passe
 * On utilise BCrypt qui est l'algorithme standard pour hacher les mots de passe
 * 
 * Hacher = transformer "monMotDePasse" en "$2a$10$xyz..."
 * C'est irréversible — on ne peut pas retrouver le mot de passe original
 * Pour vérifier, on rehache et on compare
 */
public class PasswordUtil {

    /**
     * Hache un mot de passe avant de le stocker en base
     * ex: hacher("1234") → "$2a$10$abcdefgh..."
     * 
     * @param motDePasse le mot de passe en clair
     * @return le mot de passe haché
     */
    public static String hacher(String motDePasse) {
        // gensalt() génère un "sel" aléatoire — rend chaque hash unique
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt());
    }

    /**
     * Vérifie si un mot de passe correspond au hash stocké
     * ex: verifier("1234", "$2a$10$abcdefgh...") → true
     * 
     * @param motDePasse le mot de passe en clair saisi par l'utilisateur
     * @param hash le hash stocké en base
     * @return true si le mot de passe est correct
     */
    public static boolean verifier(String motDePasse, String hash) {
        return BCrypt.checkpw(motDePasse, hash);
    }
}