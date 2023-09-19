package main.client.file.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.common.SocketClientHandler;
import main.protocol.*;
import main.server.payment.RequestPurchaseFileDto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMetadataFindAllListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;

    public FileMetadataFindAllListener(String sessionId) {
        this.sessionId = sessionId;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            SocketClientHandler socketClientHandler = new SocketClientHandler();

            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
            header.put(SocketHeaderType.SESSION_ID.getValue(), sessionId);

            RequestPurchaseFileDto requestBody = new RequestPurchaseFileDto();





            SocketRequest request = new SocketRequest();
            request.setUrl(ProtocolConstants.FILE_METADATA_FIND_ALL_URL);

            socketClientHandler.sendRequest(request);
            SocketResponse response = socketClientHandler.receiveResponse();

            if(response.getStatusCode() == Status.SUCCESS.getCode()) {
                //FileSearchingPage

            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
