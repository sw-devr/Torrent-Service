package main.client.user.listener;

import main.client.common.SocketClientHandler;
import main.client.user.ui.StartPageFrame;
import main.protocol.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.USER_DELETE_URL;

public class UserWithdrawListener implements ActionListener {

    private final String sessionId;
    private final JFrame mainFrame;

    public UserWithdrawListener(String sessionId, JFrame mainFrame) {
        this.sessionId = sessionId;
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            SocketRequest request = new SocketRequest();
            request.setUrl(USER_DELETE_URL);
            request.setHeader(header);

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                JOptionPane.showMessageDialog(null, response.getBody());
                StartPageFrame startPageFrame = new StartPageFrame();
                mainFrame.setVisible(false);
            } else {
                //로그아웃 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
