package srimalar.core.model;


public interface MessageEntity extends EntityIdentity {

    String getLocale();

    void setLocale(String locale);

    String getName();

    void setName(String name);

    String getValue();

    void setValue(String value);
}
