package main.protocol;

import java.util.Map;

public class SocketResponse {

    public static final int STATUS_CODE_BYTE_SIZE = 4;
    public static final int HEADER_BYTE_SIZE = 4;
    public static final int BODY_BYTE_SIZE = 4;
    public static final int MAX_ALLOWED_HEADER_SIZE = 1024;
    public static final int MAX_ALLOWED_BODY_SIZE = 1024;

    private int headerSize;
    private int bodySize;
    private int statusCode;
    private Map<String, String> header;
    private Object body;


    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public int getBodySize() {
        return bodySize;
    }

    public void setBodySize(int bodySize) {
        this.bodySize = bodySize;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "SocketResponse{" +
                "headerSize=" + headerSize +
                ", bodySize=" + bodySize +
                ", statusCode=" + statusCode +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
