package main.client.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.ContentType;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.server.security.CipherWorker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static main.client.common.ClientConstants.SERVER_IP_ADDRESS;
import static main.client.common.ClientConstants.SERVER_IP_PORT;
import static main.protocol.SocketHeaderType.CONTENT_TYPE;
import static main.protocol.SocketResponse.*;

public class SocketClientHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Socket socket;
    private final BufferedInputStream socketReader;
    private final BufferedOutputStream socketWriter;

    public SocketClientHandler() throws IOException {

        socket = new Socket(SERVER_IP_ADDRESS, SERVER_IP_PORT);
        socketReader = new BufferedInputStream(socket.getInputStream());
        socketWriter = new BufferedOutputStream(socket.getOutputStream());
    }

    public void sendRequest(SocketRequest request) throws IOException {
        System.out.println(request);

        byte[] decryptedURL = Arrays.copyOf(request.getUrl().getBytes(UTF_8), SocketRequest.URL_BYTE_SIZE);
        byte[] encryptedURL = CipherWorker.encrypt(decryptedURL);

        byte[] decryptedHeader = objectMapper.writeValueAsBytes(request.getHeader());
        byte[] encryptedHeader = CipherWorker.encrypt(decryptedHeader);

        byte[] decryptedBody = request.getBody() == null ? new byte[0] : ((String)request.getBody()).getBytes(UTF_8);
        byte[] encryptedBody = CipherWorker.encrypt(decryptedBody);

        byte[] decryptedHeaderSize = ByteBuffer.allocate(HEADER_BYTE_SIZE).putInt(decryptedHeader.length).array();
        byte[] encryptedHeaderSize = CipherWorker.encrypt(decryptedHeaderSize);

        byte[] decryptedBodySize = ByteBuffer.allocate(BODY_BYTE_SIZE).putInt(decryptedBody.length).array();
        byte[] encryptedBodySize = CipherWorker.encrypt(decryptedBodySize);

        socketWriter.write(encryptedHeaderSize);
        socketWriter.write(encryptedBodySize);
        socketWriter.write(encryptedURL);
        socketWriter.write(encryptedHeader);
        socketWriter.write(encryptedBody);
        socketWriter.flush();
    }

    public SocketResponse receiveResponse() throws IOException {

        //header size
        byte[] decryptedHeaderSize = new byte[HEADER_BYTE_SIZE];
        byte[] encryptedHeaderSize = CipherWorker.encrypt(decryptedHeaderSize);

        socketReader.read(encryptedHeaderSize);
        int headerSize = ByteBuffer.wrap(CipherWorker.decrypt(encryptedHeaderSize)).getInt();
        if(headerSize > MAX_ALLOWED_HEADER_SIZE) {
            // 예외 처리
        }

        //body size
        byte[] decryptedBodySize = new byte[BODY_BYTE_SIZE];
        byte[] encryptedBodySize = CipherWorker.encrypt(decryptedBodySize);

        socketReader.read(encryptedBodySize);
        int bodySize = ByteBuffer.wrap(CipherWorker.decrypt(encryptedBodySize)).getInt();
        if(headerSize > MAX_ALLOWED_BODY_SIZE) {
            // 예외 처리
        }

        //status code
        byte[] decryptedStatusCode = new byte[STATUS_CODE_BYTE_SIZE];
        byte[] encryptedStatusCode = CipherWorker.encrypt(decryptedStatusCode);

        socketReader.read(encryptedStatusCode);
        int statusCode = ByteBuffer.wrap(CipherWorker.decrypt(encryptedStatusCode)).getInt();

        //header
        byte[] decryptedHeader = new byte[headerSize];
        byte[] encryptedHeader = CipherWorker.encrypt(decryptedHeader);

        socketReader.read(encryptedHeader);
        Map<String, String> header = objectMapper.readValue(CipherWorker.decrypt(encryptedHeader), Map.class);

        System.out.println(header);
        Object body = null;
        if(header.get(CONTENT_TYPE.getValue()).equals(ContentType.STREAM.getValue())) {
            body = socketReader;
        } else if(header.get(CONTENT_TYPE.getValue()).equals(ContentType.JSON.getValue())) {

            byte[] decryptedBody = new byte[bodySize];
            byte[] encryptedBody = CipherWorker.encrypt(decryptedBody);

            socketReader.read(encryptedBody);
            body = new String(CipherWorker.decrypt(encryptedBody));
        }

        SocketResponse socketResponse = new SocketResponse();
        socketResponse.setHeaderSize(headerSize);
        socketResponse.setBodySize(bodySize);
        socketResponse.setStatusCode(statusCode);
        socketResponse.setHeader(header);
        socketResponse.setBody(body);

        return socketResponse;
    }

    public void close() throws IOException {
        this.socketReader.close();
        this.socketWriter.close();
        this.socket.close();
    }
}
