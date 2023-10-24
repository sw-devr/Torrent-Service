package main.server.payment;

public class ResponsePurchaseFileDto {

    private String downloadFileAuthorityToken;
    private String downloadFilePath;

    public String getDownloadFileAuthorityToken() {
        return downloadFileAuthorityToken;
    }

    public void setDownloadFileAuthorityToken(String downloadFileAuthorityToken) {
        this.downloadFileAuthorityToken = downloadFileAuthorityToken;
    }

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }
}
