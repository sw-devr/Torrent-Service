package main.server.file.metadata;

public class RequestFileMetadataSearchFromSubjectDto {

    private String subject;
    private int offset;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
