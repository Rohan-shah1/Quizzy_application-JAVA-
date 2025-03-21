package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import models.User;

public class LoginAndRegisterDAO {

    public boolean insertPendingRegistration(String fullName, String email, String address, String phoneNumber, String password) {
        Connection conn = null;
        try {
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                return false;
            }

            String query = "INSERT INTO PendingRegistrations (full_name, email, address, phone_number, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, address);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, password);
            pstmt.executeUpdate();

            return true; // Insert successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean verifyCredentials(String username, String password) {
        Connection conn = null;
        try {
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                return false;
            }

            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();  // If a result is found, credentials are valid
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to check user credentials from the database (case-sensitive)
    public static boolean authenticateUserFromDB(String email, String password) {
        boolean isValid = false;

        // Get the connection from the DbConn class
        try (Connection conn = DbConn.getInstance().getConnection()) {
            // Query to check if the user exists with the given email and password (case-sensitive)
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            
            ResultSet resultSet = statement.executeQuery();
            
            // If a result is found, the user is valid
            if (resultSet.next()) {
                isValid = true;
            }
            
            // Close the result set and statement (though using try-with-resources, they will be auto-closed)
            resultSet.close();
            statement.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }
    // Method to get a user by email (for further use if needed)
    public static User getUserByEmail(String email) {
        User user = null;
        Connection conn = null;

        try {
            conn = DbConn.getInstance().getConnection();
            if (conn == null) {
                return null;  // No connection available
            }

            // Query to fetch user details by email
            String query = "SELECT * FROM Users WHERE email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            
            ResultSet resultSet = statement.executeQuery();
            
            // If user is found, create a User object and populate it
            if (resultSet.next()) {
                user = new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("email"),
                    resultSet.getString("address"),
                    resultSet.getString("phone_number"),
                    resultSet.getString("password"),
                    resultSet.getTimestamp("registered_at")
                );
            }

            // Close resources (resultSet, statement)
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close(); // Ensure connection is closed
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user; // Return the user object or null if not found
    }
    

    
}

