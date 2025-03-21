package models;

import java.util.HashMap;
import java.util.Map;

public class MultipleChoiceQuestion extends Question {
    private String correctAnswer;
    private Map<String, String> options;  // Storing options as key-value pairs (e.g., "A" -> "Option 1")

    // ✅ Fixed constructor
    public MultipleChoiceQuestion(int questionId, String questionText, String subject, String difficultyLevel, 
                                  String questionType, String correctAnswer, Map<String, String> options) {
        super(questionId, questionText, subject, difficultyLevel, questionType, correctAnswer);
        this.correctAnswer = correctAnswer;
        this.options = options != null ? options : new HashMap<>();  // Avoid NullPointerException
    }

    @Override
    public String getFormattedQuestion() {
        return questionText + " (Multiple Choice)";
    }
    // Method to set the options for the multiple-choice question
    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Map<String, String> getOptions() {
        return options;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}