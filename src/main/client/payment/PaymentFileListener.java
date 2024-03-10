package main.client.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.handler.FileDownloadHandler;
import main.protocol.*;
import main.server.common.CommonConstants;
import main.server.payment.RequestPurchaseFileDto;
import main.server.payment.ResponsePurchaseFileDto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.PURCHASE_FILE_URL;

public class PaymentFileListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final long fileId;
    private final long userId;


    public PaymentFileListener(String sessionId, long fileId, long userId) {
        this.sessionId = sessionId;
        this.fileId = fileId;
        this.userId = userId;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestPurchaseFileDto requestBody = new RequestPurchaseFileDto();
            requestBody.setFileId(fileId);
            requestBody.setUserId(userId);

            SocketRequest request = new SocketRequest();
            request.setUrl(PURCHASE_FILE_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                ResponsePurchaseFileDto responseBody = objectMapper.readValue((String)response.getBody(), ResponsePurchaseFileDto.class);
                String downloadFileAuthorityToken = responseBody.getDownloadFileAuthorityToken();
                String serverDownloadFilePath = responseBody.getDownloadFilePath();
                String clientDownloadPath = createUserPath(serverDownloadFilePath);

                FileDownloadHandler fileDownloadHandler = new FileDownloadHandler(sessionId, downloadFileAuthorityToken, clientDownloadPath);
                fileDownloadHandler.getDownload();
            } else {
                // 결제 실패시 에러 메세지 알림
                JOptionPane.showMessageDialog(null, response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String createUserPath(String downloadPath) {

        String[] paths = downloadPath.split(CommonConstants.PATH_REGEX);
        return Paths.get(System.getProperty("user.home"), paths[paths.length-1]).toString();
    }
}
