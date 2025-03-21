package services;

import ui_designs.AdminDashboard;
import ui_designs.UserDashboard;
import ui_designs.Register;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.QuizSubject;
import models.User;
import backend.LoginAndRegisterDAO;

public class LoginService {

    // Method to authenticate the user (admin logic is hardcoded, user logic is fetched from DB)
    public String[] authenticateUser(String email, String password) {
        // Admin authentication (hardcoded admin email and password)
        if (email.equals("admin@gmail.com") && password.equals("password")) {
            return new String[] {"admin", email};  // Return admin role and email
        }

        // User authentication (fetch from DB)
        User user = LoginAndRegisterDAO.getUserByEmail(email);  // Fetch user by email from DB
        if (user != null && user.getPassword().equals(password)) {
            return new String[] {"user", email};  // Return user role and email
        }

        return null;  // Invalid credentials
    }

    // Method to handle page transitions based on user role
    public void handlePageTransition(String role, Stage primaryStage, String email) {
        if (role.equals("admin")) {
            // Transition to Admin Dashboard
            AdminDashboard adminDashboard = new AdminDashboard();
            primaryStage.setScene(adminDashboard.createAdminDashboardScene());
        } else if (role.equals("user")) {
            // Fetch the user details from the database based on email
            User user = LoginAndRegisterDAO.getUserByEmail(email);

            // If user is valid, transition to User Dashboard
            if (user != null) {
                // Create or fetch the QuizSubject instance as needed
                QuizSubject quizSubject = new QuizSubject();  // This can be dynamically created or fetched

                // Transition to the User Dashboard and pass the user and quizSubject to the constructor
                UserDashboard userDashboard = new UserDashboard(user, quizSubject);
                primaryStage.setScene(userDashboard.createUserDashboardScene());
            } else {
                showError("User not found.");
            }
        }
        primaryStage.show();
    }

    // Method to open the registration page
    public void openRegisterPage(Stage primaryStage) {
        Register registerPage = new Register(primaryStage);  // Pass the primaryStage to register
        Scene registerScene = registerPage.createRegisterScene();  // Create the register scene
        primaryStage.setScene(registerScene);  // Switch to registration page scene
        primaryStage.show();
    }

    private void showError(String message) {
        // Display an error message to the user (you can implement this in a custom way)
        System.out.println("Error: " + message);
    }
}
