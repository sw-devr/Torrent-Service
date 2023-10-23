package test.server.file;

import main.server.file.FileMetadata;
import main.server.file.FileState;

import java.util.ArrayList;
import java.util.List;

public class FileMetadataTestCase {

    public static List<String> getFilePaths() {

        List<String> filePaths = new ArrayList<>();
        filePaths.add("C:\\Users\\Public\\Documents\\자기소개서 합격본.pdf");           // 존재하는 파일 경로
        filePaths.add("C:\\Users\\Public\\Documents\\존재하지 않는 경로데이터.pdf");     // 존재하지 않는 파일 경로

        return filePaths;
    }

    public static List<FileMetadata> getUpdateRequests() {

        List<FileMetadata> requests = new ArrayList<>();

        FileMetadata request1 = new FileMetadata();     //존재하는 파일메타데이터
        request1.setId(16L);
        request1.setUserId(3L);
        request1.setPrice(3000);
        request1.setPath("C:\\Users\\Public\\Documents\\11.txt");
        request1.setSubject("수정안");
        request1.setDescription("수정 디스크립션");
        request1.setSize(1024);
        request1.setCreatedTimestamp(1600000000L);
        request1.setLastUpdatedTimestamp(1600000000L);
        request1.setDownloadCnt(10);
        request1.setState(FileState.AVAILABLE);

        FileMetadata request2 = new FileMetadata();     //존재하지 않는 파일메타데이터
        request2.setId(250L);
        request2.setUserId(3L);
        request2.setPrice(3000);
        request2.setPath("C:\\Users\\Public\\Documents\\11.txt");
        request2.setSubject("수정안");
        request2.setDescription("수정 디스크립션");
        request2.setSize(1024);
        request2.setCreatedTimestamp(1600000000L);
        request2.setLastUpdatedTimestamp(1600000000L);
        request2.setDownloadCnt(10);
        request2.setState(FileState.AVAILABLE);

        requests.add(request1);
        requests.add(request2);

        return requests;
    }

    public static List<Long> getDeleteRequests() {

        List<Long> requests = new ArrayList<>();
        requests.add(5L);       // 존재하는 ID
        requests.add(105L);     // 존재하지 않는 ID

        return requests;
    }
}
