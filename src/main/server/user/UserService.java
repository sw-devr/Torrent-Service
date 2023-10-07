package main.server.user;

import main.server.file.FileService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    private final Map<String /*sessionId */, Long /* userId */> session = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final FileService fileService;

    public UserService(UserRepository userRepository, FileService fileService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
    }


    public String login(RequestLoginDto request) {

        User user = userRepository.findByEmail(request.getEmail());

        if(user == null) {
            throw new IllegalArgumentException("올바르지 못한 회원 인증입니다.");
        }
        if(!user.isCorrectPassword(request.getPassword())) {
            throw new IllegalArgumentException("올바르지 못한 회원 인증입니다.");
        }
        String sessionId = UUID.randomUUID().toString();
        session.put(sessionId, user.getId());

        return sessionId;
    }

    public void logout(String sessionId) {

        if(!isLogin(sessionId)) {
            throw new IllegalArgumentException("로그인 되어 있지 않은 사용자 입니다.");
        }
        session.remove(sessionId);
    }

    public boolean isLogin(String sessionId) {

        if(sessionId == null)
            return false;

        return session.containsKey(sessionId);
    }

    public void join(RequestJoinDto request) {

        if(userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 email입니다.");
        }
        User user = User.init(request.getEmail(), request.getPassword());

        userRepository.save(user);
    }


    public Long findUserIdBySessionId(String sessionId) {

        return session.get(sessionId);
    }

    public ResponseUserDto findUserBySessionId(String sessionId) {

        Long userId = session.get(sessionId);
        if(userId == null) {
            throw new IllegalArgumentException("현재 세션이 존재하지 않습니다.");
        }
        User user = userRepository.findById(userId);
        if(user == null) {
            session.remove(sessionId);
            throw new IllegalStateException("현재 세션에 해당하는 유저 정보가 존재하지 않습니다.");
        }
        ResponseUserDto response = new ResponseUserDto();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setPoints(user.getPoints());
        response.setRole(user.getRole());

        return response;
    }

    public void remove(String sessionId) {

        Long userId = session.remove(sessionId);

        if(userId == null) {
            throw new IllegalArgumentException("요청한 세션이 존재하지 않습니다.");
        }
        fileService.deleteFromUser(userId);
        if(!userRepository.delete(userId)) {
            session.remove(sessionId);
            throw new IllegalStateException("현재 세션에 해당하는 유저 정보가 존재하지 않습니다.");
        }
    }
}
