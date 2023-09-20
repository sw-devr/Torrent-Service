package main.client.file.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.protocol.ContentType;
import main.protocol.SocketHeaderType;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_UPLOAD_URL;
import static main.protocol.SocketHeaderType.SESSION_ID;

public class FileUploadHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final String uploadPath;
    private final String currentPath;


    public FileUploadHandler(String sessionId, String uploadPath, String currentPath) {
        this.sessionId = sessionId;
        this.uploadPath = uploadPath;
        this.currentPath = currentPath;
    }

    public SocketResponse startUpload() {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.STREAM.getValue());
            header.put(SocketHeaderType.UPLOAD_PATH_URL.getValue(), uploadPath);
            header.put(SESSION_ID.getValue(), sessionId);

            File uploadFile = Paths.get(currentPath).toFile();
            //파일 사이즈 검증 필요

            SocketRequest request = new SocketRequest();
            request.setBodySize((int)uploadFile.length());
            request.setUrl(FILE_UPLOAD_URL);
            request.setHeader(header);
            request.setBody(new BufferedInputStream(new FileInputStream(uploadFile)));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            return response;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
