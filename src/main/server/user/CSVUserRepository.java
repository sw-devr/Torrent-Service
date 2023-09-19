package main.server.user;

import main.server.common.CommonConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static main.server.common.CommonConstants.CSV_COLUMN_SEPARATOR;

public class CSVUserRepository implements UserRepository {

    private final Path csvFilePath;
    private final Path tempCsvFilePath;
    private final Path idFilePath;


    public CSVUserRepository(String path, String idPath) {

        this.csvFilePath = Paths.get(path);
        tempCsvFilePath = Paths.get(CommonConstants.createTempFilePath(path));
        idFilePath = Paths.get(idPath);
    }

    @Override
    public User findById(long id) {

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            while((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                User candidateUser = parseUser(line);
                if(candidateUser.getId() == id) {
                    return candidateUser;
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("파일 읽는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    @Override
    public User findByEmail(String email) {

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            while((line = br.readLine()) != null) {
                User candidateUser = parseUser(line);
                if(candidateUser.getEmail().equals(email)) {
                    return candidateUser;
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("파일 읽는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(User user) {

        int id;
        try(BufferedReader br = new BufferedReader(new FileReader(idFilePath.toFile()))) {
            id = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        id++;
        try(BufferedWriter br = new BufferedWriter(new FileWriter(idFilePath.toFile()))) {
            br.write(Integer.toString(id));
            br.flush();
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath.toFile(), true))) {

            user.setId(id);
            String line = pasrseString(user);

            bw.newLine();
            bw.write(line);
            bw.flush();
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(User user) {

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);

            while((line = br.readLine()) != null) {
                User candidateUser = parseUser(line);
                if(candidateUser.equals(user)) {
                    line = pasrseString(user);
                }
                bw.newLine();
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("유저 데이터 업데이트 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

        try {
            if(!Files.deleteIfExists(csvFilePath)) {
                throw new IllegalStateException("기존 존재햐야하는 유저 데이터베이스 파일이 없습니다.");
            }
            if(!tempCsvFilePath.toFile().renameTo(csvFilePath.toFile())) {
                throw new IllegalStateException(
                        "새로 만들어진 유저 데이터베이스 파일 이름이 기존 유저 데이터베이스 파일 이름으로 변경할 수 없습니다."
                );
            }
        } catch (IOException e) {
            System.out.println("유저 데이터 업데이트 기능의 파일 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }


    }

    @Override
    public void delete(long id) {

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);

            while((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                User candidateUser = parseUser(line);
                if(candidateUser.getId() == id) {
                    continue;
                }
                bw.newLine();
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("유저 데이터 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

        try {
            if(!Files.deleteIfExists(csvFilePath)) {
                throw new IllegalStateException("기존 존재햐야하는 유저 데이터베이스 파일이 없습니다.");
            }
            if(!tempCsvFilePath.toFile().renameTo(csvFilePath.toFile())) {
                throw new IllegalStateException(
                        "새로 만들어진 유저 데이터베이스 파일 이름이 기존 유저 데이터베이스 파일 이름으로 변경할 수 없습니다."
                );
            }
        } catch (IOException e) {
            System.out.println("유저 데이터 삭제 기능의 파일 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }



    private User parseUser(String line) {

        String[] columns = line.split(CSV_COLUMN_SEPARATOR);
        StringBuilder password = new StringBuilder();
        for(int i=2;i<columns.length-2;i++) {
            password.append(columns[i]);
            password.append(CSV_COLUMN_SEPARATOR);
        }
        password.delete(password.length()-1, password.length());

        User user = new User();
        user.setId(Long.parseLong(columns[0]));
        user.setEmail(columns[1]);
        user.setPassword(password.toString());
        user.setRole(UserRole.valueOf(columns[columns.length-2]));
        user.setPoints(Long.parseLong(columns[columns.length-1]));

        return user;
    }

    private String pasrseString(User user) {

        return user.getId() + CSV_COLUMN_SEPARATOR +
                user.getEmail() + CSV_COLUMN_SEPARATOR +
                user.getPassword() + CSV_COLUMN_SEPARATOR +
                user.getRole().toString() + CSV_COLUMN_SEPARATOR +
                user.getPoints();
    }
}
