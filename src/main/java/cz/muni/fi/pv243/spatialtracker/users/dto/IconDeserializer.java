package cz.muni.fi.pv243.spatialtracker.users.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;

public class IconDeserializer extends JsonDeserializer<RawIcon> {

    @Override
    public RawIcon deserialize(
            final JsonParser parser,
            final DeserializationContext ctx) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();

        //ignore custom_fields name
        codec.readTree(parser);

        ArrayNode fields = codec.readTree(parser);
        RawIcon icon = null;
        for (JsonNode field : fields) {
            //there is just one field there
            icon = new RawIcon(field.findValue("value").asText());
        }
        return icon; // stupid javac
    }
}
