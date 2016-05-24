package cz.muni.fi.pv243.spatialtracker.issues.redmine;

import cz.muni.fi.pv243.spatialtracker.CustomField;
import cz.muni.fi.pv243.spatialtracker.issues.dto.Coordinates;
import static cz.muni.fi.pv243.spatialtracker.issues.redmine.RedmineCoordinatesMapper.LATITUDE;
import static cz.muni.fi.pv243.spatialtracker.issues.redmine.RedmineCoordinatesMapper.LONGITUDE;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class RedmineCoordinatesMapperTest {

    private final RedmineCoordinatesMapper sut = new RedmineCoordinatesMapper();

    @Test
    public void shouldReadCoordinatesFromCustomFieldsList() throws Exception {
        double lat = 12.3;
        double lon = 75.1;
        List<CustomField> fields = asList(new CustomField(LATITUDE, String.valueOf(lat)),
                                          new CustomField(LONGITUDE, String.valueOf(lon)));
        Coordinates coords = this.sut.readFrom(fields);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(coords.latitude()).isEqualTo(lat);
        softly.assertThat(coords.longitude()).isEqualTo(lon);
        softly.assertAll();
    }

    @Test
    public void shouldIgnoreUnknownFields() throws Exception {
        double lat = 12.3;
        double lon = 75.1;
        //the 2* and 3* are used to make sure we dont hit actual LAT or LON id with the clutter
        List<CustomField> fields = asList(new CustomField(2 * LATITUDE + 2 * LONGITUDE, "clutter"),
                                          new CustomField(LONGITUDE, String.valueOf(lon)),
                                          new CustomField(LATITUDE, String.valueOf(lat)),
                                          new CustomField(3 * LATITUDE + 3 * LONGITUDE, "also unrelated"));
        Coordinates coords = this.sut.readFrom(fields);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(coords.latitude()).isEqualTo(lat);
        softly.assertThat(coords.longitude()).isEqualTo(lon);
        softly.assertAll();
    }

    @Test
    public void shouldRetainExistingCustomFieldsWhenWriting() throws Exception {
        CustomField existing1 = new CustomField(2 * LATITUDE + 2 * LONGITUDE, "hello");
        CustomField existing2 = new CustomField(3 * LATITUDE + 3 * LONGITUDE, "world");
        double lat = 12.3;
        double lon = 75.1;
        List<CustomField> fields = new ArrayList<>(asList(existing1, existing2));
        this.sut.appendTo(fields, new Coordinates(lat, lon));
        assertThat(fields).usingFieldByFieldElementComparator()
                          .containsOnly(existing1, existing2,
                                        new CustomField(LATITUDE, String.valueOf(lat)),
                                        new CustomField(LONGITUDE, String.valueOf(lon)));
    }
}
