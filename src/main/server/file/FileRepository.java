package main.server.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public interface FileRepository {

    void send(BufferedOutputStream socketWriter, String filePath);

    void receive(BufferedInputStream socketReader, String filePath);
}
