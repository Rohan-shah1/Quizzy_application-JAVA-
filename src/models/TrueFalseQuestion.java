package models;

public class TrueFalseQuestion extends Question {
    private String correctAnswer;

    // Constructor that matches the parameters
    public TrueFalseQuestion(int questionId, String questionText, String subject, String difficultyLevel, String questionType, String correctAnswer) {
        super(questionId, questionText, subject, difficultyLevel, questionType, correctAnswer);
        this.correctAnswer = correctAnswer;
    }

    // Override the abstract method from Question class
    @Override
    public String getFormattedQuestion() {
        return questionText + " (True/False)";
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public Object getOptions() {
        // Return null or an empty structure, as no options are needed for true/false questions
        return null;
    }
}

