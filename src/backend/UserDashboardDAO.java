package backend;

import models.Question;
import models.StudyMaterial;
import models.MultipleChoiceQuestion;
import models.TrueFalseQuestion;
import models.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDashboardDAO {
	
    
    private Connection conn;
    
    private Set<Integer> displayedQuestionIds = new HashSet<>();
 

    public UserDashboardDAO() {
        // Get the Singleton Database Connection
        this.conn = DbConn.getInstance().getConnection();
    }
    // Add this method to reset displayed questions
    public void resetDisplayedQuestions() {
        displayedQuestionIds.clear();
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
//method to featch question for mix quiz.    
    
    public List<Question> getRandomMixQuestions(int count) {
        List<Question> questions = new ArrayList<>();
        String baseQuery = "SELECT * FROM questions";
        
        try {
            // Build query with exclusion of shown questions
            StringBuilder queryBuilder = new StringBuilder(baseQuery);
            if (!displayedQuestionIds.isEmpty()) {
                queryBuilder.append(" WHERE question_id NOT IN (")
                           .append(String.join(",", displayedQuestionIds.stream()
                                   .map(String::valueOf)
                                   .toArray(String[]::new)))
                           .append(")");
            }
            queryBuilder.append(" ORDER BY RAND() LIMIT ?");
            
            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());
            pstmt.setInt(1, count);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("question_text");
                String subject = rs.getString("subject");
                String difficulty = rs.getString("difficulty_level");
                String questionType = rs.getString("question_type");
                String correctAnswer = rs.getString("correct_answer");

                Question question;
                if ("MCQ".equalsIgnoreCase(questionType)) {
                    Map<String, String> options = getQuestionOptions(questionId);
                    question = new MultipleChoiceQuestion(
                        questionId, questionText, subject, difficulty, 
                        questionType, correctAnswer, options
                    );
                } else {
                    question = new TrueFalseQuestion(
                        questionId, questionText, subject, difficulty, 
                        questionType, correctAnswer
                    );
                }
                
                questions.add(question);
                displayedQuestionIds.add(questionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
    // Get all study materials (without file data)
    public List<StudyMaterial> getAllStudyMaterials() throws SQLException {
        List<StudyMaterial> materials = new ArrayList<>();
        String query = "SELECT material_id, title, description, file_type, uploaded_at FROM studymaterials";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                materials.add(new StudyMaterial(
                    rs.getInt("material_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("file_type"),
                    rs.getTimestamp("uploaded_at")
                ));
            }
        }
        return materials;
    }

    // Get file data by material ID
    public StudyMaterial getMaterialWithData(int materialId) throws SQLException {
        String query = "SELECT * FROM studymaterials WHERE material_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, materialId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new StudyMaterial(
                        rs.getInt("material_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("file_type"),
                        rs.getTimestamp("uploaded_at"),
                        rs.getBytes("file_data")
                    );
                }
            }
        }
        return null;
    }
    public void saveQuizResult(Result result) throws SQLException {
        String query = "INSERT INTO results (user_id, type, subject, difficulty_level, timetaken, score) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, result.getUserId());
            pstmt.setString(2, result.getType());
            
            if ("default".equals(result.getType())) {
                pstmt.setString(3, result.getSubject());
                pstmt.setString(4, result.getDifficultyLevel());
                pstmt.setNull(5, Types.INTEGER);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setInt(5, result.getTimeTaken());
            }
            
            pstmt.setInt(6, result.getScore());
            pstmt.executeUpdate();
        }
    }
    public List<Result> getUserResult(int userId) {
        List<Result> results = new ArrayList<>();
        String query = "SELECT id, user_id, type, subject, difficulty_level, timetaken, score, completed_at " +
                "FROM results WHERE user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                Result result;

                if ("default".equals(type)) {
                    // Use constructor for default quizzes
                    result = new Result(
                        rs.getInt("user_id"),
                        rs.getString("subject"),
                        rs.getString("difficulty_level"),
                        rs.getInt("score")
                    );
                } else {
                    // Use constructor for mix quizzes
                    result = new Result(
                        rs.getInt("user_id"),
                        rs.getInt("timetaken"),
                        rs.getInt("score")
                    );
                }

                // Set common fields
                result.setId(rs.getInt("id"));
                result.setCompletedAt(rs.getTimestamp("completed_at"));
                results.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
}
