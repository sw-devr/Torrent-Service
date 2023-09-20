package main.client.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.ui.FileMainFrame;
import main.protocol.*;
import main.server.payment.RequestPurchaseAuthorityDto;
import main.server.user.UserRole;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.PURCHASE_AUTHORITY_URL;

public class PaymentUploaderListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final long userId;
    private final JFrame beforeFrame;

    public PaymentUploaderListener(String sessionId, long userId, JFrame beforeFrame) {

        this.sessionId = sessionId;
        this.userId = userId;
        this.beforeFrame = beforeFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestPurchaseAuthorityDto requestBody = new RequestPurchaseAuthorityDto();
            requestBody.setUserId(userId);
            requestBody.setRole(UserRole.UPLOADER);

            SocketRequest request = new SocketRequest();
            request.setUrl(PURCHASE_AUTHORITY_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                JOptionPane.showMessageDialog(null, response.getBody());
                new FileMainFrame(sessionId, "all", null, 0);
                beforeFrame.setVisible(false);
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
