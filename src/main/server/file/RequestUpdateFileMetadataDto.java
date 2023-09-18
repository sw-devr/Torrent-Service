package main.server.file;

public class RequestUpdateFileMetadataDto {

    private long userId;
    private long requiredFileId;
    private Integer price;
    private String subject;
    private String description;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRequiredFileId() {
        return requiredFileId;
    }

    public void setRequiredFileId(long requiredFileId) {
        this.requiredFileId = requiredFileId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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
}
