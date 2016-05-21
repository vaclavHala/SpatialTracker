package cz.muni.fi.pv243.spatialtracker.webchat.model;

public class NewWebChatMessage {
	private String roomName;
	private WebChatMessage message;

	public NewWebChatMessage(String roomName, WebChatMessage message) {
		this.roomName = roomName;
		this.message = message;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public WebChatMessage getMessage() {
		return message;
	}

	public void setMessage(WebChatMessage message) {
		this.message = message;
	}

}
