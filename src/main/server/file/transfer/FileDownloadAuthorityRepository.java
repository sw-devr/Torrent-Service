package main.server.file.transfer;

public interface FileDownloadAuthorityRepository {

    boolean isAuthority(String authorityToken);

    String createAuthority(String downloadFilePath);

    String getDownloadFilePath(String authorityToken);

    String removeAuthority(String authorityToken);

}
