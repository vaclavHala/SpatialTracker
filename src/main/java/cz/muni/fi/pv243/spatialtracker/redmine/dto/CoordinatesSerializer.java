package cz.muni.fi.pv243.spatialtracker.redmine.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.muni.fi.pv243.spatialtracker.redmine.dto.Coordinates;
import java.io.IOException;
import javax.ws.rs.ext.Provider;

public class CoordinatesSerializer extends JsonSerializer<Coordinates> {

    private static final int LAT_ID = 2;
    private static final int LON_ID = 3;

    @Override
    public void serialize(
            final Coordinates coords,
            final JsonGenerator gen,
            final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeFieldName("custom_fields");
        gen.writeStartArray();
        this.writeCustom(LAT_ID, coords.latitude(), gen);
        this.writeCustom(LON_ID, coords.longitude(), gen);
        gen.writeEndArray();
    }

    private void writeCustom(final int id, final Object value, final JsonGenerator gen) throws IOException{
        gen.writeStartObject();
        gen.writeStringField("id", String.valueOf(id));
        gen.writeStringField("value", value.toString());
        gen.writeEndObject();
    }

}
