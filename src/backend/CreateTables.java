package backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {
	public static boolean createPendingRegistrationsTable() {
	    // SQL query to create PendingRegistrations table
	    String createTableSQL = "CREATE TABLE IF NOT EXISTS PendingRegistrations ("
	        + "request_id INT AUTO_INCREMENT PRIMARY KEY, "
	        + "full_name VARCHAR(255) NOT NULL, "
	        + "email VARCHAR(255) UNIQUE NOT NULL, "
	        + "address TEXT NOT NULL, "
	        + "phone_number VARCHAR(20) UNIQUE NOT NULL, "
	        + "password VARCHAR(255) NOT NULL, "
	        + "requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
	        + ")";

	    Connection conn = null;
	    Statement stmt = null;

	    try {
	        // Get a connection from DbConn
	        conn = DbConn.getInstance().getConnection();
	        if (conn == null) {
	            return false;  // No connection available
	        }

	        // Create the statement
	        stmt = conn.createStatement();

	        // Execute the SQL statement to create the table
	        stmt.executeUpdate(createTableSQL);
	        System.out.println("PendingRegistrations table created successfully (if it doesn't already exist).");
	        return true;  // Table created successfully

	    } catch (SQLException e) {
	        e.printStackTrace();  // Log any SQL exceptions
	        return false;  // Table creation failed
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

    public static boolean createUsersTable() {
        // SQL query to create Users table
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Users ("
            + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
            + "full_name VARCHAR(255) NOT NULL, "
            + "email VARCHAR(255) UNIQUE NOT NULL, "
            + "address TEXT NOT NULL, "
            + "phone_number VARCHAR(20) UNIQUE NOT NULL, "
            + "password VARCHAR(255) NOT NULL, "
            + "registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")";
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Get a connection from DbConn
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                return false;  // No connection available
            }

            // Create the statement
            stmt = conn.createStatement();
            
            // Execute the SQL statement
            stmt.executeUpdate(createTableSQL);
            System.out.println("Users table created successfully (if it doesn't already exist).");
            return true;  // Table created successfully

        } catch (SQLException e) {
            e.printStackTrace();  // Log any SQL exceptions
            return false;  // Table creation failed
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
    public static boolean createQuestionsTable() {
        // SQL query to create Questions table
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Questions ("
            + "question_id INT AUTO_INCREMENT PRIMARY KEY, "
            + "question_text TEXT NOT NULL, "
            + "question_type ENUM('MCQ', 'True/False') NOT NULL, "
            + "correct_answer VARCHAR(255) NOT NULL, "
            + "difficulty_level ENUM('Easy', 'Medium', 'Hard') NOT NULL, "
            + "subject ENUM('G.K', 'Science', 'Math', 'English') NOT NULL"
            + ")";
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Get a connection from DbConn
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                return false;  // No connection available
            }

            // Create the statement
            stmt = conn.createStatement();
            
            // Execute the SQL statement
            stmt.executeUpdate(createTableSQL);
            System.out.println("Questions table created successfully (if it doesn't already exist).");
            return true;  // Table created successfully

        } catch (SQLException e) {
            e.printStackTrace();  // Log any SQL exceptions
            return false;  // Table creation failed
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
        public static boolean createQuestionOptionsTable() {
            // SQL query to create QuestionOptions table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS QuestionOptions ("
                + "option_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "question_id INT NOT NULL, "
                + "option_A TEXT NOT NULL, "
                + "option_B TEXT NOT NULL, "
                + "option_C TEXT NOT NULL, "
                + "option_D TEXT NOT NULL, "
                + "FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE"
                + ")";

            Connection conn = null;
            Statement stmt = null;

            try {
                // Get a connection from DbConn
                conn = DbConn.getInstance().getConnection();
                if (conn == null) {
                    return false;  // No connection available
                }

                // Create the statement
                stmt = conn.createStatement();

                // Execute the SQL statement to create the table
                stmt.executeUpdate(createTableSQL);
                System.out.println("QuestionOptions table created successfully (if it doesn't already exist).");
                return true;  // Table created successfully

            } catch (SQLException e) {
                e.printStackTrace();  // Log any SQL exceptions
                return false;  // Table creation failed
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
        public static boolean createStudyMaterialsTable() {
            // SQL query to create StudyMaterials table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS StudyMaterials ("
                + "material_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(255) NOT NULL, "
                + "description TEXT, "
                + "file_data LONGBLOB NOT NULL, "  // Store the actual file content here
                + "file_type VARCHAR(50), "       // Optionally store the file type (e.g., pdf, doc)
                + "uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

            Connection conn = null;
            Statement stmt = null;

            try {
                // Get a connection from DbConn
                conn = DbConn.getInstance().getConnection();
                if (conn == null) {
                    return false;  // No connection available
                }

                // Create the statement
                stmt = conn.createStatement();
                
                // Execute the SQL statement
                stmt.executeUpdate(createTableSQL);
                System.out.println("StudyMaterials table created successfully (if it doesn't already exist).");
                return true;  // Table created successfully

            } catch (SQLException e) {
                e.printStackTrace();  // Log any SQL exceptions
                return false;  // Table creation failed
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



    public static void createTables(String[] args) {
    	createUsersTable();
    	createPendingRegistrationsTable();
    	createQuestionsTable();
    	createQuestionOptionsTable();
    	createStudyMaterialsTable();    
    	}
}
