package ui_designs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.LoginService;

public class Login extends Application {

    private LoginService loginService;

    @Override
    public void start(Stage primaryStage) {
        loginService = new LoginService();

        // Main container (HBox)
        HBox root = new HBox();
        root.setPrefSize(1000, 600);
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Left Panel (Form) with fixed width
        VBox leftPane = new VBox(20);
        leftPane.setId("left-pane");
        leftPane.setPrefWidth(400);  // Fixed width for the login form
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(40));

        // Right Panel (Image/Header) with dynamic resizing
        StackPane rightPane = new StackPane();
        rightPane.setId("right-pane");
        rightPane.setStyle("-fx-background-color: transparent;");

        // Gradient Background
        VBox gradientBackground = new VBox();
        gradientBackground.setStyle("-fx-background-color: linear-gradient(to bottom right, #6c5ce7, #4b3cad);");

        // Header Text
        VBox headerText = new VBox(10);
        Text appName = new Text("QUIZZY");
        appName.setFont(Font.font("Arial", 36));
        appName.setFill(Color.WHITE);
        Text slogan = new Text("Knowledge Platform");
        slogan.setFont(Font.font("Arial", 24));
        slogan.setFill(Color.WHITE);
        headerText.getChildren().addAll(appName, slogan);
        headerText.setAlignment(Pos.CENTER);

        rightPane.getChildren().addAll(gradientBackground, headerText);

        // Login Form Components
        Label loginHeader = new Label("Welcome Back!");
        loginHeader.getStyleClass().add("login-header");

        Label subHeader = new Label("Please login to continue");
        subHeader.getStyleClass().add("sub-header");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleTextField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleTextField(passwordField);

        Button loginButton = new Button("LOGIN");
        styleButton(loginButton);

        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign Up");
        signUpLink.getStyleClass().add("hyperlink");

        // Add an event handler to the Sign Up link to switch to the registration page using the service
        signUpLink.setOnAction(e -> loginService.openRegisterPage(primaryStage));

        // Form Layout
        VBox form = new VBox(15);
        form.setMaxWidth(300);
        form.getChildren().addAll(
                loginHeader, subHeader,
                createLabel("Email"), emailField,
                createLabel("Password"), passwordField,
                loginButton, signUpLink
        );

        leftPane.getChildren().add(form);
        root.getChildren().addAll(leftPane, rightPane);

        // Ensure only the rightPane is allowed to grow and fill available space
        HBox.setHgrow(leftPane, Priority.NEVER);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        // Scene Setup
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("Quizzy");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Login Button Action
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText(), primaryStage));
    }

 // Method to handle login authentication through the service
    private void handleLogin(String email, String password, Stage primaryStage) {
        // Middleware call to authenticate the user and get the role and email
        String[] authenticationResult = loginService.authenticateUser(email, password);

        if (authenticationResult != null) {
            String role = authenticationResult[0];  // Role is the first element in the array
            String userEmail = authenticationResult[1];  // Email is the second element

            // Let the middleware decide what to do based on the role and email
            loginService.handlePageTransition(role, primaryStage, userEmail);
        } else {
            showError("Invalid email or password.");
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void styleTextField(Control field) {
        field.getStyleClass().add("dark-field");
        field.setPrefWidth(300);
        field.setPrefHeight(40);
    }

    private void styleButton(Button button) {
        button.getStyleClass().add("dark-button");
        button.setPrefWidth(300);
        button.setPrefHeight(40);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
