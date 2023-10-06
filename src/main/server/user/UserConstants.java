package main.server.user;

public class UserConstants {

    public static final String ALREADY_EXISTING_USER_MESSAGE = "이미 존재하는 유저입니다.";
    public static final String NOT_EXISTING_USER_MESSAGE = "존재하지 않는 유저입니다.";

    public static final int INIT_POINTS = 1000;
    public static final String EMAIL_REGEX = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!~<>,;:_=?*+#.\"'&§%°()\\|\\[\\]\\-\\$\\^\\@\\/])[A-Za-z\\d!~<>,;:_=?*+#.\"'&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{9,100}$";
}
