package test.server.file;

import main.server.file.*;
import main.server.user.CSVUserRepository;
import main.server.user.UserRepository;

import java.util.List;

import static main.server.common.CommonConstants.*;
import static main.server.file.FileConstants.DEFAULT_PAGING_SIZE;

public class FileServiceTest {

    private final FileMetadataRepository fileMetadataRepository = new CSVFileMetadataRepository("C:\\Users\\Public\\Downloads\\file_metadata_database_test.txt", DEFAULT_FILE_METADATA_ID_PATH);
    private final FileRepository fileRepository = new StreamFileRepository();
    private final UserRepository userRepository = new CSVUserRepository("C:\\Users\\Public\\Downloads\\user_database_test.txt", DEFAULT_USER_DATABASE_PATH);
    private final FileService fileService = new FileService(fileMetadataRepository, fileRepository, userRepository);

    public static void main(String[] args) {

    }

    /**
     *  test Upload 1 : 유효한 파일 위치로 업로드 요청시 파일이 저장되고 메타데이터가 AVAILABLE로 갱신된다.
     */
    private void testUploadWithValidParam() {


        //when

    }

    /**
     *  test Upload 2 : 이미 존재하는 경로로 업로드 요청시 예외가 발생한다.
     */
    private void testUploadWithAlreadyExistingPath() {

    }


    /**
     *  test Download 1 : 존재하는 파일 경로로 다운로드 요청시 다운로드가 성공한다.
     */
    private void testDownloadWithAlreadyExistingPath() {

    }


    /**
     *  test Download 2 : 존재하지 않는 파일 경로로 다운로드 요청시 예외가 발생한다.
     */
    private void testDownloadWithNotExistingPath() {

    }


    /**
     *  test Create : 저장소에 존재하지 않는 파일 메타데이터로 요청시 저장된다.
     */
    private void testCreate(RequestCreateFileMetadataDto request) {

        //given

        //when
        fileService.create(request);

        //then
        int offset = 0;
        while(true) {
            List<FileMetadata> fileMetadataList = fileMetadataRepository.findBySubject(request.getSubject(), offset, DEFAULT_PAGING_SIZE);
            if(fileMetadataList.isEmpty()) {
                break;
            }
            for(FileMetadata fileMetadata : fileMetadataList) {
                String[] savedFilePath = fileMetadata.getPath().split(PATH_REGEX);
                String savedFileName = savedFilePath[savedFilePath.length-1];

                if(request.getSubject().equals(fileMetadata.getSubject()) &&
                        request.getDescription().equals(fileMetadata.getDescription()) &&
                        request.getSize() == fileMetadata.getSize() &&
                        request.getPrice() == fileMetadata.getPrice() &&
                        request.getFileName().equals(savedFileName)) {
                    System.out.println("정상적으로 값이 저장되었습니다.");
                    System.out.println("create test 성공");

                    fileMetadataRepository.delete(fileMetadata.getId());
                    return;
                }
            }

            offset += fileMetadataList.size();
        }
        throw new IllegalStateException("파일 메타데이터 저장이 실패했습니다.");
    }


    /**
     *  test findAll : 파라미터 조건에 따라 데이터가 존재한다면 값들을 가져오고 없다면 empty list를 리턴한다.
     */
    private void testFindAll() {

    }


    /**
     *  test findBySubject 1 :
     */
    private void testFindBySubject() {

    }


    /**
     *  test findBySubject 1 :
     */
    private void testUpdate() {

    }
}
