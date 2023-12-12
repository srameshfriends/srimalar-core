package srimalar.core.model;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface FormatConstant {
    DateTimeFormatter DATE_FORMATTER  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String SCOPE_REQUEST = "request";
    String SCOPE_SESSION = "session";
    String SCOPE_APPLICATION = "application";
}
