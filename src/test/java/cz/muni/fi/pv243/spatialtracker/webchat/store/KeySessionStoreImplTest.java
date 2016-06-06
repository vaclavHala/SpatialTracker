package cz.muni.fi.pv243.spatialtracker.webchat.store;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.websocket.Session;

import org.junit.Test;

public class KeySessionStoreImplTest {

	@Test
	public void addSessionTest() {
		KeySessionStore store = new KeySessionStoreImpl();

		addSampleKeySessions(store);

		assertSampleKeySessions(store);
	}

	@Test
	public void getSessionsTest() {
		KeySessionStore store = new KeySessionStoreImpl();

		List<Session> sessions = store.getSessions("sampleKey");

		assertEquals(sessions.size(), 0);
	}

	@Test
	public void addSessionsWithDifferentKeyTest() {
		KeySessionStore store = new KeySessionStoreImpl();

		addSampleKeySessions(store);
		addDifferentKeySessions(store);

		assertSampleKeySessions(store);
		assertDifferentKeySessions(store);
	}

	private final String sampleKey = "someKey";

	private final static Session session1 = mock(Session.class);
	private final static Session session2 = mock(Session.class);

	private void addSampleKeySessions(KeySessionStore store) {
		store.addSession(sampleKey, session1);
		store.addSession(sampleKey, session2);
	}

	private void assertSampleKeySessions(KeySessionStore store) {
		List<Session> sessions = store.getSessions(sampleKey);

		assertEquals(sessions.size(), 2);
		assertEquals(sessions.get(0), session1);
		assertEquals(sessions.get(1), session2);
	}

	private final String differentKey = "different key";

	private final static Session differentSession = mock(Session.class);

	private void addDifferentKeySessions(KeySessionStore store) {
		store.addSession(differentKey, differentSession);
	}

	private void assertDifferentKeySessions(KeySessionStore store) {
		List<Session> msgs = store.getSessions(differentKey);

		assertEquals(msgs.size(), 1);
		assertEquals(msgs.get(0), differentSession);
	}
}
