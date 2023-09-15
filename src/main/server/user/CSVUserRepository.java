package main.server.user;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVUserRepository implements UserRepository {

    private final Path csvFilePath;

    public CSVUserRepository(String path) {

        this.csvFilePath = Paths.get(path);
    }

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
