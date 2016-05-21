package cz.muni.fi.pv243.spatialtracker.webchat;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.WebChatMessageStore;

@Path("/messages/{room}")
public class WebChatEndpoint {

	@Inject
	private WebChatMessageStore messages;

	@Inject
	private Event<NewWebChatMessage> newMessageEvent;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<WebChatMessage> getMessages(@PathParam("room") String roomName) {
		return messages.getMessages(roomName);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void addMessage(WebChatMessage message, @PathParam("room") String roomName) {
		messages.addMessage(roomName, message);
		newMessageEvent.fire(new NewWebChatMessage(roomName, message));
	}
}