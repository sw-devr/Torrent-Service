package test.server.file;

import main.server.file.metadata.CSVFileMetadataRepository;
import main.server.file.metadata.FileMetadata;
import main.server.file.metadata.FileMetadataRepository;
import main.server.file.metadata.FileState;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static main.server.common.CommonConstants.CSV_COLUMN_SEPARATOR;

public class CSVFileMetadataRepositoryTest {

    private static final String HOME_PATH = Paths.get("").toAbsolutePath().toString();
    private static final String FILE_METADATA_DATABASE_FILE_PATH = Paths.get(HOME_PATH, "src/test/server/file/file_metadata_database_test.txt").toString();
    private static final String FILE_METADATA_ID_DATABASE_FILE_PATH = Paths.get(HOME_PATH, "src/test/server/file/file_metadata_id_database_test.txt").toString();
    private static final Path TEMP_FILE_METADATA_DATABASE_FILE_PATH = Paths.get(HOME_PATH, "src/test/server/file/tt_file_metadata_database_test.txt");
    private static final Path TEMP_FILE_METADATA_ID_DATABASE_FILE_PATH = Paths.get(HOME_PATH, "src/test/server/file/tt_file_metadata_id_database_test.txt");

    private static final FileMetadataRepository fileMetadataRepository = new CSVFileMetadataRepository(FILE_METADATA_DATABASE_FILE_PATH, FILE_METADATA_ID_DATABASE_FILE_PATH);

    public static void main(String[] args) throws IOException {

        CSVFileMetadataRepositoryTest test = new CSVFileMetadataRepositoryTest();

        //test Save
        test.testSave(FileMetadata.init("테스트", 500, 5L, "임시 파일 경로" ,1024));

        // test Find
        for(long id=1;id<16;id++) {
            test.testFindById(id);
        }
        for(String filePath : FileMetadataTestCase.getFilePaths()) {
            test.testFindByFilePath(filePath);
        }
        test.testFindBySubject("1", 2, 10);
        test.testFindByUserId(3, 1, 5);
        test.testFindAll(2, 7);

        // test Update
        for(FileMetadata request : FileMetadataTestCase.getUpdateRequests()) {
            test.testUpdate(request);
        }

        // test Delete
        for(Long fileMetadataId : FileMetadataTestCase.getDeleteRequests()) {
            test.testDelete(fileMetadataId);
        }

        // test DeleteAll
        test.testDeleteAll(FileMetadataTestCase.getDeleteRequests());

    }

    private void before() throws IOException {

        Files.copy(Path.of(FILE_METADATA_DATABASE_FILE_PATH), TEMP_FILE_METADATA_DATABASE_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Path.of(FILE_METADATA_ID_DATABASE_FILE_PATH), TEMP_FILE_METADATA_ID_DATABASE_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
    }

    private void after() throws IOException {

        Files.copy(TEMP_FILE_METADATA_DATABASE_FILE_PATH, Path.of(FILE_METADATA_DATABASE_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(TEMP_FILE_METADATA_ID_DATABASE_FILE_PATH, Path.of(FILE_METADATA_ID_DATABASE_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);

        Files.deleteIfExists(TEMP_FILE_METADATA_DATABASE_FILE_PATH);
        Files.deleteIfExists(TEMP_FILE_METADATA_ID_DATABASE_FILE_PATH);
    }


    /**
     * save test : 요청 파일 메타데이터를 저장한다.
     */
    private void testSave(FileMetadata requestFileMetadata) throws IOException {

        try {
            before();

            //when
            fileMetadataRepository.save(requestFileMetadata);

            //then
            System.out.println(requestFileMetadata.getId());
            FileMetadata savedFileMetadata = fileMetadataRepository.findById(requestFileMetadata.getId());
            if(!savedFileMetadata.equals(requestFileMetadata)) {
                throw new IllegalStateException("저장시도한 값과 저장된 값이 다릅니다.");
            }
            System.out.println("파일 메타데이터 저장 테스트 성공");
        }
        finally {
            after();
        }
    }


    /**
     *  id로 파일 메타데이터 조회 테스트 : 요청 id 값에 일치하는 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
     */
    private void testFindById(long id) {

        //given

        //when
        FileMetadata responseFileMetadata = fileMetadataRepository.findById(id);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_METADATA_DATABASE_FILE_PATH))) {

            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata realFileMetadata = parseFileDto(line);
                if(realFileMetadata.getId() != id) {
                    continue;
                }
                if(realFileMetadata.equals(responseFileMetadata)) {
                    System.out.println("id로 파일 메타데이터 조회 테스트 성공");
                    return;
                }
                throw new IllegalStateException(String.format("id로 파일 메타데이터 조회 테스트 실패 : 결과 값 : %s 과 실제 값 : %s 이 다릅니다. ", responseFileMetadata, realFileMetadata));
            }
            if(responseFileMetadata != null) {
                throw new IllegalStateException("id로 파일 메타데이터 조회 테스트 실패 : 존재하지 않는 데이터가 리턴되었습니다.");
            }
            System.out.println("id로 파일 메타데이터 조회 테스트 성공");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  파일 경로로 파일 메타데이터 조회 테스트 : 요청 file path 값에 일치하는 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
     */
    private void testFindByFilePath(String filePath) {

        //given

        //when
        FileMetadata responseFileMetadata = fileMetadataRepository.findByPath(filePath);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_METADATA_DATABASE_FILE_PATH))) {

            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata realFileMetadata = parseFileDto(line);
                if(!realFileMetadata.getPath().equals(filePath)) {
                    continue;
                }
                if(realFileMetadata.equals(responseFileMetadata)) {
                    if(responseFileMetadata == null) {
                        throw new IllegalStateException("파일 경로로 파일 메타데이터 조회 테스트 실패 : 존재하는 데이터가 조회되지 않았습니다.");
                    }
                    System.out.println("파일 경로로 파일 메타데이터 조회 테스트 성공 : 존재하는 파일 경로에 대해 해당 파일메타데이터를 리턴한다.");
                    return;
                }
                throw new IllegalStateException(String.format("파일 경로로 파일 메타데이터 조회 테스트 실패 : 결과 값 : %s 과 실제 값 : %s 이 다릅니다. ", responseFileMetadata, realFileMetadata));
            }
            if(responseFileMetadata != null) {
                throw new IllegalStateException("파일 경로로 파일 메타데이터 조회 테스트 실패 : 존재하지 않는 데이터가 리턴되었습니다.");
            }
            System.out.println("파일 경로로 파일 메타데이터 조회 테스트 성공 : 존재하지 않는 파일 경로에 대해 null 을 리턴한다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 특정 키워드가 제목에 포함된 파일 메타데이터 조회 테스트 : 요청 키워드값이 제목에 포함되는 파일메타데이터들을 조회한다.
     */
    private void testFindBySubject(String keyword, int offset, int size) {

        //given

        //when
        List<FileMetadata> responseFileMetadataList = fileMetadataRepository.findBySubject(keyword, offset, size);

        //then
        List<FileMetadata> realFileMetadataList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_METADATA_DATABASE_FILE_PATH))) {
            String line = br.readLine();
            int idx = 0;
            while((line = br.readLine()) != null) {
                FileMetadata realFileMetadata = parseFileDto(line);
                if(!realFileMetadata.getSubject().contains(keyword)) {
                    continue;
                }
                realFileMetadataList.add(realFileMetadata);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.reverse(realFileMetadataList);
        realFileMetadataList = realFileMetadataList.subList(Math.min(offset, realFileMetadataList.size()), Math.min(offset+size, realFileMetadataList.size()));

        if(realFileMetadataList.size() != responseFileMetadataList.size()) {
            throw new IllegalStateException("특정 키워드가 제목에 포함된 파일 메타데이터 조회 테스트 실패 : 요구 사항에 만족하지 못한 조회 결과입니다.");
        }
        for(int i=0;i<responseFileMetadataList.size();i++) {
            if(!realFileMetadataList.get(i).equals(responseFileMetadataList.get(i))) {
                throw new IllegalStateException("특정 키워드가 제목에 포함된 파일 메타데이터 조회 테스트 실패 : 요구 사항에 만족하지 못한 조회 결과입니다.");
            }
        }
        System.out.println("특정 키워드가 제목에 포함된 파일 메타데이터 조회 테스트 성공");
    }


    /**
     * 특정 유저의 파일 메타데이터 조회 테스트 : 특정 유저가 소유하고 있는 파일메타데이터들을 조회한다.
     */
    private void testFindByUserId(long userId, int offset, int size) {

        //given

        //when
        List<FileMetadata> responseFileMetadataList = fileMetadataRepository.findByUserId(userId, offset, size);

        //then
        List<FileMetadata> realFileMetadataList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_METADATA_DATABASE_FILE_PATH))) {
            String line = br.readLine();
            int idx = 0;
            while((line = br.readLine()) != null) {
                FileMetadata realFileMetadata = parseFileDto(line);
                if(realFileMetadata.getUserId() != userId) {
                    continue;
                }
                if(realFileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                realFileMetadataList.add(realFileMetadata);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.reverse(realFileMetadataList);
        realFileMetadataList = realFileMetadataList.subList(Math.min(offset, realFileMetadataList.size()), Math.min(offset+size, realFileMetadataList.size()));

        if(realFileMetadataList.size() != responseFileMetadataList.size()) {
            throw new IllegalStateException("특정 유저의 파일 메타데이터 조회 테스트 실패 : 요구 사항에 만족하지 못한 조회 결과입니다.");
        }
        for(int i=0;i<responseFileMetadataList.size();i++) {
            if(!realFileMetadataList.get(i).equals(responseFileMetadataList.get(i))) {
                throw new IllegalStateException(" 특정 유저의 파일 메타데이터 조회 테스트 실패 : 요구 사항에 만족하지 못한 조회 결과입니다. ");
            }
        }
        System.out.println("특정 유저의 파일 메타데이터 조회 테스트 성공");
    }


    /**
     * 모든 파일 메타데이터 조회 테스트 : 모든 파일메타데이터들을 조회한다.
     */
    private void testFindAll(int offset, int size) {

        //given

        //when
        List<FileMetadata> responseFileMetadataList = fileMetadataRepository.findAll(offset, size);

        //then
        List<FileMetadata> realFileMetadataList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_METADATA_DATABASE_FILE_PATH))) {
            String line = br.readLine();
            int idx = 0;
            while((line = br.readLine()) != null) {
                FileMetadata realFileMetadata = parseFileDto(line);

                if(realFileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                realFileMetadataList.add(realFileMetadata);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.reverse(realFileMetadataList);
        realFileMetadataList = realFileMetadataList.subList(Math.min(offset, realFileMetadataList.size()), Math.min(offset+size, realFileMetadataList.size()));

        if(realFileMetadataList.size() != responseFileMetadataList.size()) {
            System.out.println(realFileMetadataList.size() + " " + responseFileMetadataList.size());
            throw new IllegalStateException("모든 파일 메타데이터 조회 테스트 실패 : 요구 사항에 만족하지 못한 조회 결과입니다.");
        }
        for(int i=0;i<responseFileMetadataList.size();i++) {
            if(!realFileMetadataList.get(i).equals(responseFileMetadataList.get(i))) {
                throw new IllegalStateException("모든 파일 메타데이터 조회 테스트 실패 : 요구 사항에 만족하지 못한 조회 결과입니다. ");
            }
        }
        System.out.println("모든 파일 메타데이터 조회 테스트 성공");
    }


    /**
     * update test : 파일 메타데이터 업데이트 테스트
     */
    private void testUpdate(FileMetadata request) throws IOException {

        try {
            before();

            //when
            boolean isUpdated = fileMetadataRepository.update(request);

            //then
            FileMetadata changedFileMetadata = fileMetadataRepository.findById(request.getId());
            System.out.println(request);

            if(!isUpdated) {
                if(changedFileMetadata != null) {
                    throw new IllegalStateException("파일 메타데이터 업데이트 테스트 실패");
                }
                System.out.println("파일메타데이터 업데이트 테스트 성공 : 존재하지 않는 데이터를 업데이트 시도할 경우 false를 반환한다.");
            }else {
                if(changedFileMetadata == null) {
                    throw new IllegalStateException("업데이트 되어야하는 데이터가 존재하지 않습니다.");
                }
                if(!changedFileMetadata.equals(request)) {
                    throw new IllegalStateException("파일 메타데이터 업데이트 테스트 실패 : 요청 사항으로 업데이트 되지 않음" + changedFileMetadata + " \n" + request);
                }
                System.out.println("파일메타데이터 업데이트 테스트 성공 : 존재하는 데이터를 업데이트 시도할 경우 true를 반환하고 업데이트한다.");
            }

        } finally {
            after();
        }
    }


    /**
     * delete test : 파일 메타데이터 삭제 테스트
     */
    private void testDelete(long id) throws IOException {

        try {
            before();

            //given
            FileMetadata beforeFileMetadata = fileMetadataRepository.findById(id);

            //when
            boolean isDeleted = fileMetadataRepository.delete(id);

            //then
            if(fileMetadataRepository.findById(id) != null) {
                throw new IllegalStateException("파일메타데이터 삭제 테스트 실패 : 요청에 해당하는 파일메타데이터를 삭제하지 못함");
            }
            if(isDeleted) {
                if(beforeFileMetadata == null) {
                    throw new IllegalStateException("파일메타데이터 삭제 테스트 실패 : 존재하지 않는 데이터가 삭제되었다고 리턴됌");
                }
                System.out.println("파일메타데이터 삭제 테스트 성공 : 존재하는 데이터를 삭제 요청할 경우 true를 리턴하고 삭제한다.");
            }else {
                if(beforeFileMetadata != null) {
                    throw new IllegalStateException("파일메타데이터 삭제 테스트 실패 : 기존에 존재했던 데이터가 삭제되지 않았다고 리턴됌");
                }
                System.out.println("파일메타데이터 삭제 테스트 성공 : 존재하지 않는 데이터를 삭제 요청할 경우 false를 리턴한다.");
            }
        } finally {
            after();
        }

    }


    /**
     * deleteAll test : 파일 메타데이터 배치 삭제 테스트
     */
    private void testDeleteAll(List<Long> fileMetadataIdList) throws IOException {

        try {
            before();

            //given
            boolean isExistingAny = false;
            for(Long fileMetadataId : fileMetadataIdList) {
                if(fileMetadataRepository.findById(fileMetadataId) != null) {
                    isExistingAny = true;
                    break;
                }
            }

            //when
            boolean isDeleted = fileMetadataRepository.deleteAll(fileMetadataIdList);

            //then
            for(Long fileMetadataId : fileMetadataIdList) {
                if(fileMetadataRepository.findById(fileMetadataId) != null) {
                    throw new IllegalStateException("파일메타데이터 배치 삭제 테스트 실패 : 요청에 해당하는 파일메타데이터를 삭제하지 못함");
                }
            }
            if(isDeleted) {
                if(!isExistingAny) {
                    throw new IllegalStateException("파일메타데이터 삭제 테스트 실패 : 존재하지 않는 데이터가 삭제되었다고 리턴됌");
                }
                System.out.println("파일메타데이터 삭제 테스트 성공 : 존재하는 데이터를 삭제 요청할 경우 true를 리턴하고 삭제한다.");
            }else {
                if(isExistingAny) {
                    throw new IllegalStateException("파일메타데이터 삭제 테스트 실패 : 기존에 존재했던 데이터가 삭제되지 않았다고 리턴됌");
                }
                System.out.println("파일메타데이터 삭제 테스트 성공 : 존재하지 않는 데이터를 삭제 요청할 경우 false를 리턴한다.");
            }
        } finally {
            after();
        }

    }


    private FileMetadata parseFileDto(String line) {

        String[] columns = line.split(CSV_COLUMN_SEPARATOR);
        StringBuilder description = new StringBuilder();
        for(int i=5;i<columns.length-5;i++) {
            description.append(columns[i]);
            description.append(CSV_COLUMN_SEPARATOR);
        }
        description.delete(description.length()-1, description.length());

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(Long.parseLong(columns[0]));
        fileMetadata.setUserId(Long.parseLong(columns[1]));
        fileMetadata.setPrice(Integer.parseInt(columns[2]));
        fileMetadata.setPath(columns[3]);
        fileMetadata.setSubject(columns[4]);
        fileMetadata.setDescription(description.toString());
        fileMetadata.setSize(Integer.parseInt(columns[columns.length-5]));
        fileMetadata.setCreatedTimestamp(Long.parseLong(columns[columns.length-4]));
        fileMetadata.setLastUpdatedTimestamp(Long.parseLong(columns[columns.length-3]));
        fileMetadata.setDownloadCnt(Integer.parseInt(columns[columns.length-2]));
        fileMetadata.setState(FileState.valueOf(columns[columns.length-1]));

        return fileMetadata;
    }
}
