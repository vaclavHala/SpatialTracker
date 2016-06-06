package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import cz.muni.fi.pv243.spatialtracker.webchat.store.KeySessionStore;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TestKeySessionStore implements KeySessionStore {
    private Map<String, List<Session>> keyToSessions = new HashMap<>();

    public void addSession(String key, Session session) {
        List<Session> sessions = keyToSessions.get(key);
        if (sessions == null) {
            sessions = new LinkedList<>();
            keyToSessions.put(key, sessions);
        }
        sessions.add(session);
    }

    public List<Session> getSessions(String key) {
        List<Session> sessions = keyToSessions.get(key);
        if (sessions == null) {
            return new LinkedList<>();
        }
        return sessions;
    }

    public void removeSession(String key, Session session) {
        List<Session> sessions = keyToSessions.get(key);
        sessions.remove(session);
        if (sessions.size() == 0) {
            keyToSessions.remove(key);
        }
    }

    public void clear() {
        keyToSessions.clear();
    }

    public Map<String, List<Session>> getAll() {
        return keyToSessions;
    }
}
