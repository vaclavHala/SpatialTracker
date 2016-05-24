package cz.muni.fi.pv243.spatialtracker.webchat;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils;
import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessageEvent;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.WebChatMessageStore;

import static cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils.decodeBasicAuthLogin;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

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
	public void addMessage(NewWebChatMessage newMessage, @PathParam("room") String roomName, final @HeaderParam(AUTHORIZATION) String currentUserBasicAuth) {
        BasicAuthUtils.LoginPass auth = decodeBasicAuthLogin(currentUserBasicAuth);
		WebChatMessage fullMessage = new WebChatMessage();
		fullMessage.name(auth.login());
		fullMessage.text(newMessage.text());
		fullMessage.created(new Date());
		messages.addMessage(roomName, fullMessage);
		newMessageEvent.fire(new NewWebChatMessageEvent(roomName, fullMessage));
	}
}