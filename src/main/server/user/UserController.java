package main.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;

import static main.protocol.ProtocolConstants.*;
import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.SESSION_ID;
import static main.server.common.CommonConstants.OBJECT_MAPPER;
import static main.server.common.CommonConstants.USER_SERVICE;
import static main.server.user.UserConstants.EMAIL_REGEX;
import static main.server.user.UserConstants.PASSWORD_REGEX;

public class UserController {

    private final ObjectMapper objectMapper;
    private final UserService userService;

    public UserController() {
        this.objectMapper = OBJECT_MAPPER;
        this.userService = USER_SERVICE;
    }

    public UserController(ObjectMapper objectMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }


    @Mapping(USER_LOGIN_URL)
    public SocketResponse login(SocketRequest request) {

        try{
            validateNotExistingSession(request.getHeader().get(SESSION_ID.getValue()));

            String sessionId = userService.login(objectMapper.readValue((String)request.getBody(), RequestLoginDto.class));

            request.getHeader().put(SESSION_ID.getValue(), sessionId);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "로그인 성공");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }



    @Mapping(USER_LOGOUT_URL)
    public SocketResponse logout(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateExistingSession(sessionId);
            userService.logout(sessionId);

            request.getHeader().remove(SESSION_ID.getValue());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "로그 아웃 성공");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e);
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping(USER_FIND_URL)
    public SocketResponse findByCurrentSession(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateExistingSession(sessionId);
            ResponseUserDto user = userService.findUserBySessionId(sessionId);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), objectMapper.writeValueAsString(user));
        }
        catch (IllegalArgumentException e) {
            System.out.println(e);
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e);
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }

    }

    @Mapping(USER_JOIN_URL)
    public SocketResponse join(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateNotExistingSession(sessionId);
            String requestBodyString = (String)(request.getBody());

            RequestJoinDto requestBody = objectMapper.readValue(requestBodyString, RequestJoinDto.class);
            validateRequestJoinDto(requestBody);

            userService.join(requestBody);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "회원 가입 성공");
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e);
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }

    }

    @Mapping(USER_DELETE_URL)
    public SocketResponse delete(SocketRequest request) {

        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            validateExistingSession(sessionId);
            userService.remove(sessionId);
            request.getHeader().remove(SESSION_ID.getValue());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "계정 삭제 성공");
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalAccessException e) {
            System.out.println(e);
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }


    private void validateExistingSession(String sessionId) throws IllegalAccessException {

        if(!userService.isLogin(sessionId)) {
            throw new IllegalAccessException("세션이 존재하지 않습니다.");
        }
    }

    private void validateNotExistingSession(String sessionId) throws IllegalAccessException {

        if(userService.isLogin(sessionId)) {
            throw new IllegalAccessException("이미 로그인 되어있습니다.");
        }
    }

    private void validateRequestJoinDto(RequestJoinDto requestBody) {

        String requestEmail = requestBody.getEmail();
        String requestPassword = requestBody.getPassword();

        if(!requestEmail.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("이메일 형식이 아닙니다. 올바른 이메일 형식으로 회원가입 해주세요.");
        }
        if(!requestPassword.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException("비밀번호는 최소 9자리 이상 100자리 이하, 적어도 하나 이상의 문자와 특수문자가 포함되어야 합니다.");
        }
    }
}
