package cz.muni.fi.pv243.spatialtracker.webchat.store;

import javax.websocket.Session;
import java.util.List;

public interface KeySessionStore {

	void addSession(String key, Session session);

	List<Session> getSessions(String key);

	void removeSession(String key, Session session);
}