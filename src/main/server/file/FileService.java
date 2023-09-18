package main.server.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static main.server.file.FileConstants.*;

public class FileService {

    FileMetadataRepository fileMetadataRepository;
    FileRepository fileRepository;

    public FileService(FileMetadataRepository fileMetadataRepository, FileRepository fileRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileRepository = fileRepository;
    }

    public boolean upload(BufferedInputStream socketReader, String path) {

        try{
            fileRepository.receive(socketReader, path);
            completeFileUpload(path);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void download(BufferedOutputStream socketWriter, String filePath) {

        fileRepository.send(socketWriter, filePath);
        //다운로드 횟수 증가
        FileMetadata fileMetadata = fileMetadataRepository.findByPath(filePath);
        fileMetadata.increaseDownloadCnt();
        fileMetadataRepository.update(fileMetadata);
    }

    public void create(RequestCreateFileMetadataDto request) {

        FileMetadata fileMetadata = FileMetadata.init(request.getSubject(), request.getDescription(),
                request.getPrice(), request.getUserId(), request.getFileName(), request.getSize());

        fileMetadataRepository.save(fileMetadata);
    }

    public List<FileMetadata> findAll(int offset, int size) {

        return fileMetadataRepository.findAll(offset, size);
    }

    public List<FileMetadata> findBySubject(String fileSubject, int offset) {

        return fileMetadataRepository.findBySubject(fileSubject, offset, DEFAULT_PAGING_SIZE);
    }

    public List<FileMetadata> findByUser(long userId, int offset) {

        return fileMetadataRepository.findByUserId(userId, offset, DEFAULT_PAGING_SIZE);
    }


    public void update(RequestUpdateFileMetadataDto request) throws IllegalAccessException {

        checkAuthority(request.getUserId(), request.getRequiredFileId());

        FileMetadata fileMetadata = fileMetadataRepository.findById(request.getRequiredFileId());
        if(fileMetadata == null) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        fileMetadata.update(request.getSubject(), request.getDescription(), request.getPrice());
        fileMetadataRepository.update(fileMetadata);
    }


    public void delete(RequestDeleteFileMetadataDto request) throws IllegalAccessException {

        checkAuthority(request.getUserId(), request.getFileId());

        fileMetadataRepository.delete(request.getFileId());
    }

    public void deleteFromUser(long userId) {

        int offset = 0;
        while(true) {
            List<FileMetadata> fileMetadataList = fileMetadataRepository.findByUserId(userId, offset, DEFAULT_PAGING_SIZE);

            if(fileMetadataList.isEmpty()) {
                break;
            }
            List<Long> fileIds = fileMetadataList
                    .stream()
                    .map(FileMetadata::getId)
                    .collect(Collectors.toList());

            fileMetadataRepository.deleteAll(fileIds);
            offset += fileIds.size();
        }
    }



    private void completeFileUpload(String filePath) throws IllegalAccessException {

        FileMetadata fileMetadata = fileMetadataRepository.findByPath(filePath);
        if(fileMetadata == null) {
            throw new IllegalArgumentException("이미 존재해야할 파일 정보가 없습니다.");
        }
        fileMetadata.completeFileUpload();
    }

    private String createPath(String fileName) {

        return Paths.get(DEFAULT_FILE_STORE_PATH, UUID.randomUUID().toString(), fileName).toString();
    }


    private void checkAuthority(long userId, long fileId) throws IllegalAccessException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId);

        if(fileMetadata == null) {
            throw new IllegalArgumentException("존재하지 않는 파일에 대한 접근입니다.");
        }
        if(fileMetadata.getUserId() != userId) {
            throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
        }
    }
}
