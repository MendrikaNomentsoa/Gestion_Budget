package com.budget.resource;

import java.util.List;

import com.budget.dto.AlertDTO;
import com.budget.security.JwtUtil;
import com.budget.service.AlertService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoints pour les alertes
 *
 * GET /api/alertes          → toutes les alertes de dépassement
 * GET /api/alertes/mois     → alertes pour un mois précis
 *     ?mois=1&annee=2024
 */
@Path("/alertes")
@Produces(MediaType.APPLICATION_JSON)
public class AlertResource {

    @Inject
    private AlertService alertService;

    @Context
    private HttpHeaders headers;

    private Long getUserId() {
        String token = headers.getHeaderString("Authorization").substring(7);
        return JwtUtil.extraireUserId(token);
    }

    /**
     * Retourne toutes les alertes de dépassement
     */
    @GET
    public Response getAlertes() {
        try {
            List<AlertDTO> alertes = alertService.verifier(getUserId());
            return Response.ok(alertes).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Retourne les alertes pour un mois précis
     * ex: GET /api/alertes/mois?mois=1&annee=2024
     *
     * @QueryParam → récupère les paramètres dans l'URL
     */
    @GET
    @Path("/mois")
    public Response getAlertesParMois(
            @QueryParam("mois") int mois,
            @QueryParam("annee") int annee) {
        try {
            List<AlertDTO> alertes = alertService.verifierParMois(getUserId(), mois, annee);
            return Response.ok(alertes).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}