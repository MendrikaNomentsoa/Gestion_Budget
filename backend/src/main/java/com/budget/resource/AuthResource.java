package com.budget.resource;

import com.budget.dto.LoginRequest;
import com.budget.dto.RegisterRequest;
import com.budget.service.AuthService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoints d'authentification
 * 
 * @Path("/auth") → toutes les routes commencent par /api/auth
 * @Consumes → accepte du JSON en entrée
 * @Produces → retourne du JSON en sortie
 */
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    /**
     * POST /api/auth/register
     * Inscrit un nouvel utilisateur
     */
    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        try {
            var resultat = authService.inscrire(request);
            return Response.status(Response.Status.CREATED).entity(resultat).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * POST /api/auth/login
     * Connecte un utilisateur existant
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            var resultat = authService.connecter(request);
            return Response.ok(resultat).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"erreur\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}