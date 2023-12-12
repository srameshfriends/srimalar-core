package srimalar.core.model;

public class StringValue {
    public static void validate(String text, String message) {
        if(text == null || text.isBlank()) {
            throw new IllegalArgumentException(message == null ? "Text invalid" : message);
        }
    }

    public static boolean equal(String text1, String text2) {
        if(text1 == null || text2 == null) {
            return false;
        }
        return text1.equals(text2);
    }

    public static boolean equalIfNull(String text1, String text2) {
        if(text1 == null && text2 == null) {
            return true;
        }
        return text1 != null && text1.equals(text2);
    }

    public static boolean equalsIgnoreCase(String text1, String text2) {
        if(text1 == null || text2 == null) {
            return false;
        }
        return text1.equalsIgnoreCase(text2);
    }
}
