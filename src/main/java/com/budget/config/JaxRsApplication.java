package com.budget.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Point d'entrée de toute l'API REST
 * @ApplicationPath("/api") → toutes les routes commencent par /api
 * ex: /api/auth/login, /api/transactions, /api/categories
 * 
 * Cette classe dit à WildFly "l'API REST commence ici"
 * Elle hérite de Application qui est la classe de base JAX-RS
 */
@ApplicationPath("/api")
public class JaxRsApplication extends Application {
    // Vide — JAX-RS scanne automatiquement les @Path dans le projet
}