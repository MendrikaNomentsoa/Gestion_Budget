package com.budget.resource;

import java.util.List;
import java.util.Map;

import com.budget.dto.ObjectifDTO;
import com.budget.security.JwtUtil;
import com.budget.service.ObjectifService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoints pour les objectifs financiers
 *
 * GET    /api/objectifs              → liste tous les objectifs
 * POST   /api/objectifs              → crée un objectif
 * PUT    /api/objectifs/{id}         → modifie un objectif
 * DELETE /api/objectifs/{id}         → supprime un objectif
 * POST   /api/objectifs/{id}/epargne → ajoute un montant épargné
 */
@Path("/objectifs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ObjectifResource {

    @Inject
    private ObjectifService objectifService;

    @Context
    private HttpHeaders headers;

    private Long getUserId() {
        String token = headers.getHeaderString("Authorization").substring(7);
        return JwtUtil.extraireUserId(token);
    }

    @GET
    public Response lister() {
        List<ObjectifDTO> objectifs = objectifService.listerParUser(getUserId());
        return Response.ok(objectifs).build();
    }

    @POST
    public Response creer(ObjectifDTO dto) {
        try {
            ObjectifDTO created = objectifService.creer(getUserId(), dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response modifier(@PathParam("id") Long id, ObjectifDTO dto) {
        try {
            ObjectifDTO updated = objectifService.modifier(getUserId(), id, dto);
            return Response.ok(updated).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response supprimer(@PathParam("id") Long id) {
        try {
            objectifService.supprimer(getUserId(), id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Ajoute un montant épargné vers l'objectif
     * Body: { "montant": 50000 }
     */
    @POST
    @Path("/{id}/epargne")
    public Response ajouterEpargne(@PathParam("id") Long id, Map<String, Double> body) {
        try {
            double montant = body.get("montant");
            ObjectifDTO updated = objectifService.ajouterMontant(getUserId(), id, montant);
            return Response.ok(updated).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Retire un montant de l'objectif
     * Body: { "montant": 50000 }
     */
    @POST
    @Path("/{id}/retrait")
    public Response retirerMontant(@PathParam("id") Long id, Map<String, Double> body) {
        try {
            double montant = body.get("montant");
            ObjectifDTO updated = objectifService.retirerMontant(getUserId(), id, montant);
            return Response.ok(updated).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}