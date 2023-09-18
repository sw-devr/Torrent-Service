package main.protocol;

public enum ContentType {

    STREAM("steram"),
    JSON("json");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
