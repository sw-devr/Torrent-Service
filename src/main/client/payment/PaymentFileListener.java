package main.client.payment;

import main.client.common.SocketClientHandler;
import main.protocol.*;
import main.server.payment.RequestPurchaseFileDto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_DOWNLOAD_URL;

public class PaymentFileListener implements ActionListener {

    private final String sessionId;

    public PaymentFileListener(String sessionId) {
        this.sessionId = sessionId;
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



            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_DOWNLOAD_URL);
            request.setHeader(header);
            request.setBody(requestBody);


            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                String downloadPath = response.getHeader().get(SocketHeaderType.DOWNLOAD_PATH_URL);
                // downloadFrame 으로 이동

            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
