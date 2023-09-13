package server.user;

public interface UserRepository {

    UserDto findById(long id);
    UserDto findByEmail(String email);
    void save(UserDto user);
    void update(UserDto user);
    void delete(long id);
}
