package com.budget.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilitaire pour générer et vérifier les tokens JWT
 * 
 * Un token JWT ressemble à ça :
 * eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.abc123
 * 
 * Il est composé de 3 parties séparées par des points :
 * 1. Header  → algorithme utilisé (HS256)
 * 2. Payload → données (userId, expiration)
 * 3. Signature → garantit que le token n'a pas été modifié
 */
public class JwtUtil {

    /**
     * Clé secrète pour signer les tokens
     * En production, mets cette valeur dans une variable d'environnement
     * Elle doit faire au moins 32 caractères pour HS256
     */
    private static final String SECRET = "budget-app-secret-key-2024-super-secure";

    /**
     * Durée de validité du token : 24 heures en millisecondes
     * Après 24h, l'utilisateur devra se reconnecter
     */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;

    /**
     * Génère la clé de signature à partir du SECRET
     */
    private static SecretKey getCle() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Génère un token JWT pour un utilisateur connecté
     * 
     * @param userId l'id de l'utilisateur connecté
     * @return le token JWT sous forme de String
     */
    public static String generer(Long userId) {
        return Jwts.builder()
                // "subject" = l'identifiant principal du token (ici userId)
                .subject(String.valueOf(userId))
                // date d'émission du token
                .issuedAt(new Date())
                // date d'expiration = maintenant + 24h
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                // signature avec la clé secrète
                .signWith(getCle())
                .compact();
    }

    /**
     * Extrait l'userId depuis un token JWT
     * 
     * @param token le token JWT reçu dans le header Authorization
     * @return l'userId contenu dans le token
     */
    public static Long extraireUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(getCle())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }

    /**
     * Vérifie si un token est valide (signature correcte + non expiré)
     * 
     * @param token le token à vérifier
     * @return true si le token est valide
     */
    public static boolean estValide(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getCle())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            // Token expiré, signature invalide, ou token malformé
            return false;
        }
    }
}