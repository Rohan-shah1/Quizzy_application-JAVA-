package models;
import java.sql.Timestamp;

public class StudyMaterial {
    private int id;
    private String title;
    private String description;
    private String fileType;
    private Timestamp uploadedAt;

    public StudyMaterial(int id, String title, String description, String fileType, Timestamp uploadedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileType = fileType;
        this.uploadedAt = uploadedAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFileType() {
        return fileType;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }
}
