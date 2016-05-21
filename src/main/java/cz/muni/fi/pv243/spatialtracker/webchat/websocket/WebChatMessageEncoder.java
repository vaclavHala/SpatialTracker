package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

public class WebChatMessageEncoder implements Encoder.Text<WebChatMessage> {

	@Override
	public void destroy() {
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		
	}

	@Override
	public String encode(WebChatMessage arg) throws EncodeException {
		return Json.createObjectBuilder()
				.add("name", arg.getName())
				.add("text", arg.getText())
				.build().toString();
	}

}
