package cz.muni.fi.pv243.spatialtracker.webchat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewWebChatMessage {

	@JsonProperty("text")
	private String text;
}
