package main.server.file;

import java.nio.file.Paths;

public class FileConstants {

    public static final String DEFAULT_FILE_STORE_PATH = Paths.get("C:\\Users\\Public\\Downloads").toString();

    public static final String FILE_NOT_EXISTING_MESSAGE = "존재하지 않는 파일입니다.";
    public static final String NOT_FILE_MESSAGE = "파일이 아닙니다.";
    public static final String ALREADY_EXISTING_FILE_NAME = "이미 존재하는 파일명입니다.";


    public static final String FILE_FORMAT_SEPARATOR = "[.]";
    public static final String PATH_REGEX = "/|\\\\";
    public static final int BUFFER_SIZE = 1024;
    public static final int STREAM_FINAL_VALUE = -1;

    public static final int DEFAULT_PAGING_SIZE = 10;
    public static final int EMPTY_PRICE_VALUE = -1;
}
