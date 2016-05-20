package cz.muni.fi.pv243.spatialtracker.webchat;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.WebChatMessageStore;

@Path("/messages")
public class WebChatEndpoint {

    @Inject
    private WebChatMessageStore messages;

    @Inject
    private Event<WebChatMessage> messageEvent;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<WebChatMessage> getMessages() {
        return messages.getMessages();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addMessage(WebChatMessage message) {
        messages.addMessage(message);
        messageEvent.fire(message);
    }
}