package cz.muni.fi.pv243.spatialtracker.webchat.store;

import java.util.List;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

public interface WebChatMessageStore {
    /**
     * Adds the given message to the specified room.
     * @param room room name
     * @param message message to be added to the specified room
     */
    void addMessage(String room, WebChatMessage message);

    /**
     * Returns list of messages in the specified room.
     * @param room room name
     * @return list of messages associated with the specified room
     */
    List<WebChatMessage> getMessages(String room);
}