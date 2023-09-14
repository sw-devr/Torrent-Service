package test.server.file;

import main.server.file.StreamFileRepository;
import main.server.security.CipherWorker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static main.server.file.FileConstants.*;

public class StreamFileRepositoryTest {

    private static final StreamFileRepository streamFileRepository = new StreamFileRepository();

    public static void main(String[] args) throws IOException {

        StreamFileRepositoryTest test = new StreamFileRepositoryTest();

        test.testSendWithValidParam();
        test.testSendWithNotExistingFileName();
        test.testSendWithDirectoryPath();
        test.testReceiveWithValidParam();
        test.testReceiveWithAlreadyExistingFileName();
        test.testReceiveWithInvalidPath();
    }


    // 전송 테스트1 : 클라이언트가 요청한 파일 경로가 존재할 경우 정상적으로 파일 데이터를 클라이언트로 보낸다.
    private void testSendWithValidParam() throws IOException {

        //given
        Path validFilePath = Paths.get("C:\\Users\\Public\\Downloads\\GenericList3.java");
        Path socketPath = Paths.get("C:\\Users\\Public\\Downloads\\copy_ver.java");

        BufferedOutputStream socketWriter = new BufferedOutputStream(new FileOutputStream(socketPath.toFile()));

        //when
        streamFileRepository.send(socketWriter, validFilePath.toString());
        socketWriter.close();

        //then
        BufferedInputStream socketReader = new BufferedInputStream(new FileInputStream(socketPath.toFile()));
        int totalSize = 0;
        byte[] decryptedBuffer = new byte[BUFFER_SIZE];
        byte[] encryptedBuffer = CipherWorker.encrypt(decryptedBuffer);

        int size;
        while((size = socketReader.read(encryptedBuffer)) != -1) {

            byte[] data = CipherWorker.decrypt(Arrays.copyOf(encryptedBuffer, size));
            totalSize += data.length;
        }
        if(!Files.exists(socketPath)) {
            throw new IllegalStateException("존재해야할 파일이 존재하지 않습니다.");
        }
        if(totalSize != Files.size(validFilePath)) {
            throw new IllegalStateException("저장소에 저장된 원본 파일을 제대로 읽지 못했습니다.");
        }

        //after
        socketReader.close();
        Files.delete(socketPath);

        System.out.println("전송 테스트 1 성공!");
    }


    // 전송 테스트2 : 클라이언트가 요청한 파일 경로가 존재하지 않을 경우 예외가 발생한다.
    private void testSendWithNotExistingFileName() throws IOException {

        //given
        Path invalidFilePath = Paths.get("C:\\Users\\Public\\Downloads\\not_exiting_file.java");
        Path socketPath = Paths.get("C:\\Users\\Public\\Downloads\\copy_ver.java");

        //when & then
        try (BufferedOutputStream socketWriter = new BufferedOutputStream(new FileOutputStream(socketPath.toFile()))) {
            streamFileRepository.send(socketWriter, invalidFilePath.toString());
        } catch (IllegalArgumentException e) {
            if(!e.getMessage().equals(FILE_NOT_EXISTING_MESSAGE)) {
                throw new IllegalStateException("디렉토리 경로에 대해 예외 처리를 하지 못했습니다.");
            }
            System.out.println("전송 테스트 2 성공!");
            return;
        } finally {
            Files.delete(socketPath);
        }
        throw new IllegalStateException("존재하지 않는 파일에 대해 예외 처리를 하지 못했습니다");
    }

    // 전송 테스트3 : 클라이언트가 요청한 파일 경로가 디렉토리일 경우 예외가 발생한다.
    private void testSendWithDirectoryPath() throws IOException {

        //given
        Path invalidFilePath = Paths.get("C:\\Users\\Public\\Downloads\\directory");
        Path socketPath = Paths.get("C:\\Users\\Public\\Downloads\\copy_ver.java");

        //when & then
        try (BufferedOutputStream socketWriter = new BufferedOutputStream(new FileOutputStream(socketPath.toFile()))) {
            streamFileRepository.send(socketWriter, invalidFilePath.toString());
        } catch (IllegalArgumentException e) {
            if(!e.getMessage().equals(NOT_FILE_MESSAGE)) {
                throw new IllegalStateException("디렉토리 경로에 대해 예외 처리를 하지 못했습니다.");
            }
            System.out.println("전송 테스트 3 성공!");
            return;
        } finally {
            Files.delete(socketPath);
        }
        throw new IllegalStateException("존재하지 않는 파일에 대해 예외 처리를 하지 못했습니다");
    }

    // 저장 테스트 1 : 기존의 파일명과 충돌되지 않는 파라미터로 저장 요청시 올바르게 저장 공간에 저장된다.
    private void testReceiveWithValidParam() throws IOException {

        //given
        Path validFilePath = Paths.get("C:\\Users\\Public\\Downloads\\not_existing_file_name.java");
        Path socketPath = Paths.get("C:\\Users\\Public\\Downloads\\GenericList3.java");
        Path encryptedSocketPath = Paths.get("C:\\Users\\Public\\Downloads\\encrypted_GenericList3.java");

        BufferedOutputStream socketWriter = new BufferedOutputStream(new FileOutputStream(encryptedSocketPath.toFile()));
        streamFileRepository.send(socketWriter, socketPath.toString());
        socketWriter.close();
        BufferedInputStream encryptedSocketReader = new BufferedInputStream(new FileInputStream(encryptedSocketPath.toFile()));

        //when
        streamFileRepository.receive(encryptedSocketReader, validFilePath.toString());
        encryptedSocketReader.close();

        //then
        if(!Files.exists(validFilePath)) {
            throw new IllegalStateException("저장되어야할 파일이 존재하지 않습니다.");
        }
        if(Files.size(validFilePath) != Files.size(socketPath)) {
            throw new IllegalStateException("저장소에 저장된 파일이 원본 파일을 제대로 복사하지 못했습니다.");
        }

        //after
        Files.delete(validFilePath);

        System.out.println("저장 테스트 1 성공!");
    }

    // 저장 테스트 2 : 기존의 파일명과 일치하는 파라미터로 저장 요청시 예외가 발생한다.
    private void testReceiveWithAlreadyExistingFileName() throws IOException {

        //given
        Path alreadyExistingFilePath = Paths.get("C:\\Users\\Public\\Downloads\\existing_file_name.java");
        Path socketPath = Paths.get("C:\\Users\\Public\\Downloads\\GenericList3.java");
        Path encryptedSocketPath = Paths.get("C:\\Users\\Public\\Downloads\\encrypted_GenericList3.java");

        BufferedOutputStream socketWriter = new BufferedOutputStream(new FileOutputStream(encryptedSocketPath.toFile()));
        streamFileRepository.send(socketWriter, socketPath.toString());

        //when & then
        try (BufferedInputStream encryptedSocketReader = new BufferedInputStream(new FileInputStream(encryptedSocketPath.toFile()))) {
            streamFileRepository.receive(encryptedSocketReader, alreadyExistingFilePath.toString());
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(ALREADY_EXISTING_FILE_NAME)) {
                System.out.println("저장 테스트 2 성공");
                return;
            }
            throw new IllegalStateException("기존 파일이 존재하기 때문에 저장되어서는 안되는 파일이 덮어쓰기 됨");
        } finally {
            socketWriter.close();
            Files.delete(encryptedSocketPath);
        }
        throw new IllegalStateException("기존 파일이 존재하기 때문에 저장되어서는 안되는 파일이 덮어쓰기 됨");
    }

    // 저장 테스트 3 : 올바르지 못한 파일명으로 저장 요청시 예외가 발생한다.
    private void testReceiveWithInvalidPath() throws IOException {

        //given
        Path notFilePath = Paths.get("C:\\Users\\Public\\Downloads\\not_file");
        Path socketPath = Paths.get("C:\\Users\\Public\\Downloads\\GenericList3.java");
        Path encryptedSocketPath = Paths.get("C:\\Users\\Public\\Downloads\\encrypted_GenericList3.java");

        BufferedOutputStream socketWriter = new BufferedOutputStream(new FileOutputStream(encryptedSocketPath.toFile()));
        streamFileRepository.send(socketWriter, socketPath.toString());

        //when & then
        try (BufferedInputStream encryptedSocketReader = new BufferedInputStream(new FileInputStream(encryptedSocketPath.toFile()))) {
            streamFileRepository.receive(encryptedSocketReader, notFilePath.toString());
        } catch (IllegalArgumentException e) {
            if(e.getMessage().equals(NOT_FILE_MESSAGE)) {
                System.out.println("저장 테스트 3 성공");
                return;
            }
            throw new IllegalStateException("파일이 아닌 경로로 요청이 들어올 때 예외를 발생시키지 못했음");
        } finally {
            socketWriter.close();
            Files.delete(encryptedSocketPath);
        }
        throw new IllegalStateException("파일이 아닌 경로로 데이터가 저장되는 상황이 발생함");
    }
}
