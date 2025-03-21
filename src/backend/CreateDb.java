package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDb {

//Database connection details
private static final String URL = "jdbc:mysql://localhost:3306/"; // MySQL URL (don't specify a database yet)
private static final String USER = "root"; // Your MySQL username
private static final String PASSWORD = "2004"; // Your MySQL password

public static void createQuizzyDb() {
    // SQL query to create the SchoolDB database
    String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS quizzy";

    try {
        // Step 1: Establish a connection to MySQL (without specifying a database)
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        // Step 2: Create a Statement object to execute the SQL query
        Statement statement = connection.createStatement();

        // Step 3: Execute the SQL query to create the database
        statement.executeUpdate(createDatabaseQuery);
        System.out.println("Database 'quizzy' created successfully or already exists.");

        // Step 4: Close the statement and connection
        statement.close();
        connection.close();
    } catch (SQLException e) {
        System.out.println("Error creating database: " + e.getMessage());
    }
	}
}
