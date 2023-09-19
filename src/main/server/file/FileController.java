package main.server.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;
import main.server.user.UserService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.List;

import static main.protocol.ContentType.STREAM;
import static main.protocol.ProtocolConstants.FILE_METADATA_FIND_ALL_URL;
import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.*;
import static main.server.common.CommonConstants.FILE_SERVICE;
import static main.server.common.CommonConstants.USER_SERVICE;

public class FileController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileService fileService = FILE_SERVICE;
    private final UserService userService = USER_SERVICE;

    @Mapping("/file/upload")
    public SocketResponse upload(SocketRequest request) {

        if(!request.getHeader().get(CONTENT_TYPE.getValue()).equals(STREAM.getValue())) {
            throw new IllegalArgumentException("요청 헤더 타입이 잘못되었습니다.");
        }
        String path = request.getHeader().get(UPLOAD_PATH_URL.getValue());
        fileService.upload((BufferedInputStream) request.getBody(), path);

        return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "성공적으로 업로드 파일은 받았습니다.");
    }

    @Mapping("/file/download")
    public SocketResponse download(SocketRequest request) {

        if(!request.getHeader().get(CONTENT_TYPE.getValue()).equals(STREAM.getValue())) {
            throw new IllegalArgumentException("요청 헤더 타입이 잘못되었습니다.");
        }
        String path = request.getHeader().get(DOWNLOAD_PATH_URL.getValue());
        fileService.download((BufferedOutputStream) request.getBody(), path);

        return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "성공적으로 다운로드 파일은 전송했습니다.");
    }

    @Mapping("/file/metadata/create")
    public SocketResponse createFileMetadata(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestCreateFileMetadataDto requestParam = objectMapper.readValue((String)request.getBody(), RequestCreateFileMetadataDto.class);
            if(requestParam.getUserId() != userService.currentUserId(sessionId)) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }
            String uploadPath = fileService.create(requestParam);
            request.getHeader().put(UPLOAD_PATH_URL.getValue(), uploadPath);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "파일 메타데이터 생성 성공");
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping(FILE_METADATA_FIND_ALL_URL)
    public SocketResponse searchAll(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestSearchAllDto requestParam = objectMapper.readValue((String)request.getBody(), RequestSearchAllDto.class);
            List<FileMetadata> list = fileService.findAll(requestParam.getOffset() , requestParam.getSize());
            String body = objectMapper.writeValueAsString(list);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), body);
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/file/metadata/search/user")
    public SocketResponse searchFromUserFiles(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);
            RequestSearchFromUserDto requestParam = objectMapper.readValue((String)request.getBody(), RequestSearchFromUserDto.class);

            if(requestParam.getUserId() != userService.currentUserId(sessionId)) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            List<FileMetadata> list = fileService.findByUser(requestParam.getUserId(), requestParam.getOffset());
            String body = objectMapper.writeValueAsString(list);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), body);
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/file/metadata/search/subject")
    public SocketResponse searchFromSubject(SocketRequest request) {

        try{
            validateSession(request.getHeader().get(SESSION_ID.getValue()));

            RequestSearchFromSubjectDto requestParam = objectMapper.readValue((String)request.getBody(), RequestSearchFromSubjectDto.class);

            List<FileMetadata> list = fileService.findBySubject(requestParam.getSubject(), requestParam.getOffset());
            String body = objectMapper.writeValueAsString(list);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), body);
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/file/metadata/update")
    public SocketResponse updateFileMetadata(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            long userId = userService.currentUserId(sessionId);
            RequestUpdateFileMetadataDto requestParam = objectMapper.readValue((String)request.getBody(), RequestUpdateFileMetadataDto.class);
            if(userId != requestParam.getUserId()) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            fileService.update(requestParam);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "파일 업데이트 성공");
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/file/metadata/delete")
    public SocketResponse deleteFileMetadata(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            long userId = userService.currentUserId(sessionId);
            RequestDeleteFileMetadataDto requestParam = objectMapper.readValue((String)request.getBody(), RequestDeleteFileMetadataDto.class);
            if(userId != requestParam.getUserId()) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            fileService.delete(requestParam);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "파일 업데이트 성공");
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }



    private void validateSession(String sessionId) {

        if(!userService.isLogin(sessionId)) {
            throw new IllegalArgumentException("이미 로그인 되어 있지 않습니다.");
        }
    }
}
