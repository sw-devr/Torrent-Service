package main.server.file.metadata;

public class RequestFileMetadataCreateDto {

    private long userId;
    private String subject;
    private String description;
    private int price;
    private String fileName;
    private int size;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "RequestCreateFileMetadataDto{" +
                "userId=" + userId +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                '}';
    }
}
