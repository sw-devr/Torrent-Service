package main.server.file.transfer;

import main.server.file.metadata.FileMetadata;
import main.server.file.metadata.FileMetadataRepository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class FileTransferService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileTransferRepository fileTransferRepository;
    private final FileDownloadAuthorityRepository fileDownloadAuthorityRepository;

    public FileTransferService(FileMetadataRepository fileMetadataRepository,
                               FileTransferRepository fileTransferRepository,
                               FileDownloadAuthorityRepository fileDownloadAuthorityRepository) {

        this.fileMetadataRepository = fileMetadataRepository;
        this.fileTransferRepository = fileTransferRepository;
        this.fileDownloadAuthorityRepository = fileDownloadAuthorityRepository;
    }

    public boolean upload(BufferedInputStream socketReader, String path) {

        FileMetadata fileMetadata = fileMetadataRepository.findByPath(path);
        if(fileMetadata == null) {
            throw new IllegalArgumentException("해당 경로에 대해 존재하지 않는 파일 메타데이터입니다.");
        }
        try{
            fileTransferRepository.receive(socketReader, path, fileMetadata.getSize());

            // 파일메타데이터 상태 available로 변경
            fileMetadata.completeFileUpload();
            fileMetadataRepository.update(fileMetadata);

            return true;
        }
        catch (IllegalStateException e) {
            return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void download(BufferedOutputStream socketWriter, String downloadAuthorityToken) throws IllegalAccessException {

        String filePath = fileDownloadAuthorityRepository.getDownloadFilePath(downloadAuthorityToken);
        if(filePath == null) {
            throw new IllegalAccessException("파일 다운로드에 대한 접근 권한이 없습니다.");
        }
        FileMetadata fileMetadata = fileMetadataRepository.findByPath(filePath);
        if(fileMetadata == null) {
            throw new IllegalArgumentException("해당 경로에 대해 존재하지 않는 파일 메타데이터입니다");
        }

        fileTransferRepository.send(socketWriter, filePath);

        // 다운로드 횟수 증가
        fileMetadata.increaseDownloadCnt();
        fileMetadataRepository.update(fileMetadata);

        // 다운로드 권한 회수
        fileDownloadAuthorityRepository.removeAuthority(downloadAuthorityToken);
    }
}
