package test.server.file;

import main.server.file.metadata.RequestFileMetadataCreateDto;

public class FileServiceTest {

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
    private void testCreate(RequestFileMetadataCreateDto request) {

        //given

        //when

        //then
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
