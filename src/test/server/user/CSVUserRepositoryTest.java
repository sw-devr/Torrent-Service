package test.server.user;

import main.server.user.CSVUserRepository;
import main.server.user.UserDto;
import main.server.user.UserRepository;
import main.server.user.UserRole;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import static main.server.user.UserConstants.ALREADY_EXISTING_USER_MESSAGE;
import static main.server.user.UserConstants.NOT_EXISTING_USER_MESSAGE;

public class CSVUserRepositoryTest {

    private static final String PATH = Path.of("").toString();
    private static final UserRepository userRepository = new CSVUserRepository(PATH);

    public static void main(String[] args) {

    }



    /**
     * findById test 1 : 요청 id 값에 일치하는 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
     */
     private void testFindById(long id) {

        //when
        UserDto user = userRepository.findById(id);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(PATH))) {

            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                UserDto candidateUser = parseUserDto(line);
                if(id != candidateUser.getId()) {
                    continue;
                }
                if(!candidateUser.equals(user)) {
                    throw new IllegalStateException("정확한 값을 파싱하지 못했습니다");
                }
                System.out.printf("id : %d 로 유저 찾기 테스트 케이스 성공\n", id);
                return;
            }
            if(user != null) {
                throw new IllegalStateException("잘못된 값을 가져오고 있습니다.");
            }
            System.out.printf("id : %d 로 유저 찾기 테스트 케이스 성공\n", id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * findByName test 1 : 요청 값 subject에 일치하는 file 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
    */
    private void testFindByEmail(String email) {

        //when
        UserDto user = userRepository.findByEmail(email);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(PATH))) {

            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                UserDto candidateUser = parseUserDto(line);
                if(!candidateUser.getEmail().equals(email)) {
                    continue;
                }
                if(!candidateUser.equals(user)) {
                    throw new IllegalStateException("정확한 값을 파싱하지 못했습니다");
                }
                System.out.printf("email : %s 로 유저 찾기 테스트 케이스 성공\n", email);
                return;
            }
            if(user != null) {
                throw new IllegalStateException("잘못된 값을 가져오고 있습니다.");
            }
            System.out.printf("email : %s 로 유저 찾기 테스트 케이스 성공\n", email);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * save test 1 : 요청 유저데이터가 기존에 존재하지 않는 데이터일 경우 저장에 성공한다.
     */
    private void testSaveWithValidParam(UserDto requestUser) {

        //given
        if(userRepository.findById(requestUser.getId()) != null ||
                userRepository.findByEmail(requestUser.getEmail()) != null) {
            throw new IllegalArgumentException("존재하지 않아야하는 유저데이터로 테스트를 진행해야 합니다");
        }

        //when
        userRepository.save(requestUser);

        //then
        UserDto savedUser = userRepository.findById(requestUser.getId());
        if(!savedUser.equals(requestUser)) {
            throw new IllegalStateException("저장시도한 값과 저장된 값이 다릅니다.");
        }
        System.out.println("저장 테스트 케이스 1 성공");
    }


    /**
     *  save test 2 : 이미 존재하는 유저 email, id로 저장을 시도할 경우 예외가 발생한다.
     */
    private void testSaveWithAlreadyExistingUser(UserDto user) {

        //given
        if(userRepository.findById(user.getId()) == null
                && userRepository.findByEmail(user.getEmail()) == null) {
            throw new IllegalArgumentException("이미 존재하는 유저로 테스트를 진행해야 합니다.");
        }

        //when & then
        try{
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(ALREADY_EXISTING_USER_MESSAGE)) {
                System.out.println("저장 테스트 케이스 2 성공");
                return;
            }
        }
        throw new IllegalStateException("이미 존재하는 유저에 대한 예외 처리가 실패했습니다.");
    }


    /**
     * update test 1 : 이미 존재하는 유저에 대해 업데이트 요청시 정상적으로 업데이트 된다.
     */
    private void testUpdateWithValidParam(UserDto user) {

        //given
        UserDto beforeUser = userRepository.findById(user.getId());
        if(!beforeUser.equals(user)) {
            throw new IllegalArgumentException("특정 유저의 기존데이터와는 다른 데이터로 테스트를 해야합니다.");
        }

        //when
        userRepository.update(user);

        //then
        UserDto changedUser = userRepository.findById(user.getId());
        if(changedUser == null) {
            throw new IllegalStateException("업데이트 되어야하는 데이터가 존재하지 않습니다.");
        }
        if(changedUser.equals(beforeUser)) {
            throw new IllegalStateException("업데이트 전과 업데이트 후가 달라야하지만 같습니다.");
        }
        System.out.println("업데이트 테스트 케이스 1 성공");
    }


    /**
     * update test 2 : 존재하지 않는 데이터를 업데이트 시도한다면 예외가 발생한다.
     */
    private void testUpdateWithNotExistingUser(UserDto user) {

        //given
        UserDto beforeUser = userRepository.findById(user.getId());
        if(beforeUser != null) {
            throw new IllegalArgumentException("저장소에 없어야 할 파일 메타데이터가 저장되어 있습니다.");
        }

        //when & then
        try{
            userRepository.update(user);
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(NOT_EXISTING_USER_MESSAGE)) {
                System.out.println("업데이트 테스트 케이스 2 성공");
                return;
            }
        }
        throw new IllegalStateException("존재하지 않는 유저에 대한 예외처리가 실패했습니다.");
    }


    /**
     * delete test 1 : 존재하는 유저를 삭제시도한다면 삭제된다.
     */
    private void testDeleteWithExistingUser(long id) {

        //given
        UserDto user = userRepository.findById(id);
        if(user == null) {
            throw new IllegalArgumentException("존재하는 유저로 테스트를 해야합니다.");
        }

        //when
        userRepository.delete(id);

        //then
        user = userRepository.findById(id);
        if(user != null) {
            throw new IllegalStateException("삭제되어야할 파일 메타데이터가 삭제되지 않았습니다.");
        }
        System.out.println("삭제 테스트 케이스 1 성공");
    }


    /**
     * delete test 2 : 존재하지 않는 유저를 삭제 시도한다면 예외가 발생한다.
     */
    private void testDeleteWithNotExistingUser(long id) {

        //given
        UserDto user = userRepository.findById(id);
        if(user != null) {
            throw new IllegalArgumentException("존재하지 않는 유저로 테스트를 해야합니다.");
        }

        //when & then
        try{
            userRepository.delete(id);
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(NOT_EXISTING_USER_MESSAGE)) {
                System.out.println("삭제 테스트 케이스 2 성공");
                return;
            }
        }
        throw new IllegalStateException("존재하지 않는 유저에 대한 예외 처리가 실패했습니다.");
    }


    private UserDto parseUserDto(String line) {

        String[] columns = line.split(",");
        StringBuilder password = new StringBuilder();
        for(int i=2;i<columns.length-2;i++) {
            password.append(columns[i]);
        }

        UserDto user = new UserDto();
        user.setId(Long.parseLong(columns[0]));
        user.setEmail(columns[1]);
        user.setPassword(password.toString());
        user.setRole(UserRole.valueOf(columns[columns.length-2]));
        user.setPoints(Long.parseLong(columns[columns.length-1]));

        return user;
    }
}
