package models;

public abstract class Question {
    protected int questionId;
    protected String questionText;
    protected String subject;
    protected String difficultyLevel;
    protected String questionType;
    protected String correctAnswer;

    // Constructor
    public Question(int questionId, String questionText, String subject, String difficultyLevel, String questionType, String correctAnswer) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.subject = subject;
        this.difficultyLevel = difficultyLevel;
        this.questionType = questionType;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public int getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public String getSubject() { return subject; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public String getQuestionType() { return questionType; }
    public String getCorrectAnswer() { return correctAnswer; }

    // Setters
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    // Abstract method for UI integration
    public abstract String getFormattedQuestion(); // Used in JavaFX UI

    // Abstract method for getting options (specific to question types like MCQ)
    public abstract Object getOptions(); // Can be a Map<String, String> for MCQs, List<String> for other types, etc.
}
