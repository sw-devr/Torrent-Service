package main.client.user.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.protocol.*;
import main.server.user.ResponseUserDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.USER_FIND_URL;

public class UserHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseUserDto getUser(String sessionId) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            SocketRequest request = new SocketRequest();
            request.setUrl(USER_FIND_URL);
            request.setHeader(header);

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                return objectMapper.readValue((String)response.getBody(), ResponseUserDto.class);
            } else {
                throw new IllegalStateException((String)response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
