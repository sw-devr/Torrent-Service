package main.server.file;

import java.util.List;

public interface FileMetadataRepository {

    FileMetadata findById(long id);

    FileMetadata findByPath(String filePath);

    List<FileMetadata> findBySubject(String fileName, int offset, int defaultPagingSize);

    List<FileMetadata> findByUserId(long userId, int offset, int defaultPagingSize);

    List<FileMetadata> findAll(int offset, int size);

    void save(FileMetadata fileMetadata);

    void update(FileMetadata fileMetadata);

    void delete(long id);

    void deleteAll(List<Long> fileIds);

}
