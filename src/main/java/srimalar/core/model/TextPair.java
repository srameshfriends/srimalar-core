package srimalar.core.model;


import java.util.Objects;

public  class TextPair {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextPair keyCode = (TextPair) o;
        return Objects.equals(name, keyCode.name) && Objects.equals(value, keyCode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder().string("name", name).string("value", value).toString();
    }
}
