package com.inm.resource;

import com.inm.entity.Note;
import com.inm.service.MarkdownService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/editor/{username}")
public class EditorResource {

    @Inject
    MarkdownService markdownService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance editor(String username, String markdown, String renderedHtml);
        public static native TemplateInstance feed(List<Note> notes, String currentUsername);
        public static native TemplateInstance viewPanel(String currentUsername, String noteUsername, String markdown, String renderedHtml);
        public static native TemplateInstance ownPanel(String username, String markdown);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance editor(@PathParam("username") String username) {
        Note note = Note.findByUsername(username);
        String markdown = note != null ? note.markdown : "";
        String rendered = note != null ? note.renderedHtml : "";
        return Templates.editor(username, markdown, rendered);
    }

    @POST
    @Path("/save")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response save(@PathParam("username") String username,
                         @FormParam("markdown") String markdown) {
        Note note = Note.findByUsername(username);
        if (note == null) {
            note = new Note();
            note.username = username;
        }
        note.markdown = markdown;
        note.renderedHtml = markdownService.render(markdown);
        note.persist();
        return Response.ok().build();
    }

    @POST
    @Path("/preview")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String preview(@FormParam("markdown") String markdown) {
        return markdownService.render(markdown != null ? markdown : "");
    }

    @GET
    @Path("/feed")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance feed(@PathParam("username") String username) {
        return Templates.feed(Note.findOthers(username), username);
    }

    @GET
    @Path("/view/{noteUsername}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance view(@PathParam("username") String currentUsername,
                                 @PathParam("noteUsername") String noteUsername) {
        Note note = Note.findByUsername(noteUsername);
        String markdown = note != null ? note.markdown : "";
        String rendered = note != null ? note.renderedHtml : "<em>Nota vazia.</em>";
        return Templates.viewPanel(currentUsername, noteUsername, markdown, rendered);
    }

    @GET
    @Path("/own")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance own(@PathParam("username") String username) {
        Note note = Note.findByUsername(username);
        String markdown = note != null ? note.markdown : "";
        return Templates.ownPanel(username, markdown);
    }
}