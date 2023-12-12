package srimalar.core.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class ToStringBuilder {
    private final StringBuilder builder;
    private boolean nullSafe;
    private boolean curlyBrackets;

    public ToStringBuilder() {
        this.builder = new StringBuilder();
        this.nullSafe = true;
        this.curlyBrackets = true;
    }

    public ToStringBuilder(boolean isNullSafe) {
        this.builder = new StringBuilder();
        this.nullSafe = isNullSafe;
        this.curlyBrackets = true;
    }

    public void setNullSafe(boolean nullSafe) {
        this.nullSafe = nullSafe;
    }

    public ToStringBuilder setCurlyBrackets(boolean curlyBrackets) {
        this.curlyBrackets = curlyBrackets;
        return ToStringBuilder.this;
    }

    public ToStringBuilder string(String name, String text) {
        if(text != null) {
            StringBuffer buffer =  new StringBuffer();
            escape(text, buffer);
            builder.append("\"").append(name).append("\": \"").append(buffer).append("\", ");
            return ToStringBuilder.this;
        }
        return nullValue(name);
    }

    /* Performance turning is required */
    private static String escapeCommas(String text) {
        if (text.contains("\"") || text.contains(",")) {
            text = text.replaceAll("\"", "\"\"");
            return text;
        }
        return text;
    }

    static void escape(String s, StringBuffer sb) {
        final int len = s.length();
        for(int i=0;i<len;i++){
            char ch=s.charAt(i);
            switch(ch){
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if(ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF'){
                        String ss=Integer.toHexString(ch);
                        sb.append("\\u");
                        sb.append("0".repeat(4 - ss.length()));
                        sb.append(ss.toUpperCase());
                    }
                    else{
                        sb.append(ch);
                    }
            }
        }
    }

    private ToStringBuilder addValue(String name, String value) {
        builder.append("\"").append(name).append("\": ").append(value).append(", ");
        return ToStringBuilder.this;
    }

    private ToStringBuilder addText(String name, String value) {
        builder.append("\"").append(name).append("\": \"").append(value).append("\", ");
        return ToStringBuilder.this;
    }

    public ToStringBuilder bigDecimal(String name, BigDecimal value) {
        return value != null ? addValue(name, value.toString()) : zero(name);
    }

    public ToStringBuilder doubleValue(String name, Double value) {
        return value != null ? addValue(name, value.toString()) : zero(name);
    }

    public ToStringBuilder longValue(String name, Long value) {
        return value != null ? addValue(name, value.toString()) : zero(name);
    }

    public ToStringBuilder intValue(String name, Integer value) {
        return value != null ? addValue(name, value.toString()) : zero(name);
    }


    public ToStringBuilder boolValue(String name, Boolean value) {
        if(value == null && !nullSafe) {
            builder.append("\"").append(name).append("\": false, ");
        } else if(value != null) {
            builder.append("\"").append(name).append("\": ").append(value).append(", ");
        }
        return ToStringBuilder.this;
    }

    public ToStringBuilder date(String name, Date value) {
        if(value != null) {
            try {
                return addText(name, FormatConstant.DATE_FORMAT.format(value));
            } catch (Exception ex) {
                // ignore
            }
        }
        return nullValue(name);
    }

    public ToStringBuilder dateTime(String name, Date value) {
        if(value != null) {
            try {
                return addText(name, FormatConstant.DATE_TIME_FORMAT.format(value));
            } catch (Exception ex) {
                // ignore
            }
        }
        return nullValue(name);
    }

    public ToStringBuilder localDate(String name, LocalDate value) {
        if(value != null) {
            try {
                return addText(name, value.format(FormatConstant.DATE_FORMATTER));
            } catch (Exception ex) {
                // ignore
            }
        }
        return nullValue(name);
    }

    public ToStringBuilder localDateTime(String name, LocalDateTime value) {
        if(value != null) {
            try {
                return addText(name, value.format(FormatConstant.DATE_TIME_FORMATTER));
            } catch (Exception ex) {
                // ignore
            }
        }
        return nullValue(name);
    }

    public ToStringBuilder nullValue(String name) {
        if(!nullSafe) {
            builder.append("\"").append(name).append("\": null, ");
        }
        return ToStringBuilder.this;
    }

    public ToStringBuilder zero(String name) {
        if(!nullSafe) {
            builder.append("\"").append(name).append("\": 0, ");
        }
        return ToStringBuilder.this;
    }

    @Override
    public String toString() {
        String text = "";
        if(2 < builder.length()) {
            text = builder.substring(0, builder.length() - 2);
        }
        return curlyBrackets ? "{" + text + "}" : text;
    }
}
