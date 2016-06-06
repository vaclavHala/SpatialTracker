package cz.muni.fi.pv243.spatialtracker.webchat.store;

import cz.muni.fi.pv243.spatialtracker.webchat.websocket.WebChatWebSocket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class KeySessionStoreImpl implements  KeySessionStore {

    private Logger log = Logger.getLogger(WebChatWebSocket.class.getName());

    private Map<String, List<Session>> keyToSessions = new HashMap<>();

    public void addSession(String key, Session session) {
        List<Session> sessions;
        synchronized (keyToSessions) {
            sessions = keyToSessions.get(key);
            if (sessions == null) {
                sessions = new LinkedList<>();
                keyToSessions.put(key, sessions);
            }
            sessions.add(session);
        }
    }

    public List<Session> getSessions(String key) {
        List<Session> sessions;
        synchronized (keyToSessions) {
            sessions = keyToSessions.get(key);
        }
        if (sessions == null) {
            log.warning("empty list of sessions for key:" + key);
            return new LinkedList<>();
        }
        return sessions;
    }

    public void removeSession(String key, Session session) {
        synchronized (keyToSessions) {
            List<Session> sessions = keyToSessions.get(key);
            sessions.remove(session);
            if (sessions.size() == 0) {
                keyToSessions.remove(key);
            }
        }
    }
}