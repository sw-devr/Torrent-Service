package main.server.user;

public class CSVUserRepository implements UserRepository {

    @Override
    public UserDto findById(long id) {
        return null;
    }

    @Override
    public UserDto findByEmail(String email) {
        return null;
    }

    @Override
    public void save(UserDto user) {

    }

    @Override
    public void update(UserDto user) {

    }

    @Override
    public void delete(long id) {

    }
}
