package cz.muni.fi.pv243.spatialtracker.webchat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class NewWebChatMessage {

	@NotNull(message = "{webchat.msg.empty}")
	@JsonProperty("text")
	private String text;
}
