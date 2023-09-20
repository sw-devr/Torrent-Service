package test.server.user;

import main.server.user.User;
import main.server.user.UserRole;

import java.util.List;

public class UserTestCase {
    public static List<User> getNotExsitingUserList() {

        User user1 = new User();
        user1.setId(50L);
        user1.setEmail("notExsitingUser1@naver.com");
        user1.setPassword("12345");
        user1.setRole(UserRole.GENERAL);
        user1.setPoints(500);

        return List.of(user1);
    }

    public static List<User> getUpdatedUserList() {

        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("hi@naver.com");
        user1.setPassword("12345");
        user1.setRole(UserRole.UPLOADER);
        user1.setPoints(500);

        return List.of(user1);
    }
}
