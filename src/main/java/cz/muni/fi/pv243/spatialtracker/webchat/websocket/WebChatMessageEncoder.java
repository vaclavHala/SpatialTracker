package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import java.text.SimpleDateFormat;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

public class WebChatMessageEncoder implements Encoder.Text<WebChatMessage> {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	@Override
	public void destroy() {
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		
	}

	@Override
	public String encode(WebChatMessage arg) throws EncodeException {
		return Json.createObjectBuilder()
				.add("name", arg.name())
				.add("text", arg.text())
				.add("created", formatter.format(arg.created()))
				.build().toString();
	}

}
