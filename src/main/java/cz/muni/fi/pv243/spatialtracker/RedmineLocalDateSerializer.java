package cz.muni.fi.pv243.spatialtracker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import static java.lang.String.format;
import java.time.LocalDate;

public class RedmineLocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(
            final LocalDate date,
            final JsonGenerator gen,
            final SerializerProvider serializers) throws IOException, JsonProcessingException {
        String serializedDate = format("%d-%d-%d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        gen.writeString(serializedDate);
    }
}
