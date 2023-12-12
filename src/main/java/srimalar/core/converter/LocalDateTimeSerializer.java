package srimalar.core.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import srimalar.core.model.FormatConstant;

import java.time.LocalDateTime;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>  {
    private final Logger log = LoggerFactory.getLogger(LocalDateTimeSerializer.class);

    @Override
    public void serialize(LocalDateTime value, JsonGenerator generator, SerializerProvider provider) {
        if (value != null) {
            try {
                generator.writeString(FormatConstant.DATE_TIME_FORMATTER.format(value));
            } catch (Exception ex) {
                log.warn("LocalDateTimeSerializer: format invalid, " + ex.getMessage());
            }
        }
    }
}
