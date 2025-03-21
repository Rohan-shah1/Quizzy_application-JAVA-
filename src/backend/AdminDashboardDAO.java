package backend;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.MultipleChoiceQuestion;
import models.PendingJoinRequest;
import models.Question;
import models.QuestionFactory;
import models.StudyMaterial;

public class AdminDashboardDAO {
	// Method to fetch pending join requests from the database
    public ObservableList<PendingJoinRequest> fetchPendingRequests() {
        ObservableList<PendingJoinRequest> pendingRequests = FXCollections.observableArrayList();
        String sql = "SELECT full_name, phone_number, email, requested_at FROM PendingRegistrations";

        try (Connection conn = DbConn.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PendingJoinRequest request = new PendingJoinRequest(
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getTimestamp("requested_at")
                );
                pendingRequests.add(request);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingRequests;
    }
	// Method to accept the pending join request
    public void acceptRequest(PendingJoinRequest request) {
        // SQL to retrieve full details including address and password
        String sqlSelect = "SELECT address, password FROM PendingRegistrations WHERE email = ?";
        String sqlInsert = "INSERT INTO Users (full_name, email, address, phone_number, password) VALUES (?, ?, ?, ?, ?)";
        String sqlDelete = "DELETE FROM PendingRegistrations WHERE email = ?";

        try (Connection conn = DbConn.getInstance().getConnection();
             PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
             PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {

            // Retrieve address and password from PendingRegistrations
            pstmtSelect.setString(1, request.getEmail());
            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    String address = rs.getString("address");
                    String password = rs.getString("password");

                    // Insert into Users table
                    pstmtInsert.setString(1, request.getName());
                    pstmtInsert.setString(2, request.getEmail());
                    pstmtInsert.setString(3, address);  // Use the retrieved address
                    pstmtInsert.setString(4, request.getPhoneNumber());
                    pstmtInsert.setString(5, password);  // Use the retrieved password

                    pstmtInsert.executeUpdate();
                }
            }

            // Delete from PendingRegistrations
            pstmtDelete.setString(1, request.getEmail());
            pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	 // Method to decline the pending join request
    public void declineRequest(PendingJoinRequest request) {
        String sql = "DELETE FROM PendingRegistrations WHERE email = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizzy", "root", "2004");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ObservableList<Question> fetchFilteredQuestions(String subject, String difficulty) {
        ObservableList<Question> questionsList = FXCollections.observableArrayList();

        String query = "SELECT q.question_id, q.question_text, q.question_type, q.correct_answer, q.difficulty_level, q.subject, " +
                       "o.option_A, o.option_B, o.option_C, o.option_D " +
                       "FROM Questions q " +
                       "LEFT JOIN QuestionOptions o ON q.question_id = o.question_id " +
                       "WHERE q.subject = ? AND q.difficulty_level = ?";
        
        try (Connection conn = DbConn.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subject);
            stmt.setString(2, difficulty);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("question_id");
                String text = rs.getString("question_text");
                String type = rs.getString("question_type");
                String answer = rs.getString("correct_answer");
                String difficultyLevel = rs.getString("difficulty_level");
                String subjectName = rs.getString("subject");

                // Option columns for MCQs
                String optionA = rs.getString("option_A");
                String optionB = rs.getString("option_B");
                String optionC = rs.getString("option_C");
                String optionD = rs.getString("option_D");

                // For MCQ questions, collect options
                Map<String, String> options = null;
                if ("MCQ".equalsIgnoreCase(type)) {
                    options = new HashMap<>();
                    options.put("A", optionA);
                    options.put("B", optionB);
                    options.put("C", optionC);
                    options.put("D", optionD);
                }

                // Use the QuestionFactory to create the appropriate question object
                Question question = QuestionFactory.createQuestion(id, text, subjectName, difficultyLevel, type, answer, options);
                questionsList.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questionsList;
    }

    public static void deleteQuestionFromDatabase(Question question) {
        Connection conn = null;
        PreparedStatement deleteOptionsStmt = null;
        PreparedStatement deleteQuestionStmt = null;

        try {
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                System.out.println("Database connection failed.");
                return;
            }

            // Disable auto-commit for transaction
            conn.setAutoCommit(false);

            // Step 1: Delete from questionOptions table first
            String deleteOptionsQuery = "DELETE FROM questionoptions WHERE question_id = ?";
            deleteOptionsStmt = conn.prepareStatement(deleteOptionsQuery);
            deleteOptionsStmt.setInt(1, question.getQuestionId());
            int optionsDeleted = deleteOptionsStmt.executeUpdate();
            System.out.println("Rows affected in QuestionOptions table: " + optionsDeleted);

            // Step 2: Delete from questions table
            String deleteQuestionQuery = "DELETE FROM questions WHERE question_id = ?";
            deleteQuestionStmt = conn.prepareStatement(deleteQuestionQuery);
            deleteQuestionStmt.setInt(1, question.getQuestionId());
            int questionsDeleted = deleteQuestionStmt.executeUpdate();
            System.out.println("Rows affected in Questions table: " + questionsDeleted);

            // If deletion was successful, commit the transaction
            if (questionsDeleted > 0) {
                conn.commit();
                System.out.println("Question and related options deleted successfully.");
            } else {
                conn.rollback(); // Rollback if no questions were deleted
                System.out.println("Question delete process failed. No question found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (deleteOptionsStmt != null) deleteOptionsStmt.close();
                if (deleteQuestionStmt != null) deleteQuestionStmt.close();
                if (conn != null) conn.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void updateQuestionInDatabase(Question question) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get a connection from DbConn
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                System.out.println("Database connection failed.");
                return;
            }

            // SQL query to update the question details
            String updateQuestionSQL = "UPDATE Questions SET question_text = ?, question_type = ?, correct_answer = ? WHERE question_id = ?";
            stmt = conn.prepareStatement(updateQuestionSQL);
            stmt.setString(1, question.getQuestionText());
            stmt.setString(2, question.getQuestionType());
            stmt.setString(3, question.getCorrectAnswer());
            stmt.setInt(4, question.getQuestionId());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected in Questions table: " + rowsAffected);

            if (rowsAffected > 0) {
                System.out.println("Question updated successfully.");
            } else {
                System.out.println("Question update failed.");
            }

            // If the question is a Multiple Choice, update the options as well
            if (question instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
                System.out.println("Options: " + mcq.getOptions());

                updateQuestionOptions(mcq);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();  // Close statement
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close();  // Close connection
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Handle closing exceptions
            }
        }
    }

    private static void updateQuestionOptions(MultipleChoiceQuestion mcq) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Get a connection from DbConn
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                System.out.println("Database connection failed.");
                return;
            }

            // SQL query to update the options for the MultipleChoiceQuestion
            String updateOptionsSQL = "UPDATE QuestionOptions SET option_A = ?, option_B = ?, option_C = ?, option_D = ? WHERE question_id = ?";
            stmt = conn.prepareStatement(updateOptionsSQL);
            stmt.setString(1, mcq.getOptions().get("A"));
            stmt.setString(2, mcq.getOptions().get("B"));
            stmt.setString(3, mcq.getOptions().get("C"));
            stmt.setString(4, mcq.getOptions().get("D"));
            stmt.setInt(5, mcq.getQuestionId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Question options updated successfully.");
            } else {
                System.out.println("Question options update failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();  // Close statement
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close();  // Close connection
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Handle closing exceptions
            }
        }
    }
    
    public static void addQuestionToDatabase(Question newQuestion) {
        String insertQuestionQuery = "INSERT INTO Questions (question_text, question_type, correct_answer, difficulty_level, subject) VALUES (?, ?, ?, ?, ?)";
        String insertOptionQuery = "INSERT INTO QuestionOptions (question_id, option_A, option_B, option_C, option_D) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConn.getInstance().getConnection()) {
            // Begin a transaction to ensure both inserts are completed together
            conn.setAutoCommit(false);

            // Insert into Questions table
            try (PreparedStatement questionStmt = conn.prepareStatement(insertQuestionQuery, Statement.RETURN_GENERATED_KEYS)) {
                questionStmt.setString(1, newQuestion.getQuestionText());
                questionStmt.setString(2, newQuestion.getQuestionType());
                questionStmt.setString(3, newQuestion.getCorrectAnswer());
                questionStmt.setString(4, newQuestion.getDifficultyLevel());
                questionStmt.setString(5, newQuestion.getSubject());

                int rowsAffected = questionStmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Inserting question failed, no rows affected.");
                }

                // Get the generated question ID
                ResultSet generatedKeys = questionStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int questionId = generatedKeys.getInt(1);  // Retrieve the generated question ID

                    // If the question is an MCQ, insert the options
                    if (newQuestion instanceof MultipleChoiceQuestion) {
                        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) newQuestion;

                        try (PreparedStatement optionsStmt = conn.prepareStatement(insertOptionQuery)) {
                            optionsStmt.setInt(1, questionId);  // Use the generated question ID
                            optionsStmt.setString(2, mcq.getOptions().get("A"));
                            optionsStmt.setString(3, mcq.getOptions().get("B"));
                            optionsStmt.setString(4, mcq.getOptions().get("C"));
                            optionsStmt.setString(5, mcq.getOptions().get("D"));

                            optionsStmt.executeUpdate();
                        }
                    }

                    // Commit the transaction
                    conn.commit();
                    System.out.println("Question and options inserted successfully.");
                } else {
                    conn.rollback();
                    System.out.println("Failed to insert question, rolling back.");
                }

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                System.out.println("Error inserting question into database.");
            } finally {
                conn.setAutoCommit(true);  // Restore auto-commit mode
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error connecting to database.");
        }
    }


    public static boolean uploadStudyMaterial(String title, String description, File file) {
        String insertSQL = "INSERT INTO StudyMaterials (title, description, file_data, file_type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DbConn.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL);
             FileInputStream fileInputStream = new FileInputStream(file)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setBinaryStream(3, fileInputStream, (int) file.length()); // Binary data
            stmt.setString(4, getFileExtension(file)); // File type

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Study material uploaded successfully.");
                return true;
            } else {
                System.out.println("Failed to upload study material.");
                return false;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false; // Ensure method returns false on error
        }
    }
    
  //This method helps to configure file extension dynamically.  
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex + 1);
    }
    public static List<StudyMaterial> getAllStudyMaterials() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<StudyMaterial> materials = new ArrayList<>();

        String query = "SELECT material_id, title, description, file_type, uploaded_at FROM StudyMaterials";

        try {
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                System.out.println("Database connection failed.");
                return materials;
            }

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                materials.add(new StudyMaterial(
                    rs.getInt("material_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("file_type"),
                    rs.getTimestamp("uploaded_at")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return materials;
    }
    
    
 //method to deleteStudyMaterials from database   
    public static boolean deleteStudyMaterial(int materialId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        String deleteSQL = "DELETE FROM StudyMaterials WHERE material_id = ?";

        try {
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                System.out.println("Database connection failed.");
                return false;
            }

            stmt = conn.prepareStatement(deleteSQL);
            stmt.setInt(1, materialId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Study material deleted successfully.");
                return true;
            } else {
                System.out.println("No material found with the given ID.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }







}

    

