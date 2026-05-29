package com.budget.resource;

import java.util.List;

import com.budget.dto.TransactionDTO;
import com.budget.service.TransactionService;

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
import jakarta.ws.rs.core.UriInfo;

@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    private TransactionService transactionService;

    /**
     * @Context HttpHeaders → injecté directement dans le champ
     * RESTEasy injecte le contexte au niveau du champ, pas du paramètre
     */
    @Context
    private HttpHeaders headers;

    @Context
    private UriInfo uriInfo;

    /**
     * Récupère l'userId depuis le contexte de la requête
     * AuthFilter a stocké l'userId comme propriété de la requête
     */
    private Long getUserId() {
        // Récupère le token depuis le header
        String authHeader = headers.getHeaderString("Authorization");
        String token = authHeader.substring(7);
        return com.budget.security.JwtUtil.extraireUserId(token);
    }

    @GET
    public Response lister() {
        Long userId = getUserId();
        List<TransactionDTO> transactions = transactionService.listerParUser(userId);
        return Response.ok(transactions).build();
    }

    @POST
    public Response creer(TransactionDTO dto) {
        try {
            Long userId = getUserId();
            TransactionDTO created = transactionService.creer(userId, dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response modifier(@PathParam("id") Long id, TransactionDTO dto) {
        try {
            Long userId = getUserId();
            TransactionDTO updated = transactionService.modifier(userId, id, dto);
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
            Long userId = getUserId();
            transactionService.supprimer(userId, id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}