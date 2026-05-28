package com.budget.resource;

import com.budget.dto.StatDTO;
import com.budget.security.JwtUtil;
import com.budget.service.StatService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoint pour les statistiques du dashboard
 *
 * GET /api/stats → retourne toutes les statistiques
 */
@Path("/stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatResource {

    @Inject
    private StatService statService;

    @Context
    private HttpHeaders headers;

    private Long getUserId() {
        String token = headers.getHeaderString("Authorization").substring(7);
        return JwtUtil.extraireUserId(token);
    }

    @GET
    public Response getStats() {
        try {
            StatDTO stats = statService.calculer(getUserId());
            return Response.ok(stats).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}