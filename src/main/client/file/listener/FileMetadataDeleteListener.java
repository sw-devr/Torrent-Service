package main.client.file.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.client.file.ui.FileMainFrame;
import main.client.file.ui.ModifierFrame;
import main.protocol.*;
import main.server.file.metadata.RequestFileMetadataDeleteDto;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.ProtocolConstants.FILE_METADATA_DELETE_URL;

public class FileMetadataDeleteListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final long fileId;
    private final long userId;
    private final JComponent beforeFrame;
    private final ModifierFrame modifierFrame;

    public FileMetadataDeleteListener(String sessionId, long fileId, long userId, JComponent beforeFrame, ModifierFrame modifierFrame) {

        this.sessionId = sessionId;
        this.fileId = fileId;
        this.userId = userId;
        this.beforeFrame = beforeFrame;
        this.modifierFrame = modifierFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int code = JOptionPane.showConfirmDialog(null, "삭제 하시겠습니까?", "삭제 확인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if(code != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            SocketClientHandler socketHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestFileMetadataDeleteDto requestBody = new RequestFileMetadataDeleteDto();
            requestBody.setFileId(fileId);
            requestBody.setUserId(userId);

            SocketRequest request = new SocketRequest();
            request.setUrl(FILE_METADATA_DELETE_URL);
            request.setHeader(header);
            request.setBody(objectMapper.writeValueAsString(requestBody));

            socketHandler.sendRequest(request);
            SocketResponse response = socketHandler.receiveResponse();
            socketHandler.close();

            //후처리
            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                JOptionPane.showMessageDialog(null, response.getBody());
                new FileMainFrame(sessionId, "all", null, 0);
                modifierFrame.setVisible(false);
                beforeFrame.setVisible(false);
            } else {
                //로그인 시도 실패 메세지 콘솔 띄우기
                JOptionPane.showMessageDialog(null, response.getBody());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
}
