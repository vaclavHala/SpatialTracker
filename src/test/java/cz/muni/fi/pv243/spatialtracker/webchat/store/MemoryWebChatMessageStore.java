package cz.muni.fi.pv243.spatialtracker.webchat.store;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class MemoryWebChatMessageStore implements WebChatMessageStore {

    private Map<String, List<WebChatMessage>> roomToList = new HashMap<>();

    @Override
    public void addMessage(String room, WebChatMessage message) {
        List<WebChatMessage> list = roomToList.get(room);
        if (list == null) {
            list = new ArrayList<>();
            roomToList.put(room, list);
        }
        list.add(message);
    }

    @Override
    public List<WebChatMessage> getMessages(String room) {
        List<WebChatMessage> list = roomToList.get(room);
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    public void clear() {
        roomToList = new HashMap<>();
    }
}

