package server.file;

import java.util.List;

public class CSVFileMetadataRepository implements FileMetadataRepository{

    @Override
    public FileDto findById(long id) {
        return null;
    }

    @Override
    public List<FileDto> findMany(int offset, int size) {
        return null;
    }

    @Override
    public void save(FileDto fileMetadata) {

    }

    @Override
    public void update(FileDto fileMetadata) {

    }

    @Override
    public void delete(long id) {

    }
}
