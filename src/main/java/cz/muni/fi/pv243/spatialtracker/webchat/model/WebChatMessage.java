package cz.muni.fi.pv243.spatialtracker.webchat.model;

public class WebChatMessage {
	private String name;

	public WebChatMessage() {

	}

	public WebChatMessage(String name, String text) {
		this.name = name;
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "WebChatMessage [message=" + text + "]";
	}
}
