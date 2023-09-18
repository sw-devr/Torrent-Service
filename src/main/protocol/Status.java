package main.protocol;

public enum Status {

    SUCCESS(200),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
