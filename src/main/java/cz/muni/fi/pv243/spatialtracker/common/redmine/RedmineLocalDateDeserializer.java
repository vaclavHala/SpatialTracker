package cz.muni.fi.pv243.spatialtracker.common.redmine;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.time.LocalDate;

public class RedmineLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(
            final JsonParser parser,
            final DeserializationContext ctx) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();

        TextNode serializedDate = codec.readTree(parser);
        String[] dateSplit = serializedDate.asText().split("-");
        int year = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[2]);
        return LocalDate.of(year, month, day);
    }
}
