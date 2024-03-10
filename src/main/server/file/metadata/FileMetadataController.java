package main.server.file.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;
import main.server.user.UserService;

import java.util.List;

import static main.protocol.ProtocolConstants.*;
import static main.protocol.ProtocolConstants.FILE_METADATA_DELETE_URL;
import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.SESSION_ID;
import static main.protocol.SocketHeaderType.UPLOAD_PATH_URL;
import static main.server.common.CommonConstants.*;

public class FileMetadataController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileMetadataService fileService = FILE_METADATA_SERVICE;
    private final UserService userService = USER_SERVICE;

    @Mapping(FILE_METADATA_CREATE_URL)
    public SocketResponse createFileMetadata(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestFileMetadataCreateDto requestParam = objectMapper.readValue((String)request.getBody(), RequestFileMetadataCreateDto.class);
            if(requestParam.getUserId() != userService.findUserIdBySessionId(sessionId)) {
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

            RequestFileMetadataSearchAllDto requestParam = objectMapper.readValue((String)request.getBody(), RequestFileMetadataSearchAllDto.class);
            List<FileMetadata> list = fileService.findAll(requestParam.getOffset() , requestParam.getSize());
            String body = objectMapper.writeValueAsString(list);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), body);
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping(FILE_METADATA_FIND_FROM_USER_URL)
    public SocketResponse searchFromUserFiles(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);
            RequestFileMetadataSearchFromUserDto requestParam = objectMapper.readValue((String)request.getBody(), RequestFileMetadataSearchFromUserDto.class);

            if(requestParam.getUserId() != userService.findUserIdBySessionId(sessionId)) {
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

    @Mapping(FILE_METADATA_FIND_FROM_SUBJECT_URL)
    public SocketResponse searchFromSubject(SocketRequest request) {

        try{
            validateSession(request.getHeader().get(SESSION_ID.getValue()));

            RequestFileMetadataSearchFromSubjectDto requestParam = objectMapper.readValue((String)request.getBody(), RequestFileMetadataSearchFromSubjectDto.class);

            List<FileMetadata> list = fileService.findBySubject(requestParam.getSubject(), requestParam.getOffset());
            String body = objectMapper.writeValueAsString(list);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), body);
        } catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        } catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping(FILE_METADATA_UPDATE_URL)
    public SocketResponse updateFileMetadata(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            long userId = userService.findUserIdBySessionId(sessionId);
            RequestFileMetadataUpdateDto requestParam = objectMapper.readValue((String)request.getBody(), RequestFileMetadataUpdateDto.class);
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

    @Mapping(FILE_METADATA_DELETE_URL)
    public SocketResponse deleteFileMetadata(SocketRequest request) {

        try{
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            long userId = userService.findUserIdBySessionId(sessionId);
            RequestFileMetadataDeleteDto requestParam = objectMapper.readValue((String)request.getBody(), RequestFileMetadataDeleteDto.class);
            if(userId != requestParam.getUserId()) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            fileService.delete(requestParam);

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "파일 삭제 성공");
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
