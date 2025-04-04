package models;

import java.sql.Timestamp;

public class Result {
    private int id;
    private int userId;
    private String type;
    private String subject;
    private String difficultyLevel;
    private Integer timeTaken;
    private int score;
    private Timestamp completedAt;

    // Constructor for default quiz
    public Result(int userId, String subject, String difficultyLevel, int score) {
        this.userId = userId;
        this.type = "default";
        this.subject = subject;
        this.difficultyLevel = difficultyLevel;
        this.score = score;
    }

    // Constructor for mix quiz
    public Result(int userId, int timeTaken, int score) {
        this.userId = userId;
        this.type = "mix";
        this.timeTaken = timeTaken;
        this.score = score;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getSubject() { return subject; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public Integer getTimeTaken() { return timeTaken; }
    public int getScore() { return score; }  // This was missing
    public Timestamp getCompletedAt() { return completedAt; }

    // Setters (if needed)
    public void setId(int id) { this.id = id; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }
}