package com.budget.resource;

import java.util.List;

import com.budget.dto.CategoryDTO;
import com.budget.security.JwtUtil;
import com.budget.service.CategoryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoints pour les catégories
 *
 * GET    /api/categories        → liste toutes les catégories
 * POST   /api/categories        → crée une catégorie
 * DELETE /api/categories/{id}   → supprime une catégorie
 */
@Path("/categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    private CategoryService categoryService;

    @Context
    private HttpHeaders headers;

    private Long getUserId() {
        String token = headers.getHeaderString("Authorization").substring(7);
        return JwtUtil.extraireUserId(token);
    }

    @GET
    public Response lister() {
        List<CategoryDTO> categories = categoryService.listerParUser(getUserId());
        return Response.ok(categories).build();
    }

    @POST
    public Response creer(CategoryDTO dto) {
        try {
            CategoryDTO created = categoryService.creer(getUserId(), dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
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
            categoryService.supprimer(getUserId(), id);
            return Response.noContent().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}