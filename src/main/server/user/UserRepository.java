package main.server.user;

public interface UserRepository {

    User findById(long id);
    User findByEmail(String email);
    void save(User user);
    void update(User user);
    void delete(long id);
}
