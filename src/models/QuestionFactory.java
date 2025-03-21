package models;

import java.util.Map;

public class QuestionFactory {
    public static Question createQuestion(int questionId, String questionText, String subject, String difficultyLevel, 
                                          String questionType, String correctAnswer, Map<String, String> options) {
        if ("MCQ".equalsIgnoreCase(questionType)) {
            return new MultipleChoiceQuestion(questionId, questionText, subject, difficultyLevel, questionType, correctAnswer, options);
        } else if ("True/False".equalsIgnoreCase(questionType)) {
            return new TrueFalseQuestion(questionId, questionText, subject, difficultyLevel, questionType, correctAnswer);
        } else {
            throw new IllegalArgumentException("Invalid question type: " + questionType);
        }
    }
}

