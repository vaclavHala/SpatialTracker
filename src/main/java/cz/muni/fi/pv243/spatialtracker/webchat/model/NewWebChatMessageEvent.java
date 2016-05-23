package cz.muni.fi.pv243.spatialtracker.webchat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class NewWebChatMessageEvent {
	private String roomName;
	private WebChatMessage message;
}
