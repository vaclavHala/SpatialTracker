package cz.muni.fi.pv243.spatialtracker.issues.dto;

import lombok.*;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author opontes
 */
@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class Heat {
    private Coordinates c1;
    private Coordinates c2;
    private Integer value;
}
