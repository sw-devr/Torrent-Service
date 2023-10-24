package main.server.file.transfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public interface FileTransferRepository {

    void send(BufferedOutputStream socketWriter, String filePath);

    void receive(BufferedInputStream socketReader, String filePath, int fileSize);
}
