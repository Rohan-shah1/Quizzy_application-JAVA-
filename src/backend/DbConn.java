package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {
    private static volatile DbConn instance = null;
    private static Connection connection = null;  // Single connection to be reused
    
    private static final String URL = "jdbc:mysql://localhost:3306/quizzy";
    private static final String USER = "root";
    private static final String PASSWORD = "2004"; // Change this to your MySQL password

    private DbConn() {
        // Private constructor to prevent instantiation
    }

    public static DbConn getInstance() {
        if (instance == null) {
            synchronized (DbConn.class) {
                if (instance == null) {
                    instance = new DbConn();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        // If connection is null or closed, create a new one
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL Driver
                connection = DriverManager.getConnection(URL, USER, PASSWORD); // Create database connection
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Optionally, you can add a method to close the connection explicitly
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close(); // Close the connection
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
