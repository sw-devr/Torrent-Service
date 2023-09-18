package main.protocol;

import java.util.Map;

public class ResponseFactory {

    public static SocketResponse createResponse(int code, Map<String,String> header, String message) {

        SocketResponse response = new SocketResponse();
        response.setStatusCode(code);
        response.setHeader(header);
        response.setBody(message);

        return response;
    }
}
