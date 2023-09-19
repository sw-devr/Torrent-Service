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

        session.remove(sessionId);
    }

    public boolean isLogin(String sessionId) {

        if(sessionId == null)
            return false;

        return session.containsKey(sessionId);
    }

    public void join(RequestJoinDto request) {

        System.out.println(request);
        if(userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 email입니다.");
        }
        User user = User.init(request.getEmail(), request.getPassword());

        userRepository.save(user);
    }

    public void remove(String sessionId) {

        long userId = session.remove(sessionId);

        fileService.deleteFromUser(userId);
        userRepository.delete(userId);
    }

    public Long currentUserId(String sessionId) {

        return session.get(sessionId);
    }

    public ResponseUserDto find(String sessionId) {

        long userId = session.get(sessionId);
        User user =  userRepository.findById(userId);

        ResponseUserDto response = new ResponseUserDto();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setPoints(user.getPoints());
        response.setRole(user.getRole());

        return response;
    }
}
