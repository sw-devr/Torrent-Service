package test.server.user;

import main.server.file.FileService;
import main.server.user.*;

public class UserServiceTest {

    public static void main(String[] args) {

        UserServiceTest test = new UserServiceTest();

        //로그인 테스트
        test.testLoginWithValidParam();
        test.testLoginWithNotExistingEmail();
        test.testLoginWithInvalidPassword();

        //로그아웃 테스트
        test.testLogoutWithValidSessionId();
        test.testLogoutWithInvalidSessionId();

        //회원가입 테스트
        test.testJoinWithNotExistingEmail();
        test.testJoinWithAlreadyExistingEmail();

        //회원 id 조회 테스트
        test.testFindUserIdWithValidSession();
        test.testFindUserIdWithInvalidSession();

        //회원 정보 조회 테스트
        test.testFindUserWithValidSession();
        test.testFindUserWithInvalidSession();
        test.testFindUserWithInvalidSession2();

        //회원 탈퇴 테스트
        test.testRemoveUserWithValidSession();
        test.testRemoveUserWithInvalidSession();
        test.testRemoveUserWithInvalidSession2();
    }


    /**
     *  로그인 테스트 1 : 올바른 요청 값이 들어온 경우 세션이 생성되고 해당 세션 id를 리턴한다.
     */
    private void testLoginWithValidParam() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        //when
        String sessionId = userService.login(request);

        //then
        if(!userService.isLogin(sessionId)) {
            throw new IllegalStateException("로그인 테스트 1 실패 : 세션 값이 저장되어있지 않음");
        }
        System.out.println("로그인 테스트 1 성공");
    }


    /**
     *  로그인 테스트 2 : 이메일이 존재하지 않는 요청의 경우 로그인 실패하고 예외가 발생한다.
     */
    private void testLoginWithNotExistingEmail() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return null;}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        //when & then
        try {
            String sessionId = userService.login(request);
        }
        catch (IllegalArgumentException e) {
            System.out.println("로그인 테스트 2 성공");
            return;
        }
        throw new IllegalStateException("로그인 테스트 2 실패 : email이 존재하지 않는 유저에 대해 예외가 발생하지 않음");
    }


    /**
     *  로그인 테스트 3 : 비밀번호가 일치하지 않는 경우 로그인 실패하고 예외가 발생한다.
     */
    private void testLoginWithInvalidPassword() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword("invalid" + UserTestCase.getTestUser().getPassword());

        //when & then
        try {
            String sessionId = userService.login(request);
        }
        catch (IllegalArgumentException e) {
            System.out.println("로그인 테스트 3 성공");
            return;
        }
        throw new IllegalStateException("로그인 테스트 3 실패 : 올바르지 못한 password 요청에 대해 예외가 발생하지 않음");
    }


    /**
     *  로그아웃 테스트 1 : 로그인된 세션에 대해 로그아웃 요청 시 세션이 삭제된다.
     */
    private void testLogoutWithValidSessionId() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        String sessionId = userService.login(request);
        if(!userService.isLogin(sessionId)) {
            throw new IllegalStateException("로그아웃 테스트 1 실패 : 세션 값이 저장되어있지 않음");
        }


        //when
        userService.logout(sessionId);

        //then
        if(userService.isLogin(sessionId)) {
            throw new IllegalStateException("로그아웃 테스트 1 실패 : 세션 값이 삭제되지 않음");
        }
        System.out.println("로그아웃 테스트 1 성공");
    }


    /**
     *  로그아웃 테스트 2 : 존재하지 않는 세션에 대해 로그아웃 요청 시 예외가 발생한다.
     */
    private void testLogoutWithInvalidSessionId() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        String sessionId = "invalidSessionId";

        //when & then
        try {
            userService.logout(sessionId);
        }
        catch (IllegalArgumentException e) {
            System.out.println("로그아웃 테스트 2 성공");
            return;
        }
        throw new IllegalStateException("로그아웃 테스트 2 실패 : 존재하지 않는 세션에 대해 예외가 발생하지 않음");
    }


    /**
     *  회원가입 테스트 1 : 존재하지 않는 email로 회원가입 요청시 회원가입에 성공한다.
     */
    private void testJoinWithNotExistingEmail() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return null;}

            @Override
            public void save(User user) {}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestJoinDto request = new RequestJoinDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        //when & then
        try {
            userService.join(request);
            System.out.println("회원가입 테스트 1 성공");
        }
        catch (Exception e) {
            throw new IllegalStateException("회원가입 테스트 2 실패 : 예외 발생 " + e.getMessage());
        }
    }


    /**
     *  회원가입 테스트 2 : 이미 존재하는 email로 회원가입 요청시 예외가 발생한다.
     */
    private void testJoinWithAlreadyExistingEmail() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}

            @Override
            public void save(User user) {}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestJoinDto request = new RequestJoinDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        //when & then
        try {
            userService.join(request);
        }
        catch (IllegalArgumentException e) {
            System.out.println("회원가입 테스트 2 성공");
            return;
        }
        throw new IllegalStateException("회원가입 테스트 2 실패 : 이미 존재하는 email에 대해 예외가 발생하지 않음");
    }


    /**
     *  회원 id 조회 테스트 1 : 요청 세션값에 해당하는 회원 id를 리턴한다.
     */
    private void testFindUserIdWithValidSession() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        String sessionId = userService.login(request);

        //when
        long userId = userService.findUserIdBySessionId(sessionId);

        //then
        if(userId != UserTestCase.getTestUser().getId()) {
            throw new IllegalStateException("회원 id 조회 테스트 1 실패 : 요청한 세션에 대한 유저 정보가 올바르지 못함");
        }
        System.out.println("회원 id 조회 테스트 1 성공");
    }


    /**
     *  회원 id 조회 테스트 2 : 존재하지 않는 세션에 대해서는 null을 리턴한다.
     */
    private void testFindUserIdWithInvalidSession() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {};
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        String sessionId = "invalidSessionId";

        //when
        Long userId = userService.findUserIdBySessionId(sessionId);

        //then
        if(userId != null) {
            throw new IllegalStateException("회원 id 조회 테스트 2 실패 : 존재하지 않는 세션에 대한 유저 정보가 리턴됨");
        }
        System.out.println("회원 id 조회 테스트 2 성공");
    }


    /**
     *  회원 조회 테스트 1 : 요청 세션값에 해당하는 회원 데이터를 리턴한다.
     */
    private void testFindUserWithValidSession() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}

            @Override
            public User findById(long id) {return UserTestCase.getTestUser();}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        String sessionId = userService.login(request);

        //when
        ResponseUserDto responseUserDto = userService.findUserBySessionId(sessionId);

        //then
        if(responseUserDto.getUserId() != UserTestCase.getTestUser().getId()
                || !responseUserDto.getEmail().equals(UserTestCase.getTestUser().getEmail())
                || responseUserDto.getRole() != UserTestCase.getTestUser().getRole()
                || responseUserDto.getPoints() != UserTestCase.getTestUser().getPoints()) {
            throw new IllegalStateException("회원 조회 테스트 1 실패 : 요청한 세션에 대한 유저 정보가 올바르지 못함");
        }
        System.out.println("회원 조회 테스트 1 성공");
    }


    /**
     *  회원 조회 테스트 2 : 존재하지 않는 세션에 대해서 예외가 발생한다.
     */
    private void testFindUserWithInvalidSession() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {};
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        String sessionId = "invalidSessionId";

        //when & then
        try {
            ResponseUserDto responseUserDto = userService.findUserBySessionId(sessionId);
        }
        catch (IllegalArgumentException e) {
            System.out.println("회원 조회 테스트 2 성공");
            return;
        }
        throw new IllegalStateException("회원 조회 테스트 2 실패 : 존재하지 않는 세션에 대한 유저 정보가 리턴됨");
    }


    /**
     *  회원 조회 테스트 3 : 세션에 대한 유저 정보가 존재하지 않을 경우 예외가 발생한다.
     */
    private void testFindUserWithInvalidSession2() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}

            @Override
            public User findById(long id) {return null;}
        };
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        String sessionId = userService.login(request);

        //when & then
        try {
            ResponseUserDto responseUserDto = userService.findUserBySessionId(sessionId);
        }
        catch (IllegalStateException e) {
            if(!userService.isLogin(sessionId)) {
                System.out.println("회원 조회 테스트 3 성공");
                return;
            }else {
                throw new IllegalStateException("회원 조회 테스트 2 실패 : 존재하지 않는 유저데이터에 대한 세션이 삭제되지 않음");
            }
        }
        throw new IllegalStateException("회원 조회 테스트 2 실패 : 존재하지 않는 세션에 대한 유저 정보가 리턴됨");
    }


    /**
     *  회원 탈퇴 테스트 1 : 요청 세션값에 해당하는 회원 데이터를 삭제한다.
     */
    private void testRemoveUserWithValidSession() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}

            @Override
            public boolean delete(long id) {return true;}
        };
        FileService mockFileService = new FileService(null, null, null){
            @Override
            public void deleteFromUser(long userId) {}
        };
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        String sessionId = userService.login(request);

        //when & then
        try {
            userService.remove(sessionId);
            System.out.println("회원 탈퇴 테스트 1 성공");
        }
        catch (Exception e) {
            throw new IllegalStateException("회원 탈퇴 테스트 1 실패 : 예상치 못한 예외 발생");
        }
    }


    /**
     *  회원 탈퇴 테스트 2 : 존재하지 않는 세션에 대해서 예외가 발생한다.
     */
    private void testRemoveUserWithInvalidSession() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {};
        FileService mockFileService = new FileService(null, null, null);
        UserService userService = new UserService(mockUserRepository, mockFileService);

        String sessionId = "invalidSessionId";

        //when & then
        try {
            userService.remove(sessionId);
        }
        catch (IllegalArgumentException e) {
            System.out.println("회원 탈퇴 테스트 2 성공");
            return;
        }
        throw new IllegalStateException("회원 탈퇴 테스트 2 실패 : 존재하지 않는 세션에 대한 유저 정보가 리턴됨");
    }


    /**
     *  회원 탈퇴 테스트 3 : 세션에 대한 유저 정보가 존재하지 않을 경우 예외가 발생한다.
     */
    private void testRemoveUserWithInvalidSession2() {

        //given
        UserRepository mockUserRepository = new CSVUserRepository("", "") {
            @Override
            public User findByEmail(String email) {return UserTestCase.getTestUser();}

            @Override
            public boolean delete(long id) {return false;}
        };
        FileService mockFileService = new FileService(null, null, null){
            @Override
            public void deleteFromUser(long userId) {}
        };
        UserService userService = new UserService(mockUserRepository, mockFileService);

        RequestLoginDto request = new RequestLoginDto();
        request.setEmail(UserTestCase.getTestUser().getEmail());
        request.setPassword(UserTestCase.getTestUser().getPassword());

        String sessionId = userService.login(request);

        //when & then
        try {
            userService.remove(sessionId);
        }
        catch (IllegalStateException e) {
            if(!userService.isLogin(sessionId)) {
                System.out.println("회원 탈퇴 테스트 3 성공");
                return;
            }else {
                throw new IllegalStateException("회원 탈퇴 테스트 3 실패 : 존재하지 않는 유저데이터에 대한 세션이 삭제되지 않음");
            }
        }
        throw new IllegalStateException("회원 탈퇴 테스트 3 실패 : 존재하지 않는 세션에 대한 유저 정보가 리턴됨");
    }
}
