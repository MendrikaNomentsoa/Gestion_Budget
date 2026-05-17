package com.budget.config;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * CORS = Cross-Origin Resource Sharing
 * 
 * Problème : React tourne sur http://localhost:3000
 *            WildFly tourne sur http://localhost:8080
 * 
 * Par défaut, le navigateur bloque les requêtes entre deux origines différentes
 * Ce filtre dit au navigateur "c'est OK, autorise React à appeler WildFly"
 * 
 * @Provider → JAX-RS détecte et applique ce filtre automatiquement
 *             sur TOUTES les réponses de l'API
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Autorise React (localhost:3000) à appeler l'API
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");

        // Autorise ces méthodes HTTP
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Autorise ces headers — dont Authorization pour le JWT
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Le navigateur peut mettre en cache cette autorisation pendant 24h
        responseContext.getHeaders().add("Access-Control-Max-Age", "86400");
    }
}