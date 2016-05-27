package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessageEvent;
import cz.muni.fi.pv243.spatialtracker.webchat.store.KeySessionStore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServerEndpoint(value = "/socket/messages/{room}", encoders = { WebChatMessageEncoder.class })
public class WebChatWebSocket {

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

	public void updateClients(@Observes NewWebChatMessageEvent newMessage) {
		String roomName = newMessage.roomName();
		if (roomName == null) {
			throw new NullPointerException("roomName");
		}
		sessions.getSessions(roomName).stream().filter(session -> session.isOpen()).forEach(session -> {
			try {
				session.getBasicRemote().sendObject(newMessage.message());
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		});
	}
}