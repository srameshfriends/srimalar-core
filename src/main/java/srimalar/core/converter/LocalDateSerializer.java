package srimalar.core.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import srimalar.core.model.FormatConstant;

import java.time.LocalDate;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateSerializer.class);

    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) {
        if (value != null) {
            try {
                generator.writeString(FormatConstant.DATE_FORMATTER.format(value));
            } catch (Exception ex) {
                log.warn("LocalDateSerializer: serialize, " + ex.getMessage());
            }
        }
    }
}
