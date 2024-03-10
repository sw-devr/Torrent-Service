package main.client.file.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.ui.FileTransferProgressBarFrame;
import main.protocol.*;
import main.server.payment.RequestRefundDto;
import main.server.security.CipherWorker;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_DOWNLOAD_URL;
import static main.protocol.ProtocolConstants.REFUND_FILE_URL;
import static main.server.common.CommonConstants.DEFAULT_BUFFER_SIZE;

public class FileDownloadHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final String downloadAuthorityToken;
    private final String clientDownloadPath;

    public FileDownloadHandler(String sessionId, String downloadAuthorityToken, String clientDownloadPath) {
        this.sessionId = sessionId;
        this.downloadAuthorityToken = downloadAuthorityToken;
        this.clientDownloadPath = clientDownloadPath;
    }

    public void getDownload() {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.DOWNLOAD_AUTHORITY_TOKEN.getValue(), downloadAuthorityToken);
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_DOWNLOAD_URL);
            request.setHeader(header);

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                handleSuccess(response, socketHandler);
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
            socketHandler.close();
        }
        catch (IllegalStateException ex) {
            handleFailure();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleFailure() {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestRefundDto requestBody = new RequestRefundDto();
            requestBody.setDownloadFilePath(downloadAuthorityToken);

            SocketRequest request = new SocketRequest();
            request.setUrl(REFUND_FILE_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();
            //응답 처리

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void handleSuccess(SocketResponse response, SocketClientHandler socketHandler) throws IOException {

        if(response.getStatusCode() == Status.SUCCESS.getCode()) {

            BufferedInputStream socketReader = (BufferedInputStream)response.getBody();
            int bodySize = response.getBodySize();

            downloadFile(socketReader, bodySize, clientDownloadPath);
            SocketResponse rs = socketHandler.receiveResponse();

            System.out.println(rs);
            socketHandler.close();
        }else if(response.getStatusCode() == Status.BAD_REQUEST.getCode()) {
            JOptionPane.showMessageDialog(null, "다운로드할 파일 경로가 잘못됨");
        }
    }

    private void downloadFile(BufferedInputStream socketReader, int bodySize, String clientDownloadPath) {

        FileTransferProgressBarFrame fileTransferProgressBarFrame = new FileTransferProgressBarFrame(bodySize);
        fileTransferProgressBarFrame.setVisible(true);

        int totalSize = 0;
        try(BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(clientDownloadPath))) {
            while(true) {
                byte[] decryptedBuffer = new byte[DEFAULT_BUFFER_SIZE];
                byte[] encryptedBuffer = CipherWorker.encrypt(decryptedBuffer);

                int size = socketReader.read(encryptedBuffer);
                if(size == -1) {
                    fileTransferProgressBarFrame.setVisible(false);
                    break;
                }
                decryptedBuffer = CipherWorker.decrypt(Arrays.copyOf(encryptedBuffer, size));
                fileWriter.write(decryptedBuffer);
                fileWriter.flush();

                totalSize += decryptedBuffer.length;
                fileTransferProgressBarFrame.add(decryptedBuffer.length);

                if(totalSize >= bodySize) {
                    fileTransferProgressBarFrame.setVisible(false);
                    break;
                }
            }
            JOptionPane.showMessageDialog(null, "파일 다운로드 성공");
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }
}
