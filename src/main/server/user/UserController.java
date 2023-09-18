package main.server.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;

import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.SESSION_ID;
import static main.server.common.CommonConstants.USER_SERVICE;

public class UserController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService = USER_SERVICE;


    @Mapping("/user/login")
    public SocketResponse login(SocketRequest request) {

        try{
            validateSession(request.getHeader().get(SESSION_ID.getValue()));
            String sessionId = userService.login(objectMapper.readValue((String)request.getBody(), RequestLoginDto.class));

            request.getHeader().put(SESSION_ID.getValue(), sessionId);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "로그인 성공");
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }



    @Mapping("/user/logout")
    public SocketResponse logout(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateSession(sessionId);
            userService.logout(sessionId);

            request.getHeader().remove(SESSION_ID.getValue());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "로그 아웃 성공");
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/user/join")
    public SocketResponse join(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateSession(sessionId);
            userService.join(objectMapper.readValue((String)request.getBody(), RequestJoinDto.class));

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "회원 가입 성공");
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }

    }

    @Mapping("/user/delete")
    public SocketResponse delete(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateSession(sessionId);
            userService.remove(sessionId);
            request.getHeader().remove(SESSION_ID.getValue());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "계정 삭제 성공");
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
    }


    private void validateSession(String sessionId) {

        if(userService.isLogin(sessionId)) {
            throw new IllegalArgumentException("이미 로그인 되어있습니다.");
        }
    }
}
