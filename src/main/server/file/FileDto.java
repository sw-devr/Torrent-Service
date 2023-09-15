package main.server.file;

import java.util.Objects;

public class FileDto {

    private long id;
    private long userId;
    private int price;
    private String path;
    private String subject;
    private String description;
    private int size;
    private long createdTimestamp;
    private int downloadCnt;
    private FileState state;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public int getDownloadCnt() {
        return downloadCnt;
    }

    public void setDownloadCnt(int downloadCnt) {
        this.downloadCnt = downloadCnt;
    }

    public FileState getState() {
        return state;
    }

    public void setState(FileState fileState) {
        this.state = fileState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileDto)) return false;
        FileDto fileDto = (FileDto) o;
        return getId() == fileDto.getId() && getUserId() == fileDto.getUserId() && getPrice() == fileDto.getPrice() && getSize() == fileDto.getSize() && getCreatedTimestamp() == fileDto.getCreatedTimestamp() && getDownloadCnt() == fileDto.getDownloadCnt() && Objects.equals(getPath(), fileDto.getPath()) && Objects.equals(getSubject(), fileDto.getSubject()) && Objects.equals(getDescription(), fileDto.getDescription()) && getState() == fileDto.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getPrice(), getPath(), getSubject(), getDescription(), getSize(), getCreatedTimestamp(), getDownloadCnt(), getState());
    }

    @Override
    public String toString() {
        return "FileDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", price=" + price +
                ", path='" + path + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", size=" + size +
                ", createdTimestamp=" + createdTimestamp +
                ", downloadCnt=" + downloadCnt +
                ", state=" + state +
                '}';
    }
}
