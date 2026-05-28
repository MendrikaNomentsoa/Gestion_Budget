package com.budget.exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Intercepte TOUTES les exceptions non gérées dans l'application
 * et retourne une réponse JSON propre au lieu d'une page HTML d'erreur
 *
 * Sans ça, WildFly retourne une page HTML 500 illisible
 * Avec ça, le frontend reçoit toujours du JSON
 *
 * @Provider → JAX-RS applique ce mapper automatiquement
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {

        Map<String, Object> erreur = new HashMap<>();

        // Si c'est une erreur métier connue
        if (e instanceof AppException appEx) {
            erreur.put("erreur", appEx.getMessage());
            erreur.put("status", appEx.getStatus());
            return Response.status(appEx.getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(erreur)
                    .build();
        }

        // Si c'est une erreur inattendue
        erreur.put("erreur", e.getMessage());
        erreur.put("status", 500);

        // Affiche l'erreur dans les logs WildFly
        System.err.println("=== ERREUR NON GEREE : " + e.getMessage());
        e.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(erreur)
                .build();
    }
}