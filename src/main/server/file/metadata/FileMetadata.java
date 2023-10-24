package main.server.file.metadata;

import java.util.Objects;

public class FileMetadata {

    private long id;
    private long userId;
    private int price;
    private String path;
    private String subject;
    private String description;
    private int size;
    private long createdTimestamp;
    private long lastUpdatedTimestamp;
    private int downloadCnt;
    private FileState state;

    public static FileMetadata init(String subject, int price,
                                    long userId, String filePath, int fileSize) {

        return init(subject, "", price, userId, filePath, fileSize);
    }

    public static FileMetadata init(String subject, String description, int price,
                                    long userId, String filePath, int fileSize) {

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setSubject(subject);
        fileMetadata.setDescription(description);
        fileMetadata.setPrice(price);
        fileMetadata.setUserId(userId);
        fileMetadata.setPath(filePath);
        fileMetadata.setSize(fileSize);
        fileMetadata.setState(FileState.READY);
        fileMetadata.setCreatedTimestamp(System.currentTimeMillis());
        fileMetadata.setLastUpdatedTimestamp(System.currentTimeMillis());
        fileMetadata.setDownloadCnt(0);

        return fileMetadata;
    }


    public void update(String subject, String description, Integer price) {

        if(subject != null) {
            this.subject = subject;
        }
        if(description != null) {
            this.description = description;
        }
        if(price != null) {
            this.price = price;
        }
        lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public int increaseDownloadCnt() {
        this.downloadCnt++;

        return this.downloadCnt;
    }

    public FileState completeFileUpload() throws IllegalAccessException {
        if(this.state != FileState.READY) {
            throw new IllegalAccessException("이미 업로드 완료된 파일입니다.");
        }
        this.state = FileState.AVAILABLE;

        return this.state;
    }

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

    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
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
        if (!(o instanceof FileMetadata)) return false;
        FileMetadata that = (FileMetadata) o;
        return id == that.id && userId == that.userId && price == that.price && size == that.size && createdTimestamp == that.createdTimestamp && lastUpdatedTimestamp == that.lastUpdatedTimestamp && downloadCnt == that.downloadCnt && Objects.equals(path, that.path) && Objects.equals(subject, that.subject) && Objects.equals(description, that.description) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, price, path, subject, description, size, createdTimestamp, lastUpdatedTimestamp, downloadCnt, state);
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
                ", lastUpdatedTimestamp=" + lastUpdatedTimestamp +
                ", downloadCnt=" + downloadCnt +
                ", state=" + state +
                '}';
    }

}
