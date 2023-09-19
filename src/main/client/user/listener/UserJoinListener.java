package main.client.user.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.user.StartPageFrame;
import main.protocol.*;
import main.server.user.RequestJoinDto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.USER_JOIN_URL;

public class UserJoinListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JTextField emailTextField;
    private final JPasswordField passwordTextField;
    private final JFrame userJoinFrame;

    public UserJoinListener(JTextField emailTextField, JPasswordField passwordTextField, JFrame userJoinFrame) {

        this.emailTextField = emailTextField;
        this.passwordTextField = passwordTextField;
        this.userJoinFrame = userJoinFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            //전처리
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());

            RequestJoinDto requestBody = new RequestJoinDto();
            requestBody.setEmail(emailTextField.getText());
            requestBody.setPassword(new String(passwordTextField.getPassword()));

            SocketRequest request = new SocketRequest();
            request.setUrl(USER_JOIN_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                JOptionPane.showMessageDialog(null, response.getBody());

                StartPageFrame startMainFrame = new StartPageFrame();
                userJoinFrame.setVisible(false);
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
