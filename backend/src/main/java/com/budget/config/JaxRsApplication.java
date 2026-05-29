package com.budget.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * getProperties() → configuration globale de JAX-RS
     *
     * WildFly par défaut utilise JSON-B pour sérialiser les objets en JSON
     * On force Jackson à la place car :
     * 1. Jackson est plus flexible (supporte Map, LocalDate, etc.)
     * 2. On a configuré Jackson dans JacksonConfig.java
     * 3. Évite le 406 Not Acceptable
     */
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        // Force WildFly à utiliser Jackson au lieu de JSON-B
        props.put("resteasy.preferJacksonOverJsonB", "true");
        return props;
    }
}