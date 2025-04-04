package models;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class StudyMaterial {
    private final int materialId;
    private final String title;
    private final String description;
    private final String fileType;
    private final Timestamp uploadedAt;
    private byte[] fileData;

    // Constructor for list view (without file data)
    public StudyMaterial(int materialId, String title, String description, 
                        String fileType, Timestamp uploadedAt) {
        this.materialId = materialId;
        this.title = title;
        this.description = description;
        this.fileType = fileType;
        this.uploadedAt = uploadedAt;
    }

    // Constructor with file data (for downloads)
    public StudyMaterial(int materialId, String title, String description, 
                        String fileType, Timestamp uploadedAt, byte[] fileData) {
        this(materialId, title, description, fileType, uploadedAt);
        this.fileData = fileData;
    }

    // Getters
    public int getMaterialId() { return materialId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFileType() { return fileType; }
    public byte[] getFileData() { return fileData; }
    public Timestamp getUploadedAt() {return uploadedAt;}
    
    public String getFormattedDate() {
        return uploadedAt.toLocalDateTime()
               .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
    }
    
    public String getFileExtension() {
        return fileType.contains("/") ? 
               fileType.split("/")[1] : 
               fileType;
    }
}