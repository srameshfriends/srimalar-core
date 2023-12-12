package srimalar.core.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import srimalar.core.model.FormatConstant;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        String text = jp.getText();
        if (text != null) {
            return LocalDateTime.parse(text, FormatConstant.DATE_TIME_FORMATTER);
        }
        return null;
    }
}
