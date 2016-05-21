package cz.muni.fi.pv243.spatialtracker;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MulticauseError extends Exception {

    private final List<String> errors;

    public MulticauseError(final List<String> errors) {
        super();
        this.errors = new ArrayList<>(errors);
    }
}
