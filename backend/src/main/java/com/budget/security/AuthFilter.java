package com.budget.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Filtre de sécurité — vérifie le JWT sur chaque requête protégée
 * 
 * Fonctionne ainsi :
 * 1. Client envoie une requête avec header "Authorization: Bearer eyJ..."
 * 2. Ce filtre intercepte la requête AVANT qu'elle arrive au Resource
 * 3. Si le token est valide → la requête continue normalement
 * 4. Si le token est invalide ou absent → on retourne 401 Unauthorized
 * 
 * @Provider → JAX-RS applique ce filtre automatiquement
 * @Priority → ce filtre s'exécute avant les autres (authentification en premier)
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    
    @Override
    public void filter(ContainerRequestContext requestContext) {

        String chemin = requestContext.getUriInfo().getPath();
    
    // Affiche le chemin dans les logs WildFly
    System.out.println("=== CHEMIN RECU : " + chemin);

    if (chemin.startsWith("/auth/") || chemin.equals("/test")) {
        return;
    }

       

        // Récupère le header Authorization
        String authHeader = requestContext.getHeaderString("Authorization");

        /**
         * Le header doit être présent et commencer par "Bearer "
         * ex: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Bloque la requête avec 401 Unauthorized
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"erreur\": \"Token manquant\"}")
                        .build()
            );
            return;
        }

        // Extrait le token en enlevant "Bearer "
        String token = authHeader.substring(7);

        // Vérifie si le token est valide
        if (!JwtUtil.estValide(token)) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"erreur\": \"Token invalide ou expiré\"}")
                        .build()
            );
            return;
        }

        /**
         * Token valide — on stocke l'userId dans le contexte de la requête
         * Les Resources pourront le récupérer avec :
         * requestContext.getProperty("userId")
         */
        Long userId = JwtUtil.extraireUserId(token);
        requestContext.setProperty("userId", userId);
    }
}