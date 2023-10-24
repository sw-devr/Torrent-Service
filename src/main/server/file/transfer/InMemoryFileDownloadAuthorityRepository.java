package main.server.file.transfer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFileDownloadAuthorityRepository implements FileDownloadAuthorityRepository {

    private final Map<String /* token */, String /* file download path */> downloadAuthority = new ConcurrentHashMap<>();

    @Override
    public boolean isAuthority(String authorityToken) {

        return downloadAuthority.containsKey(authorityToken);
    }

    @Override
    public String createAuthority(String downloadFilePath) {

        return null;
    }

    @Override
    public String getDownloadFilePath(String authorityToken) {

        return downloadAuthority.get(authorityToken);
    }

    @Override
    public String removeAuthority(String authorityToken) {

        return downloadAuthority.remove(authorityToken);
    }
}
