package services;

import backend.UserDashboardDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Question;
import models.QuizSubject;
import models.Result;
import models.StudyMaterial;
import models.MultipleChoiceQuestion;
import models.TrueFalseQuestion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserDashboardService {
    
    private UserDashboardDAO userDashboardDb;
    private QuizSubject quizSubject;
    
    
    

    public UserDashboardService() {
        this.userDashboardDb = new UserDashboardDAO();
        this.quizSubject = new QuizSubject();
    }
    
    public void resetQuizSession() {
        userDashboardDb.resetDisplayedQuestions();
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
    
    public List<Question> fetchRandomMixedQuestions() {
        return userDashboardDb.getRandomMixQuestions(15);
    }

    public List<StudyMaterial> getStudyMaterials() {
        try {
            return userDashboardDb.getAllStudyMaterials();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load study materials");
            return Collections.emptyList();
        }
    }

    public File downloadMaterial(StudyMaterial material) throws IOException {
        if (material.getFileData() == null || material.getFileData().length == 0) {
            throw new IOException("No file data available");
        }

        Path tempFile = Files.createTempFile(material.getTitle() + "-", "." + material.getFileExtension());
        Files.write(tempFile, material.getFileData());
        return tempFile.toFile();
    }

    public StudyMaterial loadMaterialWithData(int materialId) {
        try {
            return userDashboardDb.getMaterialWithData(materialId);
        } catch (SQLException e) {
            showErrorAlert("Download Error", "Failed to load file content");
            return null;
        }
    }

    private void showErrorAlert(String title, String message) {
        // Implement alert showing logic
    }
    public void saveQuizResult(Result result) {
        try {
            userDashboardDb.saveQuizResult(result);
        } catch (SQLException e) {
            System.err.println("Error saving quiz result: " + e.getMessage());
            // Add error handling/alert here
        }
    }
    public List<Result> getUserResults(int userId) {
    	return userDashboardDb.getUserResult(userId);
    }
    

 
    
}



