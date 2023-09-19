package main.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;

import static main.protocol.ProtocolConstants.USER_FIND_URL;
import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.SESSION_ID;
import static main.server.common.CommonConstants.USER_SERVICE;

public class UserController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService = USER_SERVICE;


    @Mapping("/user/login")
    public SocketResponse login(SocketRequest request) {

        try{
            validateNotExistingSession(request.getHeader().get(SESSION_ID.getValue()));
            String sessionId = userService.login(objectMapper.readValue((String)request.getBody(), RequestLoginDto.class));

            request.getHeader().put(SESSION_ID.getValue(), sessionId);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "로그인 성공");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }



    @Mapping("/user/logout")
    public SocketResponse logout(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateExistingSession(sessionId);
            userService.logout(sessionId);

            request.getHeader().remove(SESSION_ID.getValue());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "로그 아웃 성공");
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping(USER_FIND_URL)
    public SocketResponse find(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateExistingSession(sessionId);
            ResponseUserDto user = userService.find(sessionId);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), objectMapper.writeValueAsString(user));
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }

    }

    @Mapping("/user/join")
    public SocketResponse join(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateNotExistingSession(sessionId);
            String requestBodyString = (String)(request.getBody());
            System.out.println(requestBodyString);
            userService.join(objectMapper.readValue(requestBodyString, RequestJoinDto.class));

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "회원 가입 성공");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }

    }

    @Mapping("/user/delete")
    public SocketResponse delete(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateExistingSession(sessionId);
            userService.remove(sessionId);
            request.getHeader().remove(SESSION_ID.getValue());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "계정 삭제 성공");
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
    }


    private void validateExistingSession(String sessionId) {

        if(!userService.isLogin(sessionId)) {
            throw new IllegalArgumentException("세션이 존재하지 않습니다.");
        }
    }

    private void validateNotExistingSession(String sessionId) {

        if(userService.isLogin(sessionId)) {
            throw new IllegalArgumentException("이미 로그인 되어있습니다.");
        }
    }
}
