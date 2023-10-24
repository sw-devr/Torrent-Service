package main.server.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.server.file.*;
import main.server.file.metadata.CSVFileMetadataRepository;
import main.server.file.metadata.FileMetadataRepository;
import main.server.file.metadata.FileMetadataService;
import main.server.file.transfer.*;
import main.server.payment.PaymentService;
import main.server.user.CSVUserRepository;
import main.server.user.UserRepository;
import main.server.user.UserService;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonConstants {


    private static final String USER_ID_INCREMENT = "user_id_increment.txt";
    private static final String FILE_METADATA_ID_INCREMENT = "file_metadata_id_increment.txt";
    private static final String FILE_METADATA_DATABASE_FILE_NAME = "file_metadata_database.txt";
    private static final String USER_DATABASE_FILE_NAME = "user_database.txt";
    public static final String DEFAULT_FILE_METADATA_DATABASE_PATH = Paths.get(FileConstants.DEFAULT_FILE_STORE_PATH, FILE_METADATA_DATABASE_FILE_NAME).toString();
    public static final String DEFAULT_USER_DATABASE_PATH = Paths.get(FileConstants.DEFAULT_FILE_STORE_PATH, USER_DATABASE_FILE_NAME).toString();
    public static final String DEFAULT_FILE_METADATA_ID_PATH = Paths.get(FileConstants.DEFAULT_FILE_STORE_PATH, FILE_METADATA_ID_INCREMENT).toString();
    public static final String DEFAULT_USER_ID_PATH = Paths.get(FileConstants.DEFAULT_FILE_STORE_PATH, USER_ID_INCREMENT).toString();


    public static final int SERVER_PORT = 8730;
    public static final int DEFAULT_BUFFER_SIZE = 1024*1024;
    public static final String PATH_REGEX = "/|\\\\";
    public static final String TEMP_PREFIX = "temp_";
    public static final String CSV_COLUMN_SEPARATOR = ",";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final UserRepository USER_REPOSITORY = new CSVUserRepository(DEFAULT_USER_DATABASE_PATH, DEFAULT_USER_ID_PATH);
    public static final FileMetadataRepository FILE_METADATA_REPOSITORY = new CSVFileMetadataRepository(DEFAULT_FILE_METADATA_DATABASE_PATH, DEFAULT_FILE_METADATA_ID_PATH);
    public static final FileTransferRepository FILE_TRANSFER_REPOSITORY = new StreamFileTransferRepository();
    public static final FileDownloadAuthorityRepository FILE_DOWNLOAD_AUTHORITY_REPOSITORY = new InMemoryFileDownloadAuthorityRepository();

    public static final FileTransferService FILE_TRANSFER_SERVICE = new FileTransferService(FILE_METADATA_REPOSITORY, FILE_TRANSFER_REPOSITORY, FILE_DOWNLOAD_AUTHORITY_REPOSITORY);
    public static final FileMetadataService FILE_METADATA_SERVICE = new FileMetadataService(FILE_METADATA_REPOSITORY);
    public static final UserService USER_SERVICE = new UserService(USER_REPOSITORY, FILE_METADATA_SERVICE);
    public static final PaymentService PAYMENT_SERVICE = new PaymentService(FILE_METADATA_SERVICE, USER_REPOSITORY, FILE_METADATA_REPOSITORY);


    public static String createTempFilePath(String path) {

        Path p = Paths.get(path);
        String[] paths = p.toString().split(PATH_REGEX);
        StringBuilder answer = new StringBuilder();
        for(int i=0;i<paths.length-1;i++) {
            answer.append(paths[i]);
            answer.append("/");
        }
        answer.append(TEMP_PREFIX).append(paths[paths.length - 1]);

        return Paths.get(answer.toString()).toString();
    }
}
