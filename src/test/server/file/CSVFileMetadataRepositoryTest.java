package test.server.file;

import main.server.file.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static main.server.file.FileConstants.ALREADY_EXISTING_FILE_NAME;
import static main.server.file.FileConstants.FILE_NOT_EXISTING_MESSAGE;

public class CSVFileMetadataRepositoryTest {

    private static final String path = "C:\\Users\\Public\\Downloads\\fileMetadata_test.txt";
    private static final FileMetadataRepository fileMetadataRepository = new CSVFileMetadataRepository(path);

    public static void main(String[] args) throws IOException {

        CSVFileMetadataRepositoryTest test = new CSVFileMetadataRepositoryTest();

        // testFindById
        for(long id=1;id<7;id++) {
            test.testFindById(id);
        }
        test.testFindById(1000000000000000L);


        // testFindByName
        String[] subjects = {"내가 좋아하는 노래", "제네릭리스트코드", "증명사진"};
        for(String subject : subjects) {
            test.testFindByName(subject);
        }

        // testFindMany
        test.testFindMany( 0, 100);
    }


    /**
     * findById test 1 : 요청 id 값에 일치하는 데이터가 존재할 경우 그 값을 리턴하고 없을 경우 null을 리턴한다.
     */
    private void testFindById(long id) {

        //given

        //when
        FileDto requestFile = fileMetadataRepository.findById(id);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {

            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                FileDto realFileDto = parseFileDto(line);
                if(id == realFileDto.getId()) {
                    if(!requestFile.equals(realFileDto)) {

                        System.out.println(realFileDto);
                        System.out.println(requestFile);
                        throw new IllegalStateException("정확한 값을 파싱하지 못했습니다");
                    }
                    System.out.printf("id : %d 로 유저 찾기 테스트 케이스 성공\n", id);
                    return;
                }
            }
            if(requestFile != null) {
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
    private void testFindByName(String subject) {

        //given

        //when
        FileDto requestFile = fileMetadataRepository.findByName(subject);

        //then
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
            String line;
            while((line = br.readLine()) != null) {
                FileDto realFileDto = parseFileDto(line);
                if(subject.equals(realFileDto.getSubject())) {
                    if(!requestFile.equals(realFileDto)) {
                        throw new IllegalStateException("정확한 값을 파싱하지 못했습니다");
                    }
                    System.out.printf("subject : %s 로 유저 찾기 테스트 케이스 성공\n", subject);
                    return;
                }
            }
            if(requestFile != null) {
                throw new IllegalStateException("잘못된 값을 가져오고 있습니다.");
            }
            System.out.printf("subject : %s 로 유저 찾기 테스트 케이스 성공\n", subject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * findMany test 1 : offset을 기준으로 최대 size 갯수 만큼 최신의 파일 메타데이터들을 가져온다.
     */
    private void testFindMany(int offset, int size) {

        //given

        //when
        List<FileDto> requestFileDtos = fileMetadataRepository.findMany(offset, size);

        //then
        for(FileDto fileDto : requestFileDtos) {
            System.out.println(fileDto);
        }
        int cnt = -1;
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            while(br.readLine() != null) {
                cnt++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<FileDto> olderRealFileDtos = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {

            int start = Math.max(1, cnt-offset-size);
            int end = Math.max(1, cnt-offset);

            for(int i=0;i<start;i++) {
                if(br.readLine() == null) break;
            }
            for(int i = start; i <= end; i++) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                olderRealFileDtos.add(parseFileDto(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<FileDto> realFileDtos = new ArrayList<>();
        for(int i=olderRealFileDtos.size()-1;i>=0;i--) {
            realFileDtos.add(olderRealFileDtos.get(i));
        }

        //verify
        if(requestFileDtos.size() != realFileDtos.size()) {
            throw new IllegalStateException("파싱이 잘못되었습니다.");
        }
        for(int i=0;i<requestFileDtos.size();i++) {
            if(!requestFileDtos.get(i).equals(realFileDtos.get(i))) {
                throw new IllegalStateException("파싱이 잘못되었습니다.");
            }
        }
    }


    /**
     * save test 1 : 요청 파일 메타데이터가 기존에 존재하지 않는 데이터일 경우 저장에 성공한다.
     */
    private void testSaveWithValidParam(FileDto requestFileDto) {

        //given
        if(fileMetadataRepository.findById(requestFileDto.getId()) != null) {
            throw new IllegalArgumentException("존재하지 않는 파일메타데이터 값으로 테스트해야합니다.");
        }

        //when & then
        fileMetadataRepository.save(requestFileDto);

        FileDto savedFileDto = fileMetadataRepository.findById(requestFileDto.getId());
        if(!savedFileDto.equals(requestFileDto)) {
            throw new IllegalStateException("저장시도한 값과 저장된 값이 다릅니다.");
        }
        System.out.println("저장 테스트 케이스 1 성공");
    }


    /**
     * save test 2 : 이미 존재하는 파일 데이터로 저장을 시도할 경우 예외가 발생한다.
     */
    private void testSaveWithAlreadyExistingFileData(FileDto fileDto) {

        //given
        if(fileMetadataRepository.findById(fileDto.getId()) == null) {
            throw new IllegalArgumentException("이미 존재하는 파일메타데이터 값으로 테스트해야합니다.");
        }

        //when & then
        try{
            fileMetadataRepository.save(fileDto);
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(ALREADY_EXISTING_FILE_NAME)) {
                System.out.println("저장 테스트 케이스 2 성공");
                return;
            }
        }
        throw new IllegalStateException("이미 존재하는 파일에 대한 중복 저장 예외 처리가 실패했습니다.");
    }


    /**
     * update test 1 : 기존 값이 존재하는 데이터에 대해 업데이트 요청시 정상적으로 업데이트 된다.
     */
    private void testUpdateWithValidParam(FileDto request) {

        //given
        FileDto beforeFileDto = fileMetadataRepository.findById(request.getId());
        if(!beforeFileDto.equals(request)) {
            throw new IllegalArgumentException("기존에 존재하는 값에서 수정된 부분이 있는 데이터로 테스트해야합니다.");
        }

        //when
        fileMetadataRepository.update(request);

        //then
        FileDto changedFileDto = fileMetadataRepository.findById(request.getId());
        if(changedFileDto == null) {
            throw new IllegalStateException("업데이트 되어야하는 데이터가 존재하지 않습니다.");
        }
        if(changedFileDto.equals(beforeFileDto)) {
            throw new IllegalStateException("업데이트 전과 업데이트 후가 달라야하지만 같습니다.");
        }
        System.out.println("업데이트 테스트 케이스 1 성공");
    }


    /**
     * update test 2 : 존재하지 않는 데이터를 업데이트 시도한다면 예외가 발생한다.
     */
    private void testUpdateWithNotExistingFilMetadata(FileDto request) {

        //given
        FileDto beforeFileDto = fileMetadataRepository.findById(request.getId());
        if(beforeFileDto != null) {
            throw new IllegalArgumentException("저장소에 없어야 할 파일 메타데이터가 저장되어 있습니다.");
        }

        //when & then
        try{
            fileMetadataRepository.update(request);
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(FILE_NOT_EXISTING_MESSAGE)) {
                System.out.println("업데이트 테스트 케이스 2 성공");
                return;
            }
        }
        throw new IllegalStateException("존재하지 않는 파일에 대한 업데이트 예외 처리가 실패했습니다.");
    }


    /**
     * delete test 1 : 존재하는 파일 데이터를 삭제시도한다면 삭제된다.
     */
    private void testDeleteWithExistingFileMetadata(long id) {

        //given
        FileDto fileDto = fileMetadataRepository.findById(id);
        if(fileDto == null) {
            throw new IllegalArgumentException("존재해야할 파일 메타데이터가 존재하지 않습니다.");
        }

        //when
        fileMetadataRepository.delete(id);
        fileDto = fileMetadataRepository.findById(id);
        if(fileDto != null) {
            throw new IllegalStateException("삭제되어야할 파일 메타데이터가 삭제되지 않았습니다.");
        }
        System.out.println("삭제 테스트 케이스 1 성공");
    }


    /**
     * delete test 2 : 존재하지 않는 파일 데이터를 삭제 시도한다면 예외가 발생한다.
     */
    private void testDeleteWithNotExistingFileMetadata(long id) {

        //when & then
        try{
            fileMetadataRepository.delete(id);
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(FILE_NOT_EXISTING_MESSAGE)) {
                System.out.println("삭제 테스트 케이스 2 성공");
                return;
            }
        }
        throw new IllegalStateException("존재하지 않는 파일에 대한 예외 처리가 실패했습니다.");
    }

    private FileDto parseFileDto(String line) {

        String[] columns = line.split(",");
        StringBuilder description = new StringBuilder();
        for(int i=5;i<columns.length-4;i++) {
            description.append(columns[i]);
        }

        FileDto fileDto = new FileDto();
        fileDto.setId(Long.parseLong(columns[0]));
        fileDto.setUserId(Long.parseLong(columns[1]));
        fileDto.setPrice(Integer.parseInt(columns[2]));
        fileDto.setPath(columns[3]);
        fileDto.setSubject(columns[4]);
        fileDto.setDescription(description.toString());
        fileDto.setSize(Integer.parseInt(columns[columns.length-4]));
        fileDto.setCreatedTimestamp(Long.parseLong(columns[columns.length-3]));
        fileDto.setDownloadCnt(Integer.parseInt(columns[columns.length-2]));
        fileDto.setState(FileState.valueOf(columns[columns.length-1]));

        return fileDto;
    }
}
