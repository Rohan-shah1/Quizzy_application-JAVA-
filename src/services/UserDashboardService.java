package services;

import backend.UserDashboardDAO;
import models.Question;
import models.QuizSubject;
import models.MultipleChoiceQuestion;
import models.TrueFalseQuestion;

import java.util.Map;

public class UserDashboardService {
    
    private UserDashboardDAO userDashboardDb;
    private QuizSubject quizSubject; // Observable for score updates

    public UserDashboardService() {
        this.userDashboardDb = new UserDashboardDAO();
        this.quizSubject = new QuizSubject();
    }
    
    public void fetchAndDisplayQuestion(String difficulty, String subject) {
        Question question = userDashboardDb.getRandomQuestion(difficulty, subject);

        if (question == null) {
            System.out.println("No question found for the selected difficulty and subject.");
            return;
        }
      //here the getFormatedquestion method is used showing the use of Polymorphism
        System.out.println("Question: " + question.getFormattedQuestion());

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            for (Map.Entry<String, String> entry : mcq.getOptions().entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } else if (question instanceof TrueFalseQuestion) {
            System.out.println("Options: True / False");
        }
    }

    // Fetch a random question from DB
    public Question fetchRandomQuestion(String difficulty, String subject) {
        return userDashboardDb.getRandomQuestion(difficulty, subject);
    }

    // Check if answer is correct and notify observers
    public void checkAnswer(String userAnswer, String correctAnswer) {
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            quizSubject.setScore(quizSubject.getScore() + 1); // Update score
            quizSubject.notifyObservers(); // Notify observers of score update
        }
    }

    public QuizSubject getQuizSubject() {
        return quizSubject;
    }
    
}



