package main.server.file.metadata;

public class RequestFileMetadataSearchFromUserDto {

    private long userId;
    private int offset;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
