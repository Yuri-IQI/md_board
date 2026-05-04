package com.inm.resource;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class JoinResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return Templates.index();
    }

    @POST
    @Path("/join")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response join(@FormParam("username") String username) {
        if (username == null || username.isBlank()) {
            return Response.seeOther(URI.create("/")).build();
        }
        return Response.seeOther(URI.create("/editor/" + username.trim())).build();
    }
}