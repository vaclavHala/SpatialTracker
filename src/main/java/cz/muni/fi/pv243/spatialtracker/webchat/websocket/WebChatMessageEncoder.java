package cz.muni.fi.pv243.spatialtracker.webchat.websocket;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;

public class WebChatMessageEncoder implements Encoder.Text<WebChatMessage> {

	public static final String DATE_FORMAT_PATTERN = "HH:mm:ss dd.MM.yyyyZ";
	public static final String TIME_ZONE = "Europe/Prague";

	private static final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);
	static {
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
	}

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
