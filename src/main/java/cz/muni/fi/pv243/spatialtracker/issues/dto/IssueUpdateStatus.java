
package cz.muni.fi.pv243.spatialtracker.issues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.pv243.spatialtracker.issues.IssueStatus;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;
import static lombok.AccessLevel.PRIVATE;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class IssueUpdateStatus {

    @NotNull(message = "{issue.status.empty}")
    @JsonProperty("status")
    private IssueStatus status;

}
