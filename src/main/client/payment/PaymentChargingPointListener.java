package main.client.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.ui.FileMainFrame;
import main.protocol.*;
import main.server.payment.RequestChargePointDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.CHARGING_POINTS_URL;

public class PaymentChargingPointListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final long userId;
    private final JTextField chargingTextField;
    private final Component beforeMainFrame;

    public PaymentChargingPointListener(String sessionId, long userId, JTextField chargingTextField, Component beforeMainFrame) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.chargingTextField = chargingTextField;
        this.beforeMainFrame = beforeMainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        try {
            SocketClientHandler socketHandler = new SocketClientHandler();
            int chargingPoints = Integer.parseInt(chargingTextField.getText());

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestChargePointDto requestBody = new RequestChargePointDto();
            requestBody.setUserId(userId);
            requestBody.setAddingPoints(chargingPoints);

            SocketRequest request = new SocketRequest();
            request.setUrl(CHARGING_POINTS_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                String sessionId = response.getHeader().get(SocketHeaderType.SESSION_ID.getValue());

                JOptionPane.showMessageDialog(null, response.getBody());
                FileMainFrame mainFrame = new FileMainFrame(sessionId, "all", null, 0);
                beforeMainFrame.setVisible(false);
            } else if(response.getStatusCode() == Status.BAD_REQUEST.getCode()) {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "잘못된 충전 금액입니다.");
        }
    }

    private void validateChargingPoints(String chargingPoints) {


    }
}
