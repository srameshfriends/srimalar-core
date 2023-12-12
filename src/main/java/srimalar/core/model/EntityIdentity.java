package srimalar.core.model;

public interface EntityIdentity {
    String getId();

    void setId(String id);

    String getKey();

    void setKey(String key);

    String getRev();

    void setRev(String rev);

    boolean isNew();
}
