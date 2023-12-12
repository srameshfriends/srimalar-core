package srimalar.core.model;

import java.math.BigDecimal;

public class NumberValue {
    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private static final BigDecimal TEN_THOUSAND = BigDecimal.valueOf(10000);

    public static void validateInt(Number number, String message) {
        if(number == null || number.intValue() == 0) {
            throw new IllegalArgumentException(message == null ? "Integer value invalid " : message);
        }
    }

    public static void validateDouble(Number number, String message) {
        if(number == null || number.doubleValue() == 0) {
            throw new IllegalArgumentException(message == null ? "Decimal value invalid " : message);
        }
    }

    public static void validateLong(Number number, String message) {
        if(number == null || number.longValue() == 0) {
            throw new IllegalArgumentException(message == null ? "Long value invalid " : message);
        }
    }

    public static boolean equalInt(int num1, int num2) {
        if(num1 == 0 || num2 == 0) {
            return false;
        }
        return num2 == num1;
    }

    public static boolean equalInt(int num1, int num2, boolean acceptZero) {
        if(acceptZero && num1 == 0 && num2 == 0) {
            return true;
        }
        return num1 != 0 && num1 == num2;
    }

    public static String format(BigDecimal decimal) {
        if (decimal == null || 0 == BigDecimal.ZERO.compareTo(decimal)) {
            return "";
        }
        BigDecimal fractionalValue = decimal.remainder(BigDecimal.ONE);
        String tempText = fractionalValue.toPlainString().substring(2);
        if(1 == tempText.length()) {
            tempText = tempText + "0";
        } else if(tempText.isEmpty()) {
            tempText = "00";
        }
        final String fractional = tempText;
        BigDecimal value = decimal.subtract(fractionalValue);
        String text = value.toPlainString();
        text = text.substring(0, text.indexOf("."));
        if (0 < THOUSAND.compareTo(decimal)) {
            return text + "." + fractional;
        } else if (0 < TEN_THOUSAND.compareTo(decimal)) {
            int cut = text.length() - 3;
            String suffix1 = text.substring(cut);
            String prefix = text.substring(0, cut);
            return prefix + "," + suffix1 + "." + fractional;
        }
        final String suffix = text.substring(text.length() - 3);
        text = text.substring(0, text.length() - 3);
        StringBuilder builder = new StringBuilder();
        int size = text.length();
        while (size > 1) {
            size = size - 2;
            builder.insert(0, text.substring(size)).insert(0, ",");
            text = text.substring(0, size);
        }
        String word = builder.toString();
        if (word.startsWith(",")) {
            word = word.replaceFirst(",", "");
        }
        return word + "," + suffix + "." + fractional;
    }
}
