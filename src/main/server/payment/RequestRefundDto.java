package main.server.payment;

public class RequestRefundDto {

    private String downloadFilePath;

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }
}
