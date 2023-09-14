package main.server.file;

import main.server.security.CipherWorker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static main.server.file.FileConstants.*;

public class StreamFileRepository implements FileRepository {

    @Override
    public void send(BufferedOutputStream socketWriter, String fileName) {

        Path filePath = Paths.get(fileName);
        if(!Files.exists(filePath)) {
            throw new IllegalArgumentException(FILE_NOT_EXISTING_MESSAGE);
        }
        if(Files.isDirectory(filePath)) {
            throw new IllegalArgumentException(NOT_FILE_MESSAGE);
        }
        byte[] buffer = new byte[BUFFER_SIZE];
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
            System.out.println("파일 전송 실패");
        }
    }

    @Override
    public void receive(BufferedInputStream socketReader, String fileName) {

        Path filePath = Paths.get(fileName);
        if(Files.exists(filePath)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_FILE_NAME);
        }
        if(!isCorrectFileFormat(extractFileName(filePath.toString()))) {
            throw new IllegalArgumentException(NOT_FILE_MESSAGE);
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] encryptedBuffer = CipherWorker.encrypt(buffer);

        System.out.println("파일 저장 시작");
        try(BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(fileName))) {
            int size;
            while((size = socketReader.read(encryptedBuffer)) != STREAM_FINAL_VALUE) {
                byte[] encryptedData = Arrays.copyOf(encryptedBuffer, size);
                byte[] decryptedData = CipherWorker.decrypt(encryptedData);

                assert decryptedData != null;

                fileOutputStream.write(decryptedData);
                fileOutputStream.flush();
            }
            System.out.println("파일 저장 성공");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("파일 저장 실패");
        }
    }

    private String extractFileName(String filePath) {

        String[] filePaths = filePath.split(PATH_REGEX);
        return filePaths[filePaths.length-1];
    }


    private boolean isCorrectFileFormat(String realFileName) {

        return realFileName.split(FILE_FORMAT_SEPARATOR).length == 2;
    }
}
