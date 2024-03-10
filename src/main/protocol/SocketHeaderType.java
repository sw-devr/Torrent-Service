package main.protocol;

public enum SocketHeaderType {

    CONTENT_TYPE("content_type"),
    SESSION_ID("session_id"),
    DOWNLOAD_AUTHORITY_TOKEN("download_authority_token"),
    UPLOAD_PATH_URL("upload_path_url"),
    DOWNLOAD_PATH_URL("download_path_url");

    private final String value;

    SocketHeaderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
