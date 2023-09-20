package main.client.file.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.handler.FileUploadHandler;
import main.client.file.ui.FileMainFrame;
import main.client.user.handler.UserHandler;
import main.protocol.*;
import main.server.file.RequestCreateFileMetadataDto;
import main.server.user.ResponseUserDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_METADATA_CREATE_URL;
import static main.protocol.SocketHeaderType.UPLOAD_PATH_URL;

public class FileMetadataCreateListener implements ActionListener {

    private static final int MAX_FILE_SIZE = Integer.MAX_VALUE;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final JTextField subjectTextField;
    private final JTextField descriptionTextField;
    private final JTextField priceTextField;
    private final JTextField filepathTextField;
    private final Component beforeFrame;


    public FileMetadataCreateListener(String sessionId, JTextField subjectTextField, JTextField descriptionTextField,
                                      JTextField priceTextField, JTextField filepathTextField, Component beforeFrame) {
        this.sessionId = sessionId;
        this.subjectTextField = subjectTextField;
        this.descriptionTextField = descriptionTextField;
        this.priceTextField = priceTextField;
        this.filepathTextField = filepathTextField;
        this.beforeFrame = beforeFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            File uploadFile = validateFilePath();
            UserHandler userHandler = new UserHandler();
            ResponseUserDto user = userHandler.getUser(sessionId);

            SocketClientHandler socketHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestCreateFileMetadataDto requestBody = new RequestCreateFileMetadataDto();
            requestBody.setUserId(user.getUserId());
            requestBody.setSubject(subjectTextField.getText());
            requestBody.setDescription(descriptionTextField.getText());
            requestBody.setPrice(Integer.parseInt(priceTextField.getText()));
            requestBody.setFileName(uploadFile.getName());
            requestBody.setSize((int)uploadFile.length());

            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_METADATA_CREATE_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                //업로드 시작
                String uploadPath = response.getHeader().get(UPLOAD_PATH_URL.getValue());

                FileUploadHandler uploadHandler = new FileUploadHandler(sessionId, uploadPath, uploadFile.getPath());
                SocketResponse uploadResponse = uploadHandler.startUpload();

                JOptionPane.showMessageDialog(null, response.getBody());
                if(uploadResponse.getStatusCode() == Status.SUCCESS.getCode()) {
                    beforeFrame.setVisible(false);
                    new FileMainFrame(sessionId, "all", null, 0);
                } else {

                }
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private File validateFilePath() {

        Path uploadFilePath = Paths.get(filepathTextField.getText());
        File uploadFile = uploadFilePath.toFile();
        if(!uploadFile.exists()) {
            throw new IllegalArgumentException("잘못된 업로드 파일 경로입니다.");
        }
        if(!uploadFile.isFile()) {
            throw new IllegalArgumentException("잘못된 업로드 파일 경로입니다.");
        }
        if(uploadFile.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 사이즈가 너무 큽니다.");
        }
        return uploadFile;
    }
}
