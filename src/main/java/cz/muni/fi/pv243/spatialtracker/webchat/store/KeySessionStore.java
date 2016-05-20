package cz.muni.fi.pv243.spatialtracker.webchat.store;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.Session;

import cz.muni.fi.pv243.spatialtracker.webchat.websocket.WebChatWebSocket;

@ApplicationScoped
public class KeySessionStore {

	private Logger log = Logger.getLogger(WebChatWebSocket.class.getName());

	private Map<String, List<Session>> keyToSessions = new HashMap<>();

	public void addSession(String key, Session session) {
		List<Session> sessions;
		synchronized (keyToSessions) {
			sessions = keyToSessions.get(key);
			if (sessions == null) {
				sessions = new LinkedList<Session>();
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
			return new LinkedList<Session>();
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