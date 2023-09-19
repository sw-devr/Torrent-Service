package main.client.file.search;

import main.server.file.FileMetadata;

import java.util.List;

public interface FileSearcher {


    List<FileMetadata> getFileMetadataList(String sessionId, int offset);
}
