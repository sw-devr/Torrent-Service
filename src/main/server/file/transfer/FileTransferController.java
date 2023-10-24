package main.server.file.transfer;

import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ContentType.JSON;
import static main.protocol.ContentType.STREAM;
import static main.protocol.ProtocolConstants.FILE_DOWNLOAD_URL;
import static main.protocol.ProtocolConstants.FILE_UPLOAD_URL;
import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.*;
import static main.server.common.CommonConstants.FILE_TRANSFER_SERVICE;

public class FileTransferController {

    private final FileTransferService fileTransferService = FILE_TRANSFER_SERVICE;

    @Mapping(FILE_UPLOAD_URL)
    public SocketResponse upload(SocketRequest request) {

        try {
            if (!request.getHeader().get(CONTENT_TYPE.getValue()).equals(STREAM.getValue())) {
                throw new IllegalArgumentException("요청 헤더 타입이 잘못되었습니다.");
            }
            String path = request.getHeader().get(UPLOAD_PATH_URL.getValue());
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            //비지니스 로직
            boolean isSuccess = fileTransferService.upload((BufferedInputStream) request.getBody(), path);

            HashMap<String, String> header = new HashMap<>();
            header.put(CONTENT_TYPE.getValue(), JSON.getValue());
            header.put(SESSION_ID.getValue(), sessionId);

            if (isSuccess) {
                return createResponse(Status.SUCCESS.getCode(), header, "성공적으로 업로드 파일은 받았습니다.");
            }
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), header, "파일 업로드 중 실패했습니다.");
        }
        catch (IllegalArgumentException e) {
            Map<String, String> header = new HashMap<>();
            header.put(CONTENT_TYPE.getValue(), JSON.getValue());
            header.put(SESSION_ID.getValue(), request.getHeader().get(SESSION_ID.getValue()));

            return createResponse(Status.BAD_REQUEST.getCode(), header, e.getMessage());
        }
    }


    @Mapping(FILE_DOWNLOAD_URL)
    public SocketResponse download(SocketRequest request) {

        try {
            if (!request.getHeader().get(CONTENT_TYPE.getValue()).equals(JSON.getValue())) {
                throw new IllegalArgumentException("요청 헤더 타입이 잘못되었습니다.");
            }
            String downloadToken = request.getHeader().get(DOWNLOAD_AUTHORITY_TOKEN.getValue());
            String sessionId = request.getHeader().get(SESSION_ID.getValue());

            //비지니스 로직
            fileTransferService.download((BufferedOutputStream) request.getBody(), downloadToken);

            Map<String, String> header = new HashMap<>();
            header.put(CONTENT_TYPE.getValue(), JSON.getValue());
            header.put(SESSION_ID.getValue(), sessionId);

            return createResponse(Status.SUCCESS.getCode(), header, "성공적으로 다운로드 파일은 전송했습니다.");
        }
        catch (IllegalStateException e) {
            Map<String, String> header = new HashMap<>();
            header.put(CONTENT_TYPE.getValue(), JSON.getValue());
            header.put(SESSION_ID.getValue(), request.getHeader().get(SESSION_ID.getValue()));

            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), header, "파일 다운로드 중 실패했습니다.");
        }
        catch (IllegalArgumentException e) {
            Map<String, String> header = new HashMap<>();
            header.put(CONTENT_TYPE.getValue(), JSON.getValue());
            header.put(SESSION_ID.getValue(), request.getHeader().get(SESSION_ID.getValue()));

            return createResponse(Status.BAD_REQUEST.getCode(), header, e.getMessage());
        }
        catch (IllegalAccessException e) {
            Map<String, String> header = new HashMap<>();
            header.put(CONTENT_TYPE.getValue(), JSON.getValue());
            header.put(SESSION_ID.getValue(), request.getHeader().get(SESSION_ID.getValue()));

            return createResponse(Status.FORBIDDEN.getCode(), header, e.getMessage());
        }
    }
}
