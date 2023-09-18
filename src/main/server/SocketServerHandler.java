package main.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.ContentType;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;
import main.server.security.CipherWorker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.CONTENT_TYPE;
import static main.protocol.SocketRequest.*;

public class SocketServerHandler implements Runnable {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Socket socket;
    private final URLMapper urlMapper;

    public SocketServerHandler(Socket socket, URLMapper urlMapper) {
        this.socket = socket;
        this.urlMapper = urlMapper;
    }


    @Override
    public void run() {

        try (BufferedInputStream socketReader = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream socketWriter = new BufferedOutputStream(socket.getOutputStream())) {

            SocketRequest socketRequest = receiveRequest(socketReader);

            SocketResponse socketResponse = handleRequest(socketRequest);

            sendResponse(socketWriter, socketResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SocketRequest receiveRequest(BufferedInputStream socketReader) throws IOException {

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
        int bodySize = ByteBuffer.wrap(CipherWorker.decrypt(encryptedHeaderSize)).getInt();
        if(headerSize > MAX_ALLOWED_BODY_SIZE) {
            // 예외 처리
        }

        //url
        byte[] decryptedURL = new byte[URL_BYTE_SIZE];
        byte[] encryptedURL = CipherWorker.encrypt(decryptedURL);

        socketReader.read(encryptedURL);
        String url = new String(CipherWorker.decrypt(encryptedURL));

        //header
        byte[] decryptedHeader = new byte[headerSize];
        byte[] encryptedHeader = CipherWorker.encrypt(decryptedHeader);

        socketReader.read(encryptedHeader);
        Map<String, String> header = objectMapper.readValue(CipherWorker.decrypt(encryptedHeader), Map.class);

        SocketRequest socketRequest = new SocketRequest();
        socketRequest.setHeaderSize(headerSize);
        socketRequest.setBodySize(bodySize);
        socketRequest.setUrl(url);
        socketRequest.setHeader(header);

        if(header.get(CONTENT_TYPE.getValue()).equals(ContentType.STREAM.getValue())) {
            socketRequest.setBody(socketReader);
        } else if(header.get(CONTENT_TYPE.getValue()).equals(ContentType.JSON.getValue())) {

            byte[] decryptedBody = new byte[bodySize];
            byte[] encryptedBody = CipherWorker.encrypt(decryptedBody);

            socketReader.read(encryptedBody);
            String body = new String(CipherWorker.decrypt(encryptedBody));
            socketRequest.setBody(body);
        }
        return socketRequest;
    }


    private SocketResponse handleRequest(SocketRequest socketRequest) {

        try {
            return urlMapper.handle(socketRequest);
        }
        catch (MalformedURLException e) {
            // 404 NOT FOUND
            return createResponse(Status.NOT_FOUND.getCode(), socketRequest.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            // 에러 메세지의 response 생성
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), socketRequest.getHeader(), e.getMessage());
        }
    }


    private void sendResponse(BufferedOutputStream socketWriter, SocketResponse socketResponse) throws IOException {

        byte[] decryptedStatusCode = ByteBuffer.allocate(HEADER_BYTE_SIZE).putInt(socketResponse.getStatusCode()).array();
        byte[] encryptedStatusCode = CipherWorker.encrypt(decryptedStatusCode);

        byte[] decryptedHeader = objectMapper.writeValueAsBytes(socketResponse.getHeader());
        byte[] encryptedHeader = CipherWorker.encrypt(decryptedHeader);

        byte[] decryptedBody = objectMapper.writeValueAsBytes(socketResponse.getBody());
        byte[] encryptedBody = CipherWorker.encrypt(decryptedBody);

        byte[] decryptedHeaderSize = ByteBuffer.allocate(HEADER_BYTE_SIZE).putInt(decryptedHeader.length).array();
        byte[] encryptedHeaderSize = CipherWorker.encrypt(decryptedHeaderSize);

        byte[] decryptedBodySize = ByteBuffer.allocate(BODY_BYTE_SIZE).putInt(decryptedBody.length).array();
        byte[] encryptedBodySize = CipherWorker.encrypt(decryptedBodySize);

        socketWriter.write(encryptedHeaderSize);
        socketWriter.write(encryptedBodySize);
        socketWriter.write(encryptedStatusCode);
        socketWriter.write(encryptedHeader);
        socketWriter.write(encryptedBody);
        socketWriter.flush();

        if(socketResponse.getHeader().get(CONTENT_TYPE.getValue()).equals(ContentType.JSON.getValue())) {
            socket.close();
        }
    }

}
