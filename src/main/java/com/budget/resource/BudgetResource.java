package com.budget.resource;

import java.util.List;

import com.budget.dto.BudgetDTO;
import com.budget.security.JwtUtil;
import com.budget.service.BudgetService;

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
 * Endpoints pour les budgets
 *
 * GET    /api/budgets        → liste tous les budgets
 * POST   /api/budgets        → crée un budget
 * PUT    /api/budgets/{id}   → modifie un budget
 * DELETE /api/budgets/{id}   → supprime un budget
 */
@Path("/budgets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BudgetResource {

    @Inject
    private BudgetService budgetService;

    @Context
    private HttpHeaders headers;

    private Long getUserId() {
        String token = headers.getHeaderString("Authorization").substring(7);
        return JwtUtil.extraireUserId(token);
    }

    @GET
    public Response lister() {
        List<BudgetDTO> budgets = budgetService.listerParUser(getUserId());
        return Response.ok(budgets).build();
    }

    @POST
    public Response creer(BudgetDTO dto) {
        try {
            BudgetDTO created = budgetService.creer(getUserId(), dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response modifier(@PathParam("id") Long id, BudgetDTO dto) {
        try {
            BudgetDTO updated = budgetService.modifier(getUserId(), id, dto);
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
            budgetService.supprimer(getUserId(), id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}