package services;

import backend.DbConn;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import backend.LoginAndRegisterDAO;
public class RegistrationService {

    // Register user with validation
    public boolean registerUser(String fullName, String email, String address, String phoneNumber, String password) {
        // Validate input
        String errorMessage = validateInput(fullName, email, address, phoneNumber, password);
        if (errorMessage != null) {
            showError(errorMessage);
            return false; // Stop registration if validation fails
        }

        // Database connection
        try (Connection conn = DbConn.getInstance().getConnection()) {
            if (conn == null) {
                showError("Database connection failed. Please try again later.");
                return false;
            }

            String query = "INSERT INTO PendingRegistrations (full_name, email, address, phone_number, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fullName);
                pstmt.setString(2, email);
                pstmt.setString(3, address);
                pstmt.setString(4, phoneNumber);
                pstmt.setString(5, password);
                pstmt.executeUpdate();
                showMessage("Registration request sent for admin approval!");
                return true; // Success
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error registering user! Please try again later.");
            return false;
        }
    }
    

    // Validate user input and return a single error message if any validation fails
    private String validateInput(String fullName, String email, String address, String phoneNumber, String password) {
        if (fullName.isEmpty() || email.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            return "All fields are required!";
        }

        if (!email.endsWith("@gmail.com")) {
            return "Invalid email! Email must end with @gmail.com.";
        }

        if (!phoneNumber.matches("\\d{10}")) { // Exactly 10 digits
            return "Invalid phone number! It must be exactly 10 digits.";
        }

        return null; // If all checks pass
    }

    // Show error message in an alert box
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Registration Error");
        alert.setHeaderText("Invalid Input");
        alert.showAndWait();
    }

    // Show success message in an alert box
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.setHeaderText("Registration Successful");
        alert.showAndWait();
    }
    

}