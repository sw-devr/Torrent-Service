package main.client.file.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import main.client.common.SocketClientHandler;
import main.protocol.*;
import main.server.file.metadata.FileMetadata;
import main.server.file.metadata.RequestFileMetadataSearchAllDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.server.file.FileConstants.DEFAULT_PAGING_SIZE;

public class AllFileSearcher implements FileSearcher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<FileMetadata> getFileMetadataList(String sessionId, int offset) {

        try {
            SocketClientHandler socketClientHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestFileMetadataSearchAllDto requestBody = new RequestFileMetadataSearchAllDto();
            requestBody.setOffset(offset);
            requestBody.setSize(DEFAULT_PAGING_SIZE);

            SocketRequest request = new SocketRequest();
            request.setUrl(ProtocolConstants.FILE_METADATA_FIND_ALL_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketClientHandler.sendRequest(request);
            SocketResponse response = socketClientHandler.receiveResponse();

            socketClientHandler.close();
            if (response.getStatusCode() == Status.SUCCESS.getCode()) {
                CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, FileMetadata.class);
                return objectMapper.readValue((String)response.getBody(), listType);
            }else {
                System.out.println(response.getBody());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
