package main.client.file.listener;

import main.client.common.SocketClientHandler;
import main.client.file.ui.ProgressBarFrame;
import main.protocol.*;
import main.server.security.CipherWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_DOWNLOAD_URL;
import static main.server.common.CommonConstants.DEFAULT_BUFFER_SIZE;

public class FileDownloadListener implements ActionListener {

    private final String sessionId;
    private final String downloadPath;
    private final String currentPath;

    public FileDownloadListener(String sessionId, String downloadPath, String currentPath) {
        this.sessionId = sessionId;
        this.downloadPath = downloadPath;
        this.currentPath = currentPath;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.STREAM.getValue());
            header.put(SocketHeaderType.DOWNLOAD_PATH_URL.getValue(), downloadPath);

            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_DOWNLOAD_URL);
            request.setHeader(header);

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();

            System.out.println(response.getStatusCode());
            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                handleSuccess(response);
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
            socketHandler.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void handleSuccess(SocketResponse response) {

        int bodySize = response.getBodySize();
        BufferedInputStream socketReader = (BufferedInputStream) response.getBody();

        ProgressBarFrame progressBarFrame = new ProgressBarFrame(sessionId, bodySize, downloadPath);

        int totalSize = 0;
        try(BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(currentPath))) {
            while(true) {
                byte[] decryptedBuffer = new byte[DEFAULT_BUFFER_SIZE];
                byte[] encryptedBuffer = CipherWorker.encrypt(decryptedBuffer);

                int size = socketReader.read(encryptedBuffer);

                decryptedBuffer = CipherWorker.decrypt(Arrays.copyOf(encryptedBuffer, size));
                fileWriter.write(decryptedBuffer);

                totalSize += decryptedBuffer.length;
                progressBarFrame.setProgress(decryptedBuffer.length);

                if(totalSize == bodySize) {
                    progressBarFrame.setVisible(false);
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

        JOptionPane.showMessageDialog(null, "파일 다운로드 성공");
        //FileMainFrame fileMainFrame = new FileMainFrame(sessionId, "all", 0);

    }
}
