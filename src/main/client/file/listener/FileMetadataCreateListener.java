package main.client.file.listener;

import main.client.common.SocketClientHandler;
import main.client.file.ui.ProgressBarFrame;
import main.protocol.*;
import main.server.file.RequestCreateFileMetadataDto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_DOWNLOAD_URL;
import static main.protocol.SocketHeaderType.UPLOAD_PATH_URL;

public class FileMetadataCreateListener implements ActionListener {

    private static final int MAX_FILE_SIZE = Integer.MAX_VALUE;
    private final String sessionId;
    private final JTextField subjectTextField;
    private final JTextField descriptionTextField;
    private final JTextField priceTextField;
    private final JTextField filepathTextField;


    public FileMetadataCreateListener(String sessionId, JTextField subjectTextField, JTextField descriptionTextField,
                                      JTextField priceTextField, JTextField filepathTextField) {
        this.sessionId = sessionId;
        this.subjectTextField = subjectTextField;
        this.descriptionTextField = descriptionTextField;
        this.priceTextField = priceTextField;
        this.filepathTextField = filepathTextField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            File uploadFile = validateFilePath();

            SocketClientHandler socketHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestCreateFileMetadataDto requestBody = new RequestCreateFileMetadataDto();
            requestBody.setSubject(subjectTextField.getText());
            requestBody.setDescription(descriptionTextField.getText());
            requestBody.setPrice(Integer.parseInt(priceTextField.getText()));
            requestBody.setFileName(uploadFile.getName());
            requestBody.setSize((int)uploadFile.length());

            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_DOWNLOAD_URL);
            request.setHeader(header);
            request.setBody(requestBody);

            System.out.println(request);

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                //업로드 시작
                String uploadPath = response.getHeader().get(UPLOAD_PATH_URL.getValue());

                ProgressBarFrame progressBarFrame = new ProgressBarFrame(sessionId, (int)uploadFile.length(), uploadPath);
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
            socketHandler.close();
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
