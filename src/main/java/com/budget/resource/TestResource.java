package com.budget.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Endpoint de test — à supprimer après vérification
 * GET /api/test → retourne {"status": "ok"}
 */
@Path("/test")
public class TestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response test() {
        return Response.ok("{\"status\": \"ok\", \"message\": \"WildFly fonctionne\"}").build();
    }
}