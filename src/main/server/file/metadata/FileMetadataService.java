package main.server.file.metadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static main.server.file.FileConstants.DEFAULT_FILE_STORE_PATH;
import static main.server.file.FileConstants.DEFAULT_PAGING_SIZE;

public class FileMetadataService {

    private final Map<String /* file path */, Boolean> lock = new ConcurrentHashMap<>();
    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadataService(FileMetadataRepository fileMetadataRepository) {

        this.fileMetadataRepository = fileMetadataRepository;
    }

    public String create(RequestFileMetadataCreateDto request) {

        String savingFilePath;

        // 파일 경로 겹치지 않기 위한 사전 작업 (lock 얻기)
        while(true) {
            savingFilePath = createPath(request.getFileName());
            if(lock.put(savingFilePath, true) != null) {
                continue;
            }
            if(fileMetadataRepository.findByPath(savingFilePath) != null) {
                lock.remove(savingFilePath);
                continue;
            }
            break;
        }

        FileMetadata fileMetadata = FileMetadata.init(request.getSubject(), request.getDescription(),
                request.getPrice(), request.getUserId(), savingFilePath, request.getSize());

        fileMetadataRepository.save(fileMetadata);

        // 후처리 작업 (lock 해제)
        lock.remove(savingFilePath);

        return savingFilePath;
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


    public void update(RequestFileMetadataUpdateDto request) throws IllegalAccessException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(request.getRequiredFileId());
        if(fileMetadata == null) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        if(request.getUserId() != fileMetadata.getUserId()) {
            throw new IllegalAccessException("해당 파일메타데이터의 변경에 대한 접근 권한이 없는 유저입니다.");
        }
        fileMetadata.update(request.getSubject().replaceAll(",", " "), request.getDescription(), request.getPrice());
        fileMetadataRepository.update(fileMetadata);
    }


    public void delete(RequestFileMetadataDeleteDto request) throws IllegalAccessException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(request.getFileId());
        if(fileMetadata == null) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        if(request.getUserId() != fileMetadata.getUserId()) {
            throw new IllegalAccessException("해당 파일메타데이터의 변경에 대한 접근 권한이 없는 유저입니다.");
        }
        fileMetadataRepository.delete(request.getFileId());
    }

    public void deleteAllFromUser(long userId) {

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

    private String createPath(String fileName) {

        try{
            Path directoryPath = Paths.get(DEFAULT_FILE_STORE_PATH, UUID.randomUUID().toString());
            Files.createDirectories(directoryPath);

            return Paths.get(directoryPath.toString(), fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
