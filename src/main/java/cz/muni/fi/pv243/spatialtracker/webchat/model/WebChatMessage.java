package cz.muni.fi.pv243.spatialtracker.webchat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

import cz.muni.fi.pv243.spatialtracker.webchat.websocket.WebChatMessageEncoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebChatMessage {

	@JsonProperty("name")
	private String name;

	@JsonProperty("text")
	private String text;

	@JsonProperty("created")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern=WebChatMessageEncoder.DATE_FORMAT_PATTERN, timezone=WebChatMessageEncoder.TIME_ZONE)
	private Date created;
}
