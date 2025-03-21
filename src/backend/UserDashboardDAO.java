package backend;

import models.Question;
import models.MultipleChoiceQuestion;
import models.TrueFalseQuestion;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserDashboardDAO {
	
    
    private Connection conn;
    
    private static Set<Integer> displayedQuestionIds = new HashSet<>();

    public UserDashboardDAO() {
        // Get the Singleton Database Connection
        this.conn = DbConn.getInstance().getConnection();
    }

    public Question getRandomQuestion(String difficulty, String subject) {
        Question question = null;

        // Start building the query
        StringBuilder query = new StringBuilder("SELECT * FROM questions WHERE difficulty_level = ? AND subject = ?");

        // If there are already displayed questions, add a NOT IN condition
        if (!displayedQuestionIds.isEmpty()) {
            query.append(" AND question_id NOT IN (")
                 .append(String.join(",", displayedQuestionIds.stream().map(String::valueOf).toArray(String[]::new)))
                 .append(")");
        }

        query.append(" ORDER BY RAND() LIMIT 1");

        try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            pstmt.setString(1, difficulty);
            pstmt.setString(2, subject);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("question_text");
                String questionType = rs.getString("question_type");
                String correctAnswer = rs.getString("correct_answer");

                // Create appropriate Question object based on type
                if (questionType.equalsIgnoreCase("MCQ")) {
                    // Fetch options from questionoptions table
                    Map<String, String> options = getQuestionOptions(questionId);
                    question = new MultipleChoiceQuestion(questionId, questionText, subject, difficulty, questionType, correctAnswer, options);
                } else if (questionType.equalsIgnoreCase("True/False")) {
                    question = new TrueFalseQuestion(questionId, questionText, subject, difficulty, questionType, correctAnswer);
                }

                // Mark the question as displayed (so it won't be picked again)
                displayedQuestionIds.add(questionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return question; // Returns null if no question is found
    }
    private Map<String, String> getQuestionOptions(int questionId) {
        Map<String, String> options = new HashMap<>();
        String query = "SELECT option_A, option_B, option_C, option_D FROM questionoptions WHERE question_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                options.put("A", rs.getString("option_A"));
                options.put("B", rs.getString("option_B"));
                options.put("C", rs.getString("option_C"));
                options.put("D", rs.getString("option_D"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }
}
