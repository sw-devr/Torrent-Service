package main.server.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.ContentType;
import main.protocol.SocketHeaderType;
import main.protocol.Status;
import main.server.security.CipherWorker;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.SocketRequest.BODY_BYTE_SIZE;
import static main.protocol.SocketRequest.HEADER_BYTE_SIZE;
import static main.protocol.SocketResponse.STATUS_CODE_BYTE_SIZE;
import static main.server.common.CommonConstants.DEFAULT_BUFFER_SIZE;
import static main.server.file.FileConstants.*;

public class StreamFileRepository implements FileRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void send(BufferedOutputStream socketWriter, String fileName) {

        Path filePath = Paths.get(fileName);

        if(!Files.exists(filePath)) {
            throw new IllegalArgumentException(FILE_NOT_EXISTING_MESSAGE);
        }
        if(Files.isDirectory(filePath)) {
            throw new IllegalArgumentException(NOT_FILE_MESSAGE);
        }

        //response header 전송
        try {
            int bodySize = (int)filePath.toFile().length();
            Map<String, String> header = new HashMap<>();
            header.put(SocketHeaderType.CONTENT_TYPE.getValue(), ContentType.STREAM.getValue());

            byte[] decryptedStatusCode = ByteBuffer.allocate(STATUS_CODE_BYTE_SIZE).putInt(Status.SUCCESS.getCode()).array();
            byte[] encryptedStatusCode = CipherWorker.encrypt(decryptedStatusCode);

            byte[] decryptedHeader = objectMapper.writeValueAsBytes(header);
            byte[] encryptedHeader = CipherWorker.encrypt(decryptedHeader);

            byte[] decryptedHeaderSize = ByteBuffer.allocate(HEADER_BYTE_SIZE).putInt(decryptedHeader.length).array();
            byte[] encryptedHeaderSize = CipherWorker.encrypt(decryptedHeaderSize);

            byte[] decryptedBodySize = ByteBuffer.allocate(BODY_BYTE_SIZE).putInt(bodySize).array();
            byte[] encryptedBodySize = CipherWorker.encrypt(decryptedBodySize);

            socketWriter.write(encryptedHeaderSize);
            socketWriter.write(encryptedBodySize);
            socketWriter.write(encryptedStatusCode);
            socketWriter.write(encryptedHeader);
            socketWriter.flush();
        } catch (IOException e) {

        }

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int size;

        System.out.println("파일 전송 시작");
        try(BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(fileName))) {

            while((size = fileInputStream.read(buffer)) != STREAM_FINAL_VALUE) {
                byte[] decryptedData = Arrays.copyOf(buffer, size);
                byte[] encryptedData = CipherWorker.encrypt(decryptedData);

                assert encryptedData != null;

                socketWriter.write(encryptedData);
                socketWriter.flush();
            }
            System.out.println("파일 전송 성공");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("파일 전송 실패");
        }
    }

    @Override
    public void receive(BufferedInputStream socketReader, String fileName, int fileSize) {

        Path filePath = Paths.get(fileName);
        if(Files.exists(filePath)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_FILE_NAME);
        }
        if(!isCorrectFileFormat(extractFileName(filePath.toString()))) {
            throw new IllegalArgumentException(NOT_FILE_MESSAGE);
        }

        System.out.println("파일 저장 시작");

        int sumSize = 0;
        try(BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(fileName))) {
            while(true) {
                int bufferSize = Math.min(fileSize-sumSize, DEFAULT_BUFFER_SIZE);
                byte[] buffer = new byte[bufferSize];
                byte[] encryptedBuffer = CipherWorker.encrypt(buffer);

                int size = socketReader.read(encryptedBuffer);
                if(size == -1) {
                    break;
                }
                byte[] encryptedData = Arrays.copyOf(encryptedBuffer, size);
                byte[] decryptedData = CipherWorker.decrypt(encryptedData);
                assert decryptedData != null;

                fileOutputStream.write(decryptedData);
                fileOutputStream.flush();
                sumSize += decryptedData.length;
                if(sumSize >= fileSize) {
                    break;
                }
            }
            System.out.println("파일 저장 성공");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("파일 저장 실패");
        }
    }

    private String extractFileName(String filePath) {

        String[] filePaths = filePath.split(PATH_REGEX);
        return filePaths[filePaths.length-1];
    }


    private boolean isCorrectFileFormat(String realFileName) {

        return realFileName.split(FILE_FORMAT_SEPARATOR).length >= 2;
    }
}
