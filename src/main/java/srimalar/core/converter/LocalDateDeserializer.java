package srimalar.core.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import srimalar.core.model.FormatConstant;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private final Logger log = LoggerFactory.getLogger(LocalDateDeserializer.class);

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext context) throws IOException {
        String text = jp.getText();
        if (text != null) {
            try {
                return LocalDate.parse(text, FormatConstant.DATE_FORMATTER);
            } catch (DateTimeParseException ex) {
                log.warn("LocalDateDeserializer: format invalid, " + ex.getMessage());
            }
        }
        return null;
    }
}
