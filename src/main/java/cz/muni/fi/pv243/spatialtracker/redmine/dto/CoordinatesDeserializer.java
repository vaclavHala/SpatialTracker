package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.Coordinates;
import java.io.IOException;
import static java.lang.String.format;

public class CoordinatesDeserializer extends JsonDeserializer<Coordinates> {

    private static final int LAT_ID = 2;
    private static final int LON_ID = 3;

    @Override
    public Coordinates deserialize(
            final JsonParser parser,
            final DeserializationContext ctx) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();

        //ignore field name
        codec.readTree(parser);

        ArrayNode fields = codec.readTree(parser);
        CoordinatesBuilder builder = new CoordinatesBuilder();
        for (JsonNode field : fields) {
            this.readField(field, builder);
        }
        System.out.println("DONE "+builder.build());
        return builder.build();
    }

    private void readField(final JsonNode custom, final CoordinatesBuilder builder) {
        int id = custom.findValue("id").asInt();
        switch (id) {
            case LAT_ID :
                builder.lat = custom.findValue("value").asDouble();
                return;
            case LON_ID :
                builder.lon = custom.findValue("value").asDouble();
                return;
        }
    }

    private static class CoordinatesBuilder {

        private Double lat;
        private Double lon;

        private Coordinates build() {
            if (this.lat == null || this.lon == null) {
                throw new IllegalStateException(format("Coordinates missing fields, got lat=$f; lon=$f",
                                                       this.lat, this.lon));
            }
            return new Coordinates(this.lat, this.lon);
        }
    }
}
