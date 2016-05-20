package cz.muni.fi.pv243.spatialtracker.webchat.store;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

@ApplicationScoped
public class WebChatMessageStore {

    private List<WebChatMessage> messages = Collections.synchronizedList(new LinkedList<>());

    public void addMessage(WebChatMessage message) {
        messages.add(message);
    }
    
    public List<WebChatMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}