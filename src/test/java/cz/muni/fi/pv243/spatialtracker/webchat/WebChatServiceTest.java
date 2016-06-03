package cz.muni.fi.pv243.spatialtracker.webchat;


import cz.muni.fi.pv243.spatialtracker.RunAsUser;
import cz.muni.fi.pv243.spatialtracker.users.BasicAuthUtils;
import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.model.NewWebChatMessageEvent;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import cz.muni.fi.pv243.spatialtracker.webchat.store.MemoryWebChatMessageStore;
import cz.muni.fi.pv243.spatialtracker.webchat.store.WebChatMessageStore;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.*;

@RunWith(Arquillian.class)
public class WebChatServiceTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(WebChatService.class, WebChatMessageStore.class, MemoryWebChatMessageStore.class,
                        NewWebChatMessage.class, NewWebChatMessageEvent.class, WebChatMessage.class,
                        BasicAuthUtils.class, RunAsUser.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private WebChatService service;

    @Inject
    private MemoryWebChatMessageStore store;

    @Inject
    private RunAsUser withUserRole;

    @Before
    public void prepare() {
        store.clear();
    }

    private static final String ROOM1_NAME = "room1";
    private static final String ROOM2_NAME = "room2";

    @Test
    public void getMessagesTest_emptyRoom() throws Exception {
        List<WebChatMessage> emptyRoom = withUserRole.call(()->service.getMessages("emptyRoom"));

        Assert.assertEquals(0, emptyRoom.size());
    }

    @Test
    public void getMessagesTest_oneRoomOneMessage() throws Exception {
        final String ONE_ROOM = "oneRoom";
        store.addMessage(ONE_ROOM, createSampleMessages()[0]);

        List<WebChatMessage> oneRoom = withUserRole.call(()->service.getMessages(ONE_ROOM));

        Assert.assertEquals(1, oneRoom.size());
        assertWebChatMessage(createSampleMessages()[0], oneRoom.get(0));
    }

    @Test
    public void getMessagesTest_multipleRooms() throws Exception {
        store.addMessage(ROOM1_NAME, createSampleMessages()[0]);
        store.addMessage(ROOM1_NAME, createSampleMessages()[1]);
        store.addMessage(ROOM2_NAME, createSampleMessages()[2]);
        store.addMessage(ROOM1_NAME, createSampleMessages()[3]);

        List<WebChatMessage> room1 = withUserRole.call(()->service.getMessages(ROOM1_NAME));
        List<WebChatMessage> room2 = withUserRole.call(()->service.getMessages(ROOM2_NAME));

        assertRoom1(room1);
        assertRoom2(room2);
    }

    @Test
    public void addMessageTest() throws Exception {
        final String ROOM_ONE = "roomOne";
        Date before = new Date();
        addNewMessage(createSampleMessages()[0], ROOM_ONE);
        Date after = new Date();

        List<WebChatMessage> roomOne = store.getMessages(ROOM_ONE);
        WebChatMessage expectedMsg = createSampleMessages()[0];
        WebChatMessage actualMsg = roomOne.get(0);
        Assert.assertEquals(1, roomOne.size());
        Assert.assertEquals(expectedMsg.text(), actualMsg.text());
        Assert.assertEquals(expectedMsg.name(), actualMsg.name());
        Assert.assertFalse(actualMsg.created().before(before));
        Assert.assertFalse(actualMsg.created().after(after));
    }

    private WebChatMessage[] createSampleMessages() {
        return new WebChatMessage[]{
                new WebChatMessage("John Doe", "First message", new Date(0)),
                new WebChatMessage("John Doe", "Second message", new Date(100)),
                new WebChatMessage("Sherlock Holmes", "Looking for a secret...", new Date(1000)),
                new WebChatMessage("John Doe", "Last message", new Date(10000)),
        };
    }

    private void assertRoom1(List<WebChatMessage> room1) {
        Assert.assertEquals(room1.size(), 3);
        assertWebChatMessage(createSampleMessages()[0], room1.get(0));
        assertWebChatMessage(createSampleMessages()[1], room1.get(1));
        assertWebChatMessage(createSampleMessages()[3], room1.get(2));
    }

    private void assertRoom2(List<WebChatMessage> room2) {
        Assert.assertEquals(room2.size(), 1);
        assertWebChatMessage(createSampleMessages()[2], room2.get(0));
    }

    private void assertWebChatMessage(WebChatMessage expected, WebChatMessage actual) {
        Assert.assertEquals(expected.text(), actual.text());
        Assert.assertEquals(expected.name(), actual.name());
        Assert.assertEquals(expected.created(), actual.created());
    }

    private void addNewMessage(WebChatMessage msg, String roomName) throws Exception {
        withUserRole.call(()->{
            String authHeader = createAuthHeader(msg.name(), "fakePassword");
            service.addMessage(new NewWebChatMessage(msg.text()), roomName, authHeader);
            return null;
        });
    }

    private String createAuthHeader(String username, String password) {
        byte[] rawAuthHeader = append(new byte[][]{username.getBytes(), ":".getBytes(), password.getBytes()});
        return "Basic "+new String(java.util.Base64.getEncoder().encode(rawAuthHeader));
    }

    private byte[] append(byte[][] arrays) {
        int n = 0;
        for (byte[] array : arrays) {
            n += array.length;
        }
        byte[] result = new byte[n];
        int curr = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, curr, array.length);
            curr += array.length;
        }
        return result;
    }
}
