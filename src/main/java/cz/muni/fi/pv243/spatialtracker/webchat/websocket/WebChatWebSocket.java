package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.KeySessionStore;

@ServerEndpoint(value = "/socket/messages/{room}", encoders = { WebChatMessageEncoder.class })
public class WebChatWebSocket {

	private Logger log = Logger.getLogger(WebChatWebSocket.class.getName());

	@Inject
	private KeySessionStore sessions;

	@OnOpen
	public void onOpen(final Session session, @PathParam("room") final String roomName) {
		sessions.addSession(roomName, session);
	}

	@OnClose
	public void onClose(final Session session, @PathParam("room") final String roomName) {
		sessions.removeSession(roomName, session);
	}

	public void updateClients(@Observes NewWebChatMessage newMessage) {
		String roomName = newMessage.getRoomName();
		if (roomName == null) {
			throw new NullPointerException("roomName");
		}
		sessions.getSessions(roomName).stream().filter(session -> session.isOpen()).forEach(session -> {
			try {
				session.getBasicRemote().sendObject(newMessage.getMessage());
			} catch (Exception e) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		});
	}
}