package main.client.file.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.ui.FileMainFrame;
import main.client.file.ui.ModifierFrame;
import main.protocol.*;
import main.server.file.metadata.RequestFileMetadataUpdateDto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_METADATA_UPDATE_URL;

public class FileMetadataUpdateListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final long fileId;
    private final long userId;
    private final JComponent beforeFrame;
    private final ModifierFrame modifierFrame;
    private final JTextField priceTextField;
    private final JTextField subjectTextField;
    private final JTextArea descriptionTextArea;

    public FileMetadataUpdateListener(String sessionId, long fileId, long userId, JTextField priceTextField, JTextField subjectTextField, JTextArea descriptionTextArea, JComponent beforeFrame, ModifierFrame modifierFrame) {

        this.sessionId = sessionId;
        this.fileId = fileId;
        this.userId = userId;
        this.beforeFrame = beforeFrame;
        this.modifierFrame = modifierFrame;
        this.priceTextField = priceTextField;
        this.subjectTextField = subjectTextField;
        this.descriptionTextArea = descriptionTextArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestFileMetadataUpdateDto requestBody = new RequestFileMetadataUpdateDto();
            requestBody.setRequiredFileId(fileId);
            requestBody.setUserId(userId);
            requestBody.setPrice(Integer.parseInt(priceTextField.getText()));
            requestBody.setSubject(subjectTextField.getText());
            requestBody.setDescription(descriptionTextArea.getText());

            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_METADATA_UPDATE_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                JOptionPane.showMessageDialog(null, response.getBody());

                FileMainFrame mainFrame = new FileMainFrame(sessionId, "all", null, 0);
                modifierFrame.setVisible(false);
                beforeFrame.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, response.getBody());
            }

        }
        catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "올바르지 못한 입력 값");
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
}
