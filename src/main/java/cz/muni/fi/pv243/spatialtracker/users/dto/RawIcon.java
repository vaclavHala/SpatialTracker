
package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * User icon stored as Base64 encoded PNG
 */
@ToString
@Getter
@AllArgsConstructor
@JsonSerialize(using = IconSerializer.class)
@JsonDeserialize(using = IconDeserializer.class)
public class RawIcon {

    private String icon;

}
