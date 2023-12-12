package srimalar.core.model;

import java.text.MessageFormat;
import java.util.Locale;

public class MessageCache {
    private final String message;
    private final Locale locale;

    private volatile MessageFormat cachedFormat;

    public MessageCache(String message, Locale locale) {
        this.message = message;
        this.locale = locale;
    }

    public String getMessage() {
        return this.message;
    }

    public MessageFormat getMessageFormat() {
        MessageFormat messageFormat = this.cachedFormat;
        if (messageFormat == null) {
            messageFormat = new MessageFormat(this.message, locale);
            this.cachedFormat = messageFormat;
        }
        return messageFormat;
    }

    public String toString() {
        return this.message;
    }
}
