package test.server.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import main.protocol.SocketHeaderType;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;
import main.server.user.*;

import static main.server.common.CommonConstants.OBJECT_MAPPER;

public class UserControllerTest {


    public static void main(String[] args) throws JsonProcessingException {

        UserControllerTest test = new UserControllerTest();

        //회원 가입 테스트
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidJoin()) {
            test.testJoinByValidRequestBody(validRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithInvalidJoin()) {
            test.testJoinByInvalidRequestBody(invalidRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithValidJoin()) {
            test.testJoinWithAlreadyLogin(invalidRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithValidJoin()) {
            test.testJoinWithUnknownError(invalidRequest);
        }

        //로그인 테스트
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithValidLogin()) {
            test.testLoginWithValidParam(invalidRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithValidLogin()) {
            test.testLoginWithInvalidParam(invalidRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithValidLogin()) {
            test.testLoginWithSession(invalidRequest);
        }

        //로그 아웃 테스트
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidLogout()) {
            test.testLogoutWithSession(validRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithInvalidLogout()) {
            test.testLogoutWithoutSession(invalidRequest);
        }
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidLogout()) {
            test.testLogoutWithUnknownError(validRequest);
        }

        //유저 데이터 조회 테스트
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidFindUser()) {
            test.testFindCurrentUserWithSession(validRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithInvalidFindUser()) {
            test.testFindCurrentUserWithoutSession(invalidRequest);
        }
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidFindUser()) {
            test.testFindCurrentUserWithUnknownError(validRequest);
        }

        //유저 계정 탈퇴 테스트
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidDelete()) {
            test.testDeleteWithSession(validRequest);
        }
        for(SocketRequest invalidRequest : UserTestCase.getSocketRequestWithInvalidDelete()) {
            test.testDeleteWithoutSession(invalidRequest);
        }
        for(SocketRequest validRequest : UserTestCase.getSocketRequestWithValidDelete()) {
            test.testDeleteWithUnknownError(validRequest);
        }
    }


    /**
     *  회원 가입 테스트1 : 세션이 없고 올바른 요청 값으로 회원가입 요청 시 200 상태코드를 리턴한다.
     */
    private void testJoinByValidRequestBody(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}
            @Override
            public void join(RequestJoinDto request) {}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.join(request);

        //then
        if(response.getStatusCode() != Status.SUCCESS.getCode()) {
            throw new IllegalStateException("회원 가입 테스트 1 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("회원 가입 테스트 1 성공");
    }


    /**
     *  회원 가입 테스트2 : 세션이 없지만 올바르지 못한 요청 값으로 회원가입 요청 시 400 상태코드를 리턴한다.
     */
    private void testJoinByInvalidRequestBody(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}

            @Override
            public void join(RequestJoinDto request) {}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.join(request);

        //then
        if(response.getStatusCode() != Status.BAD_REQUEST.getCode()) {
            throw new IllegalStateException("회원 가입 테스트 2 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("회원 가입 테스트 2 성공");
    }


    /**
     *  회원 가입 테스트3 : 세션이 존재하면서 회원가입 요청 시 403 상태코드를 리턴한다.
     */
    private void testJoinWithAlreadyLogin(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public void join(RequestJoinDto request) {}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.join(request);

        //then
        if(response.getStatusCode() != Status.FORBIDDEN.getCode()) {
            throw new IllegalStateException("회원 가입 테스트 3 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("회원 가입 테스트 3 성공");
    }


    /**
     *  회원 가입 테스트4 : 예상치 못한 에러 발생시 500 상태코드를 리턴한다.
     */
    private void testJoinWithUnknownError(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}

            @Override
            public void join(RequestJoinDto request) {throw new RuntimeException("DB에 데이터 저장 실패");}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.join(request);

        //then
        if(response.getStatusCode() != Status.INTERNAL_SERVER_ERROR.getCode()) {
            throw new IllegalStateException("회원 가입 테스트 4 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("회원 가입 테스트 4 성공");
    }


    /**
     *  로그인 테스트 1 : 세션이 없고 올바른 요청 값으로 로그인 시도할 경우 200 상태코드를 리턴한다.
     */
    private void testLoginWithValidParam(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}

            @Override
            public String login(RequestLoginDto request) {return "sessionId1";}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.login(request);

        //then
        if(response.getStatusCode() != Status.SUCCESS.getCode()) {
            throw new IllegalStateException("로그인 테스트 1 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("로그인 테스트 1 성공");
    }


    /**
     *  로그인 테스트 2 : 세션이 없지만 올바르지 못한 값으로 로그인 시도할 경우 400 상태코드를 리턴한다.
     */
    private void testLoginWithInvalidParam(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}

            @Override
            public String login(RequestLoginDto request) {throw new IllegalArgumentException("로그인 실패");}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.login(request);

        //then
        if(response.getStatusCode() != Status.BAD_REQUEST.getCode()) {
            throw new IllegalStateException("로그인 테스트 2 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("로그인 테스트 2 성공");
    }


    /**
     *  로그인 테스트 3 : 세션이 존재하면서 로그인 시도할 경우 403 상태코드를 리턴한다.
     */
    private void testLoginWithSession(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.login(request);

        //then
        if(response.getStatusCode() != Status.FORBIDDEN.getCode()) {
            throw new IllegalStateException("로그인 테스트 3 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("로그인 테스트 3 성공");
    }


    /**
     *  로그인 테스트 4 : 로그인 시도 중 예상치 못한 에러가 발생할 경우 500 상태코드를 리턴한다.
     */
    private void testLoginWithUnknownError(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public String login(RequestLoginDto request) { throw new RuntimeException("DB 조회 중 에러 발생");}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.login(request);

        //then
        if(response.getStatusCode() != Status.INTERNAL_SERVER_ERROR.getCode()) {
            throw new IllegalStateException("로그인 테스트 3 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("로그인 테스트 3 성공");
    }


    /**
     *  로그 아웃 테스트 1 : 세션이 있고 로그 아웃 시도할 경우 200 상태코드를 리턴한다.
     */
    private void testLogoutWithSession(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public void logout(String sessionId) { }
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.logout(request);

        //then
        if(response.getStatusCode() != Status.SUCCESS.getCode()) {
            throw new IllegalStateException("로그아웃 테스트 1 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        if(response.getHeader().get(SocketHeaderType.SESSION_ID.getValue()) != null) {
            throw new IllegalStateException("로그아웃 테스트 1 실패" + " response에 세션 정보가 존재하면 안 됌");
        }
        System.out.println("로그아웃 테스트 1 성공");
    }


    /**
     *  로그 아웃 테스트 2 : 세션 없이 로그 아웃 시도할 경우 403 상태코드를 리턴한다.
     */
    private void testLogoutWithoutSession(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}

            @Override
            public void logout(String sessionId) {}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.logout(request);

        //then
        if(response.getStatusCode() != Status.FORBIDDEN.getCode()) {
            throw new IllegalStateException("로그아웃 테스트 2 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("로그아웃 테스트 2 성공");
    }


    /**
     *  로그 아웃 테스트 3 : 로그 아웃 시도 중 예상치 못한 에러가 발생할 경우 500 상태코드를 리턴한다.
     */
    private void testLogoutWithUnknownError(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public void logout(String sessionId) { throw new RuntimeException("세션 접근시 에러 발생");}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.logout(request);

        //then
        if(response.getStatusCode() != Status.INTERNAL_SERVER_ERROR.getCode()) {
            throw new IllegalStateException("로그 아웃 테스트 3 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("로그 아웃 테스트 3 성공");
    }


    /**
     *  유저 데이터 조회 테스트 1 : 세션이 존재하고 현재 유저의 정보를 조회를 요청할 경우 200 상태코드를 리턴한다.
     */
    private void testFindCurrentUserWithSession(SocketRequest request) throws JsonProcessingException {

        //given
        ResponseUserDto responseUserDto = new ResponseUserDto();
        responseUserDto.setEmail("test@naver.com");
        responseUserDto.setUserId(5L);
        responseUserDto.setPoints(5000);
        responseUserDto.setRole(UserRole.GENERAL);

        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public ResponseUserDto findUserBySessionId(String sessionId) {

                return responseUserDto;
            }
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.findByCurrentSession(request);

        //then
        if(response.getStatusCode() != Status.SUCCESS.getCode()) {
            throw new IllegalStateException("유저 데이터 조회 테스트 1 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        ResponseUserDto responseBody = OBJECT_MAPPER.readValue((String)response.getBody(), ResponseUserDto.class);
        if(!responseBody.equals(responseUserDto)) {
            throw new IllegalStateException("유저 데이터 조회 테스트 1 실패" + "예상 값 : " + responseUserDto + " 과 실제 값 : " + responseBody + " 값이 다릅니다.");
        }
        System.out.println("유저 데이터 조회 테스트 1 성공");
    }


    /**
     *  유저 데이터 조회 테스트 2 : 세션이 존재하지 않고 현재 유저의 정보를 조회를 요청할 경우 403 상태코드를 리턴한다.
     */
    private void testFindCurrentUserWithoutSession(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.findByCurrentSession(request);

        //then
        if(response.getStatusCode() != Status.FORBIDDEN.getCode()) {
            throw new IllegalStateException("유저 데이터 조회 테스트 2 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("유저 데이터 조회 테스트 2 성공");
    }


    /**
     *  유저 데이터 조회 테스트 3 : 유저의 정보 조회 중 예상치 못한 에러가 발생할 경우 500 상태코드를 리턴한다.
     */
    private void testFindCurrentUserWithUnknownError(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public ResponseUserDto findUserBySessionId(String sessionId) {throw new RuntimeException("DB에서 유저 데이터 조회 중 에러 발생");}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.findByCurrentSession(request);

        //then
        if(response.getStatusCode() != Status.INTERNAL_SERVER_ERROR.getCode()) {
            throw new IllegalStateException("유저 데이터 조회 테스트 3 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("유저 데이터 조회 테스트 3 성공");
    }


    /**
     *  유저 계정 탈퇴 테스트 1 : 세션이 존재하고 현재 유저의 계정 탈퇴를 요청할 경우 200 상태코드를 리턴한다.
     */
    private void testDeleteWithSession(SocketRequest request) throws JsonProcessingException {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public void remove(String sessionId) {}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.delete(request);

        //then
        if(response.getStatusCode() != Status.SUCCESS.getCode()) {
            throw new IllegalStateException("유저 계정 탈퇴 테스트 1 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("유저 계정 탈퇴 테스트 1 성공");
    }


    /**
     *  유저 계정 탈퇴 테스트 2 : 세션이 존재하지 않고 현재 유저의 계정 탈퇴를 요청할 경우 403 상태코드를 리턴한다.
     */
    private void testDeleteWithoutSession(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return false;}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.delete(request);

        //then
        if(response.getStatusCode() != Status.FORBIDDEN.getCode()) {
            throw new IllegalStateException("유저 계정 탈퇴 테스트 2 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("유저 계정 탈퇴 테스트 2 성공");
    }


    /**
     *  유저 계정 탈퇴 테스트 3 : 계정 탈퇴 중 예상치 못한 에러가 발생할 경우 500 상태코드를 리턴한다.
     */
    private void testDeleteWithUnknownError(SocketRequest request) {

        //given
        UserService mockUserService = new UserService(null, null) {
            @Override
            public boolean isLogin(String sessionId) {return true;}

            @Override
            public void remove(String sessionId) {throw new RuntimeException("계정 삭제 중 에러 발생");}
        };
        UserController userController = new UserController(OBJECT_MAPPER, mockUserService);

        //when
        SocketResponse response = userController.delete(request);

        //then
        if(response.getStatusCode() != Status.INTERNAL_SERVER_ERROR.getCode()) {
            throw new IllegalStateException("유저 계정 탈퇴 테스트 3 실패" + "결과 상태 코드 : " + response.getStatusCode());
        }
        System.out.println("유저 계정 탈퇴 테스트 3 성공");
    }
}
