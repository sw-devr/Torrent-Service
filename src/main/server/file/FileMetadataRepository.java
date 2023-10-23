package main.server.file;

import java.util.List;

public interface FileMetadataRepository {

    void save(FileMetadata fileMetadata);

    FileMetadata findById(long id);

    FileMetadata findByPath(String filePath);

    List<FileMetadata> findBySubject(String fileName, int offset, int defaultPagingSize);

    List<FileMetadata> findByUserId(long userId, int offset, int defaultPagingSize);

    List<FileMetadata> findAll(int offset, int size);

    boolean update(FileMetadata fileMetadata);

    boolean delete(long id);

    boolean deleteAll(List<Long> fileIds);

}
