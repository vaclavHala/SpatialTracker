package cz.muni.fi.pv243.spatialtracker.common.redmine;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonProperty;
import static lombok.AccessLevel.PRIVATE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = PRIVATE)
@JsonInclude(NON_NULL)
public class CustomField {

    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;

    public CustomField(final int id, final String value) {
        this.id = id;
        this.value = value;
    }
}
