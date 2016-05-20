package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.SessionStore;

@ServerEndpoint(value = "/socket/messages", encoders = { WebChatMessageEncoder.class })
public class WebChatWebSocket {

    private Logger log = Logger.getLogger(WebChatWebSocket.class.getName());

    @Inject
    private SessionStore sessions;

    @OnOpen
    public void onOpen(final Session session) {
        sessions.addSession(session);
    }

    @OnClose
    public void onClose(final Session session) {
        sessions.removeSession(session);
    }

    public void updateClients(@Observes WebChatMessage message) {
        sessions.getAllSessions().stream().filter(session -> session.isOpen()).forEach(session -> {
            try {
                session.getBasicRemote().sendObject(message);
            } catch (Exception e) {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        });
    }
}