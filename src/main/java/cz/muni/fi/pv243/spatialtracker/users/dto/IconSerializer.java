package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.muni.fi.pv243.spatialtracker.users.dto.RawIcon;
import java.io.IOException;

public class IconSerializer extends JsonSerializer<RawIcon> {

    private static final int ICON_ID = 4;

    @Override
    public void serialize(
            final RawIcon icon,
            final JsonGenerator gen,
            final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeFieldName("custom_fields");
        gen.writeStartArray();
        this.writeCustom(ICON_ID, icon.icon(), gen);
        gen.writeEndArray();
    }

    private void writeCustom(final int id, final Object value, final JsonGenerator gen) throws IOException{
        gen.writeStartObject();
        gen.writeStringField("id", String.valueOf(id));
        gen.writeStringField("value", value.toString());
        gen.writeEndObject();
    }

}
