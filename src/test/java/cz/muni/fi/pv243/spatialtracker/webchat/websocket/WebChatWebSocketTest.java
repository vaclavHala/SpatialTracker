package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessageEvent;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.KeySessionStore;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.websocket.*;
import java.io.IOException;
import java.util.List;

@RunWith(Arquillian.class)
public class WebChatWebSocketTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(KeySessionStore.class, TestKeySessionStore.class, NewWebChatMessageEvent.class, WebChatMessage.class)
                .addPackages(false, WebChatWebSocket.class.getPackage())
                .addAsLibraries(Maven.resolver()
                        .resolve("org.mockito:mockito-all:2.0.2-beta")
                        .withTransitivity().asFile())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private WebChatWebSocket socket;

    @Inject
    private TestKeySessionStore store;

    private static final  String ROOM_NAME = "testRoom";

    @After
    public void cleanUp() {
        store.clear();
    }

    @Test
    public void onOpenTest() {
        Session session = Mockito.mock(Session.class);

        socket.onOpen(session, ROOM_NAME);

        List<Session> list = store.getSessions(ROOM_NAME);
        Assert.assertEquals(1, store.getAll().size());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(session, list.get(0));
    }

    @Test
    public void onCloseTest() {
        Session session = Mockito.mock(Session.class);
        store.addSession(ROOM_NAME, session);

        socket.onClose(session, ROOM_NAME);

        List<Session> list = store.getSessions(ROOM_NAME);
        Assert.assertEquals(0, store.getAll().size());
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void updateClientsTest() throws IOException, EncodeException {
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.isOpen()).thenReturn(true);
        RemoteEndpoint.Basic basic = Mockito.mock(RemoteEndpoint.Basic.class);
        Mockito.when(session.getBasicRemote()).thenReturn(basic);
        store.addSession(ROOM_NAME, session);

        socket.updateClients(new NewWebChatMessageEvent(ROOM_NAME, new WebChatMessage()));

        Mockito.verify(basic, Mockito.times(1)).sendObject(Mockito.anyObject());
    }
}
