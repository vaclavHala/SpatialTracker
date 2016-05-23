package cz.muni.fi.pv243.spatialtracker.webchat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
