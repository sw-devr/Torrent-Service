package main.server.user;

public interface UserRepository {

    User findById(long id);
    User findByEmail(String email);
    void save(User user);
    boolean update(User user);
    boolean delete(long id);
}
