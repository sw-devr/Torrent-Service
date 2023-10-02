package test.server.user;

import main.server.user.CSVUserRepository;
import main.server.user.User;
import main.server.user.UserRepository;
import main.server.user.UserRole;

import java.io.*;
import java.nio.file.Paths;

import static main.server.common.CommonConstants.CSV_COLUMN_SEPARATOR;

public class CSVUserRepositoryTest {

    private static final String HOME_PATH = Paths.get("").toAbsolutePath().toString();
    private static final String USER_DATABASE_FILE_PATH = Paths.get(HOME_PATH, "src/test/server/user/user_database_test.txt").toString();
    private static final String USER_ID_DATABASE_FILE_PATH = Paths.get(HOME_PATH, "src/test/server/user/user_id_database_test.txt").toString();
    private static final UserRepository userRepository = new CSVUserRepository(USER_DATABASE_FILE_PATH, USER_ID_DATABASE_FILE_PATH);

    public static void main(String[] args) {

        CSVUserRepositoryTest test = new CSVUserRepositoryTest();

        for(Long userToFindById : UserTestCase.getUserListToFindById()) {
            test.testFindById(userToFindById);
        }

        for(String userToFindByEmail : UserTestCase.getUserListToFindByEmail()) {
            test.testFindByEmail(userToFindByEmail);
        }

        for(User notExistingUser : UserTestCase.getUserListToSave()) {
            test.testSave(notExistingUser);
        }

        for(User userToUpdate : UserTestCase.getUserListToUpdate()) {
            test.testUpdate(userToUpdate);
        }

        for(Long userToDelete : UserTestCase.getUserListToDelete()) {
            test.testDelete(userToDelete);
        }
    }



    /**
     * findById test : 요청 id 값에 일치하는 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
     */
     private void testFindById(long id) {

        //when
        User user = userRepository.findById(id);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(USER_DATABASE_FILE_PATH))) {

            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                User candidateUser = parseUser(line);
                if(id != candidateUser.getId()) {
                    continue;
                }
                if(!candidateUser.equals(user)) {
                    throw new IllegalStateException("정확한 값을 파싱하지 못했습니다");
                }
                System.out.printf("id : %d 로 유저 찾기 테스트 케이스 성공 user : %s\n", id, user);
                return;
            }
            if(user != null) {
                throw new IllegalStateException("잘못된 값을 가져오고 있습니다.");
            }
            System.out.printf("id : %d 로 유저 찾기 테스트 케이스 성공 user : %s\n", id, user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * findByEmail test : 요청 email에 일치하는 유저 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
    */
    private void testFindByEmail(String email) {

        //when
        User user = userRepository.findByEmail(email);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(USER_DATABASE_FILE_PATH))) {

            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                User candidateUser = parseUser(line);
                if(!candidateUser.getEmail().equals(email)) {
                    continue;
                }
                if(!candidateUser.equals(user)) {
                    throw new IllegalStateException("정확한 값을 파싱하지 못했습니다");
                }
                System.out.printf("email : %s 로 유저 찾기 테스트 케이스 성공 user = %s\n", email, user);
                return;
            }
            if(user != null) {
                throw new IllegalStateException("잘못된 값을 가져오고 있습니다.");
            }
            System.out.printf("email : %s 로 유저 찾기 테스트 케이스 성공 user = %s\n", email, user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * save test : 요청 유저 데이터를 저장한다.
     */
    private void testSave(User requestUser) {

        //given
        if(userRepository.findByEmail(requestUser.getEmail()) != null) {
            throw new IllegalArgumentException("존재하지 않아야하는 유저데이터로 테스트를 진행해야 합니다");
        }

        //when
        userRepository.save(requestUser);

        //then
        User savedUser = userRepository.findById(requestUser.getId());
        if(!savedUser.equals(requestUser)) {
            throw new IllegalStateException("저장시도한 값과 저장된 값이 다릅니다.");
        }
        try(BufferedReader br = new BufferedReader(new FileReader(USER_ID_DATABASE_FILE_PATH))) {
            long id = Long.parseLong(br.readLine())-1;
            if((id != requestUser.getId()) && (requestUser.getId() == savedUser.getId())) {
                throw new IllegalStateException("자동 id 생성 로직에 문제가 있습니다.");
            }
            if((id == requestUser.getId()) && (requestUser.getId() != savedUser.getId())) {
                throw new IllegalStateException("자동 id 생성 로직에 문제가 있습니다.");
            }
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        System.out.println("저장 테스트 성공 " + savedUser);

        //후처리
        userRepository.delete(requestUser.getId());
        long id;
        try(BufferedReader br = new BufferedReader(new FileReader(USER_ID_DATABASE_FILE_PATH))) {
            id = Long.parseLong(br.readLine());
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        try(BufferedWriter br = new BufferedWriter(new FileWriter(USER_ID_DATABASE_FILE_PATH))) {
            br.write(Long.toString(id-1));
            br.flush();
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    /**
     * update test : 업데이트 요청시 이미 있는 유저는 정상적으로 업데이트되어 true를 리턴, 없으면 변화 없이 false를 리턴.
     */
    private void testUpdate(User user) {

        //given
        User beforeUser = userRepository.findById(user.getId());

        //when
        boolean isUpdated = userRepository.update(user);

        //then
        User changedUser = userRepository.findById(user.getId());

        if(beforeUser == null && isUpdated) {
            throw new IllegalStateException("존재하지 않는 유저가 업데이트 되었다고 결과를 출력합니다");
        }
        if(beforeUser != null && !isUpdated) {
            throw new IllegalStateException("존재하는 유저가 업데이트 되지 않았다고 결과를 출력합니다.");
        }
        if(changedUser != null && !changedUser.equals(user)) {
            throw new IllegalStateException("업데이트 전과 업데이트 후의 유저가 같은 유저여야 합니다.");
        }
        System.out.printf("업데이트 테스트 케이스 1 성공 before user : %s => after user : %s\n", beforeUser, changedUser);

        //후처리
        if(beforeUser != null) {
            userRepository.update(beforeUser);
        }
    }


    /**
     * delete test : 삭제 요청시 이미 존재하는 유저면 삭제하고 true를 리턴, 없다면 변화 없이 false 리턴
     */
    private void testDelete(long id) {

        //given
        User beforeUser = userRepository.findById(id);

        //when
        boolean isDeleted = userRepository.delete(id);

        //then
        User afterUser = userRepository.findById(id);
        if(beforeUser != null && !isDeleted) {
            throw new IllegalStateException("삭제되어야할 유저 데이터가 삭제되지 않았습니다.");
        }
        if(beforeUser == null && isDeleted) {
            throw new IllegalStateException("존재하지 않는 유저 데이터를 삭제했다는 메세지가 나왔습니다.");
        }
        if(afterUser != null) {
            throw new IllegalStateException("존재해서는 안되는 유저가 존재합니다.");
        }
        System.out.println("삭제 테스트 케이스 1 성공 " + beforeUser);

        //후처리
        if(beforeUser != null) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(USER_DATABASE_FILE_PATH, true))) {

                String line = pasrseString(beforeUser);

                bw.newLine();
                bw.write(line);
                bw.flush();
            } catch (IOException e) {
                System.out.println("파일 쓰는 과정에서 예외가 발생함");
                throw new RuntimeException(e);
            }
        }

    }


    private User parseUser(String line) {

        String[] columns = line.split(",");
        StringBuilder password = new StringBuilder();
        for(int i=2;i<columns.length-2;i++) {
            password.append(columns[i]);
            password.append(",");
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
