package srimalar.core.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import srimalar.core.model.MessageCache;
import srimalar.core.model.MessageEntity;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBundle {
    private static final Logger log = LoggerFactory.getLogger(MessageBundle.class);

    public static class MessageHashMap extends ConcurrentHashMap<String, String> {

        public MessageHashMap() {
        }

        public MessageHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public String put(String key, String value) {
            if(key != null && value != null) {
                if(super.containsKey(key)) {
                    String text = "Message property (" + key + ") duplicate not allowed. ("
                            + super.get(key) + ") : (" + value + ") existing value (" + super.get(key) + ")";
                    log.warn(text);
                    return text;
                }
                return super.put(key, value);
            }
            return value;
        }
    }
    private static final int SIZE = 2;
    private static final HashSet<String> applicationSet = new HashSet<>();
    private static final ConcurrentHashMap<String, Map<String, String>> messageMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Map<Locale, MessageCache>> formatMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Locale> localeMap = new ConcurrentHashMap<>();
    private static Locale contextLocale;

    private static String language;

    public static String getLanguage() {
        if(language == null) {
            language = getLocale().getLanguage();
        }
        return language;
    }

    public static void setContextLocale(Locale contextLocale) {
        MessageBundle.contextLocale = contextLocale;
    }

    private static Locale forLanguageTag(String lang) {
        if(!localeMap.containsKey(lang)) {
            Locale locale = Locale.forLanguageTag(lang);
            if(locale != null) {
                localeMap.put(lang, locale);
            }

        }
        return localeMap.get(lang);
    }

    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        Map<Locale, MessageCache> localeMap = formatMap.get(code);
        if (localeMap == null) {
            return code;
        } else {
            MessageCache holder = localeMap.get(locale);
            return holder == null ? null : holder.getMessage();
        }
    }

    protected MessageFormat resolveCode(String code, Locale locale) {
        Map<Locale, MessageCache> localeMap = formatMap.get(code);
        if (localeMap == null) {
            MessageCache holder = new MessageCache(code, locale);
            return holder.getMessageFormat();
        } else {
            MessageCache holder = localeMap.get(locale);
            return holder == null ? null : holder.getMessageFormat();
        }
    }

    public static void setAppMessages(String appName, List<? extends MessageEntity> msgList) {
        if(msgList == null) {
            return;
        }
        applicationSet.add(appName);
        msgList.forEach(msgProp -> {
            if (!messageMap.containsKey(msgProp.getName())) {
                messageMap.put(msgProp.getName(), new MessageHashMap(SIZE));
            }
            messageMap.get(msgProp.getName()).put(msgProp.getLocale(), msgProp.getValue());
        });
    }

    public static boolean containsAppName(String appName) {
        return applicationSet.contains(appName);
    }

    public static Locale getLocale() {
        if(contextLocale == null) {
            contextLocale = Locale.getDefault();
        }
        return contextLocale;
    }

    public static String getMessage(String code) {
        Map<String, String> map = messageMap.get(code);
        if(map == null || !map.containsKey(getLanguage())) {
            return code;
        }
        return map.get(getLanguage());
    }

    public static String getMessage(String code, Object... args) {
        String msg = getMessage(code);
        return args == null ? msg : String.format(msg, args);
    }

    public static String getLocaleMessage(String code, Locale locale) {
        Map<String, String> map = messageMap.get(code);
        if(map == null || !map.containsKey(locale.getLanguage())) {
            return code;
        }
        return map.get(locale.getLanguage());
    }

    public static String getLocaleMessage(String code, Locale locale, Object... args) {
        String msg = getLocaleMessage(code, locale);
        return args == null ? msg : String.format(msg, args);
    }

    public static void add(String code, Locale locale, String msg) {
        if(code == null) {
           throw new NullPointerException("Code must not be null");
        }
        if(locale == null) {
            throw new NullPointerException("Locale must not be null");
        }
        if(msg == null) {
            throw new NullPointerException("Message must not be null");
        }
        if (!messageMap.containsKey(code)) {
            messageMap.put(code, new ConcurrentHashMap<>(SIZE));
        }
        messageMap.get(code).remove(locale.getLanguage());
        messageMap.get(code).put(locale.getLanguage(), msg);
        (formatMap.computeIfAbsent(code, (key) -> new ConcurrentHashMap<>(SIZE))).put(locale, new MessageCache(msg, locale));
        if (log.isDebugEnabled()) {
            log.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }

    private static void put(String code, Locale locale, String msg) {
        if (!messageMap.containsKey(code)) {
            messageMap.put(code, new ConcurrentHashMap<>(SIZE));
        }
        messageMap.get(code).remove(locale.getLanguage());
        messageMap.get(code).put(locale.getLanguage(), msg);
        (formatMap.computeIfAbsent(code, (key) -> new ConcurrentHashMap<>(SIZE))).put(locale, new MessageCache(msg, locale));
        if (log.isDebugEnabled()) {
            log.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }

    public static void add(String code, String msg) {
        if(code == null) {
            throw new NullPointerException("Code must not be null");
        }
        if(msg == null) {
            throw new NullPointerException("Message must not be null");
        }
        (formatMap.computeIfAbsent(code, (key) -> new ConcurrentHashMap<>(SIZE))).put(getLocale(), new MessageCache(msg, getLocale()));
        if (log.isDebugEnabled()) {
            log.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + getLocale() + "]");
        }
    }

    public static void add(Map<String, String> messages, Locale locale) {
        if(messages == null) {
            throw new NullPointerException("Messages Map must not be null");
        }
        messages.forEach((code, msg) -> {
            if(code != null && msg != null) {
                put(code, locale, msg);
            }
        });
    }

    public static Map<String, String> getMessageMap(String lang) {
        String locale = lang == null || lang.trim().length() != 2 ? language : lang.trim();
        Map<String, String> resultMap = new MessageHashMap();
        messageMap.forEach((str, stringMap) -> resultMap.put(str, stringMap.get(locale)));
        return resultMap;
    }
}