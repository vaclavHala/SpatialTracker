package cz.muni.fi.pv243.spatialtracker.webchat;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
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
import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessageEvent;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.WebChatMessageStore;

@Path("/messages/{room}")
@RolesAllowed("USER")
@Stateless
public class WebChatService {

	@Inject
	private WebChatMessageStore messages;

	@Inject
	private Event<NewWebChatMessageEvent> newMessageEvent;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<WebChatMessage> getMessages(@PathParam("room") String roomName) {
		return messages.getMessages(roomName);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void addMessage(NewWebChatMessage newMessage, @PathParam("room") String roomName) {
		WebChatMessage fullMessage = new WebChatMessage();
		fullMessage.name("John Doe");//TODO
		fullMessage.text(newMessage.text());
		fullMessage.created(new Date());
		messages.addMessage(roomName, fullMessage);
		newMessageEvent.fire(new NewWebChatMessageEvent(roomName, fullMessage));
	}
}