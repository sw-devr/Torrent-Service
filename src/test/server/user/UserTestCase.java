package test.server.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.protocol.ContentType;
import main.protocol.ProtocolConstants;
import main.protocol.SocketHeaderType;
import main.protocol.SocketRequest;
import main.server.common.CommonConstants;
import main.server.user.RequestJoinDto;
import main.server.user.RequestLoginDto;
import main.server.user.User;
import main.server.user.UserRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTestCase {

    static List<Long> getUserListToFindById() {

        return List.of(0L,1L,2L,3L,4L,5L,6L,10000000000L);
    }

    static List<String> getUserListToFindByEmail() {

        return List.of("test5@naver.com", "notExistingUser@naver.com");
    }

    static List<User> getUserListToSave() {

        User user1 = new User();
        user1.setEmail("notExsitingUser1@naver.com");
        user1.setPassword("12345");
        user1.setRole(UserRole.GENERAL);
        user1.setPoints(500);

        User user2 = new User();
        user2.setId(33);
        user2.setEmail("notExsitingUser2@naver.com");
        user2.setPassword("12345");
        user2.setRole(UserRole.GENERAL);
        user2.setPoints(500);

        User user3 = new User();
        user3.setId(10000000001L);
        user3.setEmail("notExsitingUser3@naver.com");
        user3.setPassword("12345");
        user3.setRole(UserRole.GENERAL);
        user3.setPoints(500);

        return List.of(user1, user2, user3);
    }

    static List<User> getUserListToUpdate() {

        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("hi@naver.com");
        user1.setPassword("12345");
        user1.setRole(UserRole.UPLOADER);
        user1.setPoints(500);

        User user2 = new User();
        user2.setId(33L);
        user2.setEmail("hi@naver.com");
        user2.setPassword("12345");
        user2.setRole(UserRole.UPLOADER);
        user2.setPoints(500);

        return List.of(user1, user2);
    }

    static List<Long> getUserListToDelete() {

        return List.of(0L,1L,2L,3L,4L,5L,6L,100000L);
    }

    static List<SocketRequest> getSocketRequestWithInvalidJoin() throws JsonProcessingException {

        RequestJoinDto requestBody1 = new RequestJoinDto();
        requestBody1.setEmail("invalidEmail");
        requestBody1.setPassword("1234");

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_JOIN_URL);
        request1.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request1.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody1));

        RequestJoinDto requestBody2 = new RequestJoinDto();
        requestBody2.setEmail("invalid#Email@naver.com");
        requestBody2.setPassword("1234");

        SocketRequest request2 = new SocketRequest();
        request2.setUrl(ProtocolConstants.USER_JOIN_URL);
        request2.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request2.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody2));

        RequestJoinDto requestBody3 = new RequestJoinDto();
        requestBody3.setEmail("valid@naver.com");
        requestBody3.setPassword("1234asdfa");

        SocketRequest request3 = new SocketRequest();
        request3.setUrl(ProtocolConstants.USER_JOIN_URL);
        request3.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request3.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody3));

        RequestJoinDto requestBody4 = new RequestJoinDto();
        requestBody4.setEmail("valid@naver.com");
        requestBody4.setPassword("1234");

        SocketRequest request4 = new SocketRequest();
        request4.setUrl(ProtocolConstants.USER_JOIN_URL);
        request4.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request4.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody4));

        RequestJoinDto requestBody5 = new RequestJoinDto();
        requestBody5.setEmail("valid@naver.com");
        requestBody5.setPassword("1234SDG!");

        SocketRequest request5 = new SocketRequest();
        request5.setUrl(ProtocolConstants.USER_JOIN_URL);
        request5.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request5.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody5));

        RequestJoinDto requestBody6 = new RequestJoinDto();
        requestBody6.setEmail("valid@naver.com");
        requestBody6.setPassword("1234SDG!1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");

        SocketRequest request6 = new SocketRequest();
        request6.setUrl(ProtocolConstants.USER_JOIN_URL);
        request6.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request6.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody6));

        return List.of(request1, request2, request3, request4, request5, request6);
    }

    static List<SocketRequest> getSocketRequestWithValidJoin() throws JsonProcessingException {

        RequestJoinDto requestBody1 = new RequestJoinDto();
        requestBody1.setEmail("valid@naver.com");
        requestBody1.setPassword("Valid1234!");

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_JOIN_URL);
        request1.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request1.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody1));

        RequestJoinDto requestBody2 = new RequestJoinDto();
        requestBody2.setEmail("valid@naver.co.kr");
        requestBody2.setPassword("valid1234(");

        SocketRequest request2 = new SocketRequest();
        request2.setUrl(ProtocolConstants.USER_JOIN_URL);
        request2.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request2.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody2));

        RequestJoinDto requestBody3 = new RequestJoinDto();
        requestBody3.setEmail("valid@naver.com");
        requestBody3.setPassword("123+VVVVVVVVV");

        SocketRequest request3 = new SocketRequest();
        request3.setUrl(ProtocolConstants.USER_JOIN_URL);
        request3.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request3.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody3));

        RequestJoinDto requestBody4 = new RequestJoinDto();
        requestBody4.setEmail("valid@naver.com");
        requestBody4.setPassword("12sdgG3QT'34");

        SocketRequest request4 = new SocketRequest();
        request4.setUrl(ProtocolConstants.USER_JOIN_URL);
        request4.setHeader(Map.of(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue()));
        request4.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody4));

        return List.of(request1, request2, request3, request4);
    }

    static List<SocketRequest> getSocketRequestWithValidLogin() throws JsonProcessingException {

        RequestLoginDto requestBody1 = new RequestLoginDto();
        requestBody1.setEmail("valid@naver.com");
        requestBody1.setPassword("Valid1234!");

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_LOGIN_URL);
        request1.setHeader(header);
        request1.setBody(CommonConstants.OBJECT_MAPPER.writeValueAsString(requestBody1));

        return List.of(request1);
    }

    static List<SocketRequest> getSocketRequestWithValidLogout() {

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        header.put(SocketHeaderType.SESSION_ID.getValue(), "sessionId1");

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_LOGOUT_URL);
        request1.setHeader(header);

        return List.of(request1);
    }

    static List<SocketRequest> getSocketRequestWithInvalidLogout() {

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_LOGOUT_URL);
        request1.setHeader(header);

        return List.of(request1);
    }

    static List<SocketRequest> getSocketRequestWithValidFindUser() {

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        header.put(SocketHeaderType.SESSION_ID.getValue(), "sessionId1");

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_FIND_URL);
        request1.setHeader(header);

        return List.of(request1);
    }

    static List<SocketRequest> getSocketRequestWithInvalidFindUser() {

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_FIND_URL);
        request1.setHeader(header);

        return List.of(request1);
    }

    static List<SocketRequest> getSocketRequestWithValidDelete() {

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        header.put(SocketHeaderType.SESSION_ID.getValue(), "sessionId1");

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_DELETE_URL);
        request1.setHeader(header);

        return List.of(request1);
    }

    static List<SocketRequest> getSocketRequestWithInvalidDelete() {

        Map<String, String> header = new HashMap<>();
        header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());

        SocketRequest request1 = new SocketRequest();
        request1.setUrl(ProtocolConstants.USER_DELETE_URL);
        request1.setHeader(header);

        return List.of(request1);
    }

    public static User getTestUser() {

        User user = new User();
        user.setId(500L);
        user.setEmail("test@naver.com");
        user.setPassword("testPassword");
        user.setRole(UserRole.GENERAL);
        user.setPoints(2000);

        return user;
    }
}
